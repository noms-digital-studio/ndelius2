package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import data.offendersearch.OffenderSearchResult;
import helpers.DateTimeHelper;
import helpers.FutureListener;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import play.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.search.suggest.SuggestBuilders.termSuggestion;
import static play.libs.Json.parse;

public class ElasticOffenderSearch implements OffenderSearch {

    private final RestHighLevelClient elasticSearchClient;
    private final OffenderApi offenderApi;

    @Inject
    public ElasticOffenderSearch(RestHighLevelClient elasticSearchClient, OffenderApi offenderApi) {
        this.elasticSearchClient = elasticSearchClient;
        this.offenderApi = offenderApi;
    }

    @Override
    public CompletionStage<OffenderSearchResult> search(String bearerToken, String searchTerm, int pageSize, int pageNumber) {
        val listener = new FutureListener<SearchResponse>();
        elasticSearchClient.searchAsync(new SearchRequest("offender")
            .source(searchSourceFor(searchTerm, pageSize, pageNumber)), listener);
        return listener.stage().thenComposeAsync(response -> processSearchResponse(bearerToken, response));
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return elasticSearchClient.ping();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                return false;
            }
        });
    }

    private SearchSourceBuilder searchSourceFor(String searchTerm, int pageSize, int pageNumber) {
        val boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutDatesIn(searchTerm))
            .field("firstName", 10)
            .field("surname", 10)
            .field("offenderAliases.firstName", 8)
            .field("offenderAliases.surname", 8)
            .field("contactDetails.addresses.town")
            .type(CROSS_FIELDS));

        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutDatesIn(searchTerm))
            .field("gender")
            .field("otherIds.crn", 10)
            .field("otherIds.nomsNumber", 10)
            .field("otherIds.niNumber", 10)
            .field("otherIds.pncNumber", 10)
            .field("otherIds.croNumber", 10)
            .field("contactDetails.addresses.streetName")
            .field("contactDetails.addresses.county")
            .field("contactDetails.addresses.postcode", 10)
            .type(MOST_FIELDS));

        termsThatLookLikeDatesIn(searchTerm).forEach(dateTerm ->
            boolQueryBuilder.should().add(multiMatchQuery(dateTerm)
                .field("dateOfBirth", 11)
                .lenient(true)));

        val searchSource = new SearchSourceBuilder()
            .query(boolQueryBuilder)
            .explain(Logger.isDebugEnabled())
            .size(pageSize)
            .from(pageSize * aValidPageNumberFor(pageNumber))
            .suggest(suggestionsFor(searchTerm));

        Logger.info(searchSource.toString());
        return searchSource;
    }

    private List<String> termsThatLookLikeDatesIn(String searchTerm) {
        val searchTerms = searchTerm.split(" ");
        return Stream.of(searchTerms)
            .filter(DateTimeHelper::canBeConvertedToADate)
            .map(term -> DateTimeHelper.covertToCanonicalDate(term).get())
            .collect(toList());
    }

    private String termsWithoutDatesIn(String searchTerm) {
        val searchTerms = searchTerm.split(" ");
        return Stream.of(searchTerms)
            .filter(term -> !DateTimeHelper.covertToCanonicalDate(term).isPresent())
            .collect(joining(" "));
    }

    private SuggestBuilder suggestionsFor(String searchTerm) {
        return new SuggestBuilder()
            .addSuggestion("surname", termSuggestion("surname").text(searchTerm))
            .addSuggestion("firstName", termSuggestion("firstName").text(searchTerm));
    }

    private CompletionStage<OffenderSearchResult> processSearchResponse(String bearerToken, SearchResponse response) {
        logResults(response);

        val offenderNodesCompletionStages = stream(response.getHits().getHits())
                .map(searchHit -> {
                    JsonNode offender = parse(searchHit.getSourceAsString());
                    return embellishNode(bearerToken, offender);
                }).collect(toList());

        return CompletableFuture.allOf(
                toCompletableFutureArray(offenderNodesCompletionStages))
                .thenApply(ignoredVoid ->
                        OffenderSearchResult.builder()
                                .offenders(offendersFromCompletionStages(offenderNodesCompletionStages))
                                .total(response.getHits().getTotalHits())
                                .suggestions(suggestionsIn(response))
                                .build());
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

    private CompletableFuture[] toCompletableFutureArray(List<CompletionStage<ObjectNode>> offenderNodesCompletionStages) {
        return offenderNodesCompletionStages
                .stream()
                .map(CompletionStage::toCompletableFuture)
                .toArray(CompletableFuture[]::new);
    }

    private List<JsonNode> offendersFromCompletionStages(List<CompletionStage<ObjectNode>> offenderNodes) {
        return offenderNodes
                .stream()
                .map(objectNodeCompletionStage -> objectNodeCompletionStage.toCompletableFuture().join())
                .collect(toList());
    }


    private JsonNode suggestionsIn(SearchResponse response) {
        return Optional.ofNullable(response.getSuggest())
            .map(suggest -> parse(suggest.toString()))
            .orElse(parse("{}"));
    }

    private int aValidPageNumberFor(int pageNumber) {
        return pageNumber >= 1 ? pageNumber - 1 : 0;
    }

    private CompletionStage<ObjectNode> embellishNode(String bearerToken, JsonNode node) {
        return restrictViewOfOffenderIfNecessary(
                bearerToken,
                appendDateOfBirth((ObjectNode)node));
    }

    private ObjectNode appendDateOfBirth(ObjectNode rootNode) {
        JsonNode dateOfBirth = rootNode.get("dateOfBirth");

        return Optional.ofNullable(dateOfBirth)
            .map(dob -> rootNode.put("age", calculateAge(dob.asText(), systemUTC())))
            .orElse(rootNode);
    }

    private CompletionStage<ObjectNode> restrictViewOfOffenderIfNecessary(String bearerToken, ObjectNode rootNode) {
        if (toBoolean(rootNode, "currentExclusion") || toBoolean(rootNode, "currentRestriction")) {
            return offenderApi.canAccess(bearerToken, rootNode.get("offenderId").asLong())
                    .thenApply(canAccess -> canAccess ? rootNode : restrictView(rootNode));
        }
        return CompletableFuture.completedFuture(rootNode);
    }

    private Boolean toBoolean(ObjectNode rootNode, String nodeName) {
        return Optional.ofNullable(rootNode.get(nodeName))
                .map(JsonNode::asBoolean).orElse(false);
    }

    private ObjectNode restrictView(ObjectNode rootNode) {
        final ObjectNode restrictedAccessRootNode = JsonNodeFactory.instance.objectNode();
        restrictedAccessRootNode
            .put("accessDenied", true)
            .put("offenderId", rootNode.get("offenderId").asLong())
            .set("otherIds", JsonNodeFactory.instance.objectNode().put("crn", rootNode.get("otherIds").get("crn").asText()));
        return restrictedAccessRootNode;
    }
}
