package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import data.offendersearch.OffenderSearchResult;
import helpers.Encryption;
import helpers.FutureListener;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import play.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.JsonHelper.toBoolean;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static play.libs.Json.parse;
import static services.helpers.CurrentDisposalAndNameComparator.currentDisposalAndNameComparator;
import static services.helpers.SearchQueryBuilder.searchSourceFor;
import static services.helpers.SearchResultAppenders.*;

public class ElasticOffenderSearch implements OffenderSearch {

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
    public CompletionStage<OffenderSearchResult> search(String bearerToken, String searchTerm, int pageSize, int pageNumber) {
        val listener = new FutureListener<SearchResponse>();
        elasticSearchClient.searchAsync(new SearchRequest("offender")
            .source(searchSourceFor(searchTerm, pageSize, pageNumber)), listener);
        return listener.stage().thenComposeAsync(response -> processSearchResponse(bearerToken, searchTerm, response));
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

    private CompletionStage<OffenderSearchResult> processSearchResponse(String bearerToken, String searchTerm, SearchResponse response) {
        logResults(response);

        val offenderNodesCompletionStages = stream(response.getHits().getHits())
                .sorted(currentDisposalAndNameComparator)
                .map(searchHit -> {
                    JsonNode offender = parse(searchHit.getSourceAsString());
                    return restrictAndEmbellishNode(bearerToken, searchTerm, offender, searchHit.getHighlightFields());
                })
                .collect(toList());

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

    private CompletionStage<ObjectNode> restrictAndEmbellishNode(String bearerToken, String searchTerm, JsonNode node, Map<String, HighlightField> highlightFields) {
        return restrictViewOfOffenderIfNecessary(
                bearerToken,
                appendHighlightFields(
                    appendOffendersAge(
                        appendOneTimeNomisRef(bearerToken, (ObjectNode)node, encrypter)),
                    searchTerm,
                    highlightFields));
    }

    private CompletionStage<ObjectNode> restrictViewOfOffenderIfNecessary(String bearerToken, ObjectNode rootNode) {
        if (toBoolean(rootNode, "currentExclusion") || toBoolean(rootNode, "currentRestriction")) {
            return offenderApi.canAccess(bearerToken, rootNode.get("offenderId").asLong())
                    .thenApply(canAccess -> canAccess ? rootNode : restrictView(rootNode));
        }
        return CompletableFuture.completedFuture(rootNode);
    }

    private ObjectNode restrictView(ObjectNode rootNode) {
        val restrictedAccessRootNode = JsonNodeFactory.instance.objectNode();
        restrictedAccessRootNode
            .put("accessDenied", true)
            .put("offenderId", rootNode.get("offenderId").asLong())
            .set("otherIds", JsonNodeFactory.instance.objectNode().put("crn", rootNode.get("otherIds").get("crn").asText()));
        return restrictedAccessRootNode;
    }
}
