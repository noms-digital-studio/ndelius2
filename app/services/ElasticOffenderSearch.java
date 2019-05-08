package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.CourtDefendant;
import data.MatchedOffenders;
import helpers.Encryption;
import helpers.FutureListener;
import helpers.JwtHelper;
import interfaces.HealthCheckResult;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import play.Logger;
import play.libs.Json;
import services.helpers.OffenderSorter;
import services.helpers.SearchQueryBuilder.*;
import services.helpers.SearchResultPipeline;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static helpers.FluentHelper.not;
import static helpers.JsonHelper.toBoolean;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static play.libs.Json.parse;
import static services.helpers.SearchQueryBuilder.*;

public class ElasticOffenderSearch implements OffenderSearch {

    private static final String CENTRAL_TEAM_CODE = "N40";
    private final OffenderApi offenderApi;
    private final RestHighLevelClient elasticSearchClient;
    private final Function<String, String> encrypter;

    @Inject
    public ElasticOffenderSearch(Config configuration, RestHighLevelClient elasticSearchClient, OffenderApi offenderApi) {
        this.elasticSearchClient = elasticSearchClient;
        this.offenderApi = offenderApi;

        val paramsSecretKey = configuration.getString("params.secret.key");

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey).orElseThrow(() -> new RuntimeException("Encrypt failed"));
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new HealthCheckResult(elasticSearchClient.ping());
            } catch (IOException e) {
                Logger.error("Got an error calling ElasticSearch health endpoint", e);
                return HealthCheckResult.unhealthy(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public CompletionStage<Map<String, Object>> search(String bearerToken, List<String> probationAreasFilter, String searchTerm, int pageSize, int pageNumber, QUERY_TYPE queryType) {

        val restrictResults = checkOffenderRestrictions(bearerToken);

        final Function<SearchResponse, CompletionStage<Map<String, Object>>> processResponse = response -> {

            logResults(response);

            final List<ObjectNode> embellishedNodes = stream(response.getHits().getHits()).map(searchHit -> {

                val pipeline = SearchResultPipeline.create(
                        encrypter,
                        bearerToken,
                        searchTerm,
                        searchHit.getHighlightFields()
                );

                return SearchResultPipeline.process(toObjectNode(searchHit), pipeline.values());

            }).collect(Collectors.toList());

            val processingResults = restrictResults.apply(OffenderSorter.groupByNameAndSortByCurrentDisposal(embellishedNodes));

            return CompletableFuture.allOf(processingResults).thenApply(ignoredVoid -> {

                final List completeResults = Arrays.stream(processingResults).map(result -> result.toCompletableFuture().join()).collect(toList());

                return ImmutableMap.of(
                        "offenders", completeResults,
                        "total", response.getHits().getTotalHits(),
                        "suggestions", Optional.ofNullable(response.getSuggest()).map(suggest -> parse(suggest.toString())).orElse(parse("{}"))
                );
            }).thenCombine(

                offenderApi.probationAreaDescriptions(bearerToken, toProbationAreaCodeList(response))
                        .thenApply(probationAreaDescriptions -> ImmutableMap.of("byProbationArea",
                            Optional.ofNullable(response.getAggregations())
                                    .map(this::extractProbationAreaCodeToCountMap)
                                    .map(probationAreas -> probationAreas
                                            .stream()
                                            .filter(not(probationArea -> isCentralProjectsTeam(probationArea) && doesNotHaveCentralProjectsTeamInProfile(bearerToken)))
                                            .map(addDescription(probationAreaDescriptions))
                                            .collect(toList()))
                                    .map(Json::toJson)
                                    .orElseGet(Json::newArray))),

                    (result, aggregations) -> ImmutableMap
                            .<String, Object>builder()
                            .putAll(result)
                            .put("aggregations", aggregations).build());
        };

        val listener = new FutureListener<SearchResponse>();
        val request = new SearchRequest("offender").preference(bearerToken).source(
            searchSourceFor(searchTerm,
                            probationAreasFilter,
                            pageSize,
                            pageNumber,
                            queryType)
        );

        elasticSearchClient.searchAsync(request, listener);

        return listener.stage().thenComposeAsync(processResponse);
    }

    /**
     * This will attempt to match a defendant in court with an offender known to Probation.
     * This implementation users a hammer approach of doing multiple searches using different
     * requests. It should be possible to collapse these requests and inspect the results for
     * what matched to work out what our confidence level is; e.g combine both variations on the
     * PNC Number search in to a single search. For this spike we have gone for the less performant
     * explicit approach since it will be easier it iterate on the algorithm.
     */
    @Override
    public CompletionStage<MatchedOffenders> findMatch(String bearerToken, CourtDefendant offender) {
        val maybePNCSurnameRequest = pncSurnameSearchRequest(offender);
        val maybeNameDateOfBirthRequest = nameDateOfBirthRequest(offender);

        final Function<SearchResponse, CompletionStage<MatchedOffenders>> processNameDateOfBirthVariationsResponse = response -> {
            if (noMatches(response)) {
                return CompletableFuture.completedFuture(MatchedOffenders.noMatch());
            } else if (duplicateMatches(response)) {
                return allDuplicates(bearerToken, response).thenApply(MatchedOffenders::duplicateLowConfidence);
            }
            return singleOffender(bearerToken, response).thenApply(MatchedOffenders::mediumConfidence);
        };

        final Function<SearchResponse, CompletionStage<MatchedOffenders>> processNameDateOfBirthResponse = response -> {
            val listener = new FutureListener<SearchResponse>();
            if (noMatches(response)) {
                elasticSearchClient.searchAsync(nameDateOfBirthVariationsRequest(offender), listener);
                return listener.stage().thenComposeAsync(processNameDateOfBirthVariationsResponse);
            } else if (duplicateMatches(response)) {
                return clearAndObviousActiveDuplicate(bearerToken, response)
                        .thenApply(maybeOffenderNode -> maybeOffenderNode
                                .map(MatchedOffenders::mediumConfidence)
                                .orElseGet(() -> MatchedOffenders.duplicateHighConfidence(toOffenderObjectNodes(response))));
            }

            return singleOffender(bearerToken, response).thenApply(MatchedOffenders::highConfidence);
        };

        final Function<SearchResponse, CompletionStage<MatchedOffenders>> processPNCOnlyResponse = response -> {
            val listener = new FutureListener<SearchResponse>();
            if (noMatches(response)) {
                return maybeNameDateOfBirthRequest.map(request -> {
                    elasticSearchClient.searchAsync(request, listener);
                    return listener.stage().thenComposeAsync(processNameDateOfBirthResponse);
                }).orElse(CompletableFuture.completedFuture(MatchedOffenders.noMatch()));
            } else if (duplicateMatches(response)) {
                return clearAndObviousActiveDuplicate(bearerToken, response)
                        .thenApply(maybeOffenderNode -> maybeOffenderNode
                                .map(MatchedOffenders::mediumConfidence)
                                .orElseGet(() -> MatchedOffenders.duplicateMediumConfidence(toOffenderObjectNodes(response))));
            }

            return singleOffender(bearerToken, response).thenApply(MatchedOffenders::mediumConfidence);
        };

        final Function<SearchResponse, CompletionStage<MatchedOffenders>> processPNCSurnameResponse = response -> {
            val listener = new FutureListener<SearchResponse>();
            if (noMatches(response)) {
                elasticSearchClient.searchAsync(pncOnlySearchRequest(offender), listener);
                return listener.stage().thenComposeAsync(processPNCOnlyResponse);
            } else if (duplicateMatches(response)) {
                return clearAndObviousActiveDuplicate(bearerToken, response)
                        .thenApply(maybeOffenderNode -> maybeOffenderNode
                                .map(MatchedOffenders::highConfidence)
                                .orElseGet(() -> MatchedOffenders.duplicateVeryHighConfidence(toOffenderObjectNodes(response))));
            }

            return singleOffender(bearerToken, response).thenApply(MatchedOffenders::veryHighConfidence);
        };


        return maybePNCSurnameRequest.map(request -> {
            val listener = new FutureListener<SearchResponse>();
            elasticSearchClient.searchAsync(request, listener);
            return listener.stage().thenComposeAsync(processPNCSurnameResponse);
        }).orElseGet(() -> maybeNameDateOfBirthRequest.map(request -> {
            val listener = new FutureListener<SearchResponse>();
            elasticSearchClient.searchAsync(request, listener);
            return listener.stage().thenComposeAsync(processNameDateOfBirthResponse);
        }).orElse(CompletableFuture.completedFuture(MatchedOffenders.noMatch())));

    }

    private boolean duplicateMatches(SearchResponse response) {
        return response.getHits().totalHits > 1;
    }

    private boolean noMatches(SearchResponse response) {
        return response.getHits().totalHits == 0;
    }

    private CompletionStage<ObjectNode> singleOffender(String bearerToken, SearchResponse response) {
        return restrictViewOfAnyRestrictedOffenders(bearerToken, toOffenderObjectNodes(response)).thenApply(allNodes -> allNodes.get(0));
    }

    private CompletionStage<List<ObjectNode>> allDuplicates(String bearerToken, SearchResponse response) {
        return restrictViewOfAnyRestrictedOffenders(bearerToken, toOffenderObjectNodes(response));
    }

    private CompletionStage<List<ObjectNode>> restrictViewOfAnyRestrictedOffenders(String bearerToken, List<ObjectNode> offenderNodes) {
        val processingResults = checkOffenderRestrictions(bearerToken).apply(offenderNodes);

        return CompletableFuture.allOf(processingResults).thenApply(ignoredVoid -> Arrays.stream(processingResults).map(result -> (ObjectNode)result.toCompletableFuture().join()).collect(toList()));
    }

    private Function<List<ObjectNode>, CompletableFuture[]> checkOffenderRestrictions(String bearerToken) {
        return results -> results.stream().map(resultNode -> {

            val offenderId = resultNode.get("offenderId").asLong();
            val restricted = toBoolean(resultNode, "currentExclusion") || toBoolean(resultNode, "currentRestriction");

            val accessCheck = restricted ? offenderApi.canAccess(bearerToken, offenderId) : CompletableFuture.completedFuture(true);

            return accessCheck.thenApply(canAccess -> canAccess ? resultNode : restrictedView(resultNode));

        }).map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new);
    }

    private List<ObjectNode> toOffenderObjectNodes(SearchResponse response) {
        return Arrays.stream(response.getHits().getHits()).map(this::toObjectNode).collect(toList());
    }

    private CompletionStage<Optional<ObjectNode>> clearAndObviousActiveDuplicate(String bearerToken, SearchResponse response) {
        final Function<List<ObjectNode>, CompletableFuture[]> resultsWithEvents = results -> results.stream().map(resultNode -> {

            val offenderId = resultNode.get("offenderId").asText();
            val convictions = offenderApi.getOffenderConvictionsByOffenderId(bearerToken, offenderId);

            return convictions.thenApply(convictionsNode -> {
               resultNode.set("convictions", convictionsNode);
               return resultNode;
            });

        }).map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new);


        CompletableFuture[] offendersWithConvictions = resultsWithEvents.apply(toOffenderObjectNodes(response));


        return CompletableFuture.allOf(offendersWithConvictions).thenApply(notUsed -> {
            val offenders = Arrays.stream(offendersWithConvictions).map(result -> (ObjectNode)result.toCompletableFuture().join()).collect(toList());

            val offendersWithActiveEvent = offenders
                    .stream()
                    .filter(node -> node.hasNonNull("convictions"))
                    .filter(this::hasActiveConviction)
                    .collect(toList());

            if (offendersWithActiveEvent.size() == 1) {
                return Optional.of(offendersWithActiveEvent.get(0));
            }

            val offendersWithMoreThanOneEvent = offenders
                    .stream()
                    .filter(node -> node.hasNonNull("convictions"))
                    .filter(node -> convictionsNode(node).size() > 1).collect(toList());

            if (offendersWithMoreThanOneEvent.size() == 1) {
                return Optional.of(offendersWithMoreThanOneEvent.get(0));
            }

            return Optional.empty();

        });

    }

    private ArrayNode convictionsNode(ObjectNode offenderNode) {
        return (ArrayNode)offenderNode.get("convictions");
    }

    private boolean hasActiveConviction(ObjectNode offenderNode) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(convictionsNode(offenderNode).elements(), Spliterator.ORDERED),false)
                .anyMatch(conviction -> conviction.get("active").asBoolean());
    }

    private ObjectNode toObjectNode(SearchHit hit) {
        return (ObjectNode) parse(hit.getSourceAsString());
    }


    private Optional<SearchRequest> pncSurnameSearchRequest(CourtDefendant offender) {
        return Optional.ofNullable(offender.getPncNumber())
                .filter(StringUtils::isNotBlank)
                .map(pncNumber -> new SearchRequest("offender").source(
                        searchSourceForPNCWithSurname(offender.getPncNumber(), offender.getSurname())));

    }

    private SearchRequest pncOnlySearchRequest(CourtDefendant offender) {
        return new SearchRequest("offender").source(
                searchSourceForPNC(offender.getPncNumber()));
    }
    private Optional<SearchRequest> nameDateOfBirthRequest(CourtDefendant offender) {
        return Optional.ofNullable(offender.getDateOfBirth())
                .map(dateOfBirth -> new SearchRequest("offender").source(
                        searchSourceForNameWithDateOfBirth(offender.getFirstName(), offender.getSurname(), dateOfBirth)));

    }

    private SearchRequest nameDateOfBirthVariationsRequest(CourtDefendant offender) {
        return new SearchRequest("offender").source(
                searchSourceForNameWithDateOfBirthVariations(offender.getFirstName(), offender.getSurname(), offender.getDateOfBirth()));

    }

    private boolean doesNotHaveCentralProjectsTeamInProfile(String bearerToken) {
        return !JwtHelper.probationAreaCodes(bearerToken).contains(CENTRAL_TEAM_CODE);
    }

    private boolean isCentralProjectsTeam(Map<String, Object> probationArea) {
        return probationArea.get("code").equals(CENTRAL_TEAM_CODE);
    }

    private Function<Map<String, Object>, Map<Object, Object>> addDescription(Map<String, String> probationAreaDescriptions) {
        return area -> ImmutableMap
                .builder()
                .putAll(area)
                .put("description", probationAreaDescription(probationAreaDescriptions, area.get("code").toString())).build();
    }

    private String probationAreaDescription(Map<String, String> probationAreaDescriptions, String code) {
        return Optional.ofNullable(probationAreaDescriptions.get(code)).orElse(code);
    }

    private List<String> toProbationAreaCodeList(SearchResponse response) {

        return Optional.ofNullable(response.getAggregations())
                .map(aggregations -> extractProbationAreaCodeToCountMap(aggregations)
                        .stream()
                        .map(probationAreaMap -> probationAreaMap.get("code").toString())
                        .collect(toList())).orElse(emptyList());
    }

    private List<Map<String, Object>> extractProbationAreaCodeToCountMap(Aggregations aggregations) {

        val offenderManagersAggregation = (Nested)aggregations.asMap().get("offenderManagers");
        val activeAggregation = (Terms)offenderManagersAggregation.getAggregations().asMap().get("active");
        val possibleActiveBucket = activeAggregation.getBucketByKey("true");


        return Optional.ofNullable(possibleActiveBucket).map (activeBucket -> {
            val possibleProbationCodeBuckets = (Terms)activeBucket.getAggregations().asMap().get("byProbationAreaCode");

            val buckets = Optional.ofNullable(possibleProbationCodeBuckets)
                    .map(probationCodeBuckets -> (List<Terms.Bucket>)probationCodeBuckets.getBuckets())
                    .orElse(emptyList());

            return buckets.stream().map(this::probationAreaToMap).collect(toList());
        }).orElse(emptyList());
    }

    private Map<String, Object> probationAreaToMap(Terms.Bucket bucket) {

        return ImmutableMap.of(
                "code", bucket.getKeyAsString(),
                "count", bucket.getDocCount());
    }
    private ObjectNode restrictedView(ObjectNode rootNode) {

        return (ObjectNode) Json.toJson(ImmutableMap.of(
            "accessDenied", true,
            "offenderId", rootNode.get("offenderId").asLong(),
            "otherIds", ImmutableMap.of("crn", rootNode.get("otherIds").get("crn").asText()),
            "offenderManagers", offenderManagers(rootNode)
        ));
    }

    private JsonNode offenderManagers(ObjectNode rootNode) {

        return Optional.ofNullable(rootNode.get("offenderManagers")).orElseGet(Json::newArray);
    }

    private void logResults(SearchResponse response) {

        Logger.debug(() -> {
            try {
                return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(parse(response.toString()));
            } catch (Exception e) {
                return response.toString();
            }
        });
    }

}
