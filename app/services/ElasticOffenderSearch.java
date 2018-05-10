package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.FutureListener;
import helpers.JwtHelper;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import play.Logger;
import play.libs.Json;
import services.helpers.OffenderSorter;
import services.helpers.SearchResultPipeline;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import static helpers.FluentHelper.not;
import static helpers.JsonHelper.toBoolean;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static play.libs.Json.parse;
import static services.helpers.SearchQueryBuilder.searchSourceFor;

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

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey);
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return elasticSearchClient.ping();
            } catch (IOException e) {
                Logger.error("Got an error calling ElasticSearch health endpoint", e);
                return false;
            }
        });
    }

    @Override
    public CompletionStage<Map<String, Object>> search(String bearerToken, List<String> probationAreasFilter, String searchTerm, int pageSize, int pageNumber) {

        final Function<List<ObjectNode>, CompletableFuture[]> restrictResults = results -> results.stream().map(resultNode -> {

            val offenderId = resultNode.get("offenderId").asLong();
            val restricted = toBoolean(resultNode, "currentExclusion") || toBoolean(resultNode, "currentRestriction");

            val accessCheck = restricted ? offenderApi.canAccess(bearerToken, offenderId) : CompletableFuture.completedFuture(true);

            return accessCheck.thenApply(canAccess -> canAccess ? resultNode : restrictedView(resultNode));

        }).map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new);

        final Function<SearchResponse, CompletionStage<Map<String, Object>>> processResponse = response -> {

            logResults(response);

            final List<ObjectNode> embellishedNodes = stream(response.getHits().getHits()).map(searchHit -> {

                val pipeline = SearchResultPipeline.create(
                        encrypter,
                        bearerToken,
                        searchTerm,
                        searchHit.getHighlightFields()
                );

                return SearchResultPipeline.process((ObjectNode) parse(searchHit.getSourceAsString()), pipeline.values());

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
                searchSourceFor(searchTerm, probationAreasFilter, pageSize, pageNumber)
        );

        elasticSearchClient.searchAsync(request, listener);

        return listener.stage().thenComposeAsync(processResponse);
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
