package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.FutureListener;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
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

import static helpers.JsonHelper.toBoolean;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static play.libs.Json.parse;
import static services.helpers.SearchQueryBuilder.searchSourceFor;

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
    public CompletionStage<Map<String, Object>> search(String bearerToken, String searchTerm, int pageSize, int pageNumber) {

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
            });
        };

        val request = new SearchRequest("offender").source(searchSourceFor(searchTerm, pageSize, pageNumber));
        val listener = new FutureListener<SearchResponse>();

        elasticSearchClient.searchAsync(request, listener);

        return listener.stage().thenComposeAsync(processResponse);
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

        return Optional.ofNullable(rootNode.get("offenderManagers"))
            .map(offenderManagers -> offenderManagers)
            .orElse(parse("[]"));
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
