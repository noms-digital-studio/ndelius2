package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.offendersearch.OffenderSearchResult;
import helpers.*;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.SuggestBuilder;
import play.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.suggest.SuggestBuilders.termSuggestion;
import static play.libs.Json.parse;

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

    private SearchSourceBuilder searchSourceFor(String searchTerm, int pageSize, int pageNumber) {
        val boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutDates(searchTerm))
            .field("firstName", 10)
            .field("surname", 10)
            .field("middleNames", 8)
            .field("offenderAliases.firstName", 8)
            .field("offenderAliases.surname", 8)
            .field("contactDetails.addresses.town")
            // CROSS_FIELDS Analyzes the query string into individual terms, then looks
            // for each term in any of the fields, as though they were one big field
            .type(CROSS_FIELDS));

        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutSingleLetters(termsWithoutDates(searchTerm)))
            .field("gender")
            .field("otherIds.crn", 10)
            .field("otherIds.nomsNumber", 10)
            .field("otherIds.niNumber", 10)
            .field("otherIds.pncNumber", 10)
            .field("otherIds.croNumber", 10)
            .field("contactDetails.addresses.streetName")
            .field("contactDetails.addresses.county")
            .field("contactDetails.addresses.postcode", 10)
            // MOST_FIELDS Finds documents which match any field and combines the _score from each field
            .type(MOST_FIELDS));

        termsThatLookLikeDates(searchTerm).forEach(dateTerm ->
            boolQueryBuilder.should().add(multiMatchQuery(dateTerm)
                .field("dateOfBirth", 11)
                .lenient(true)));

        Stream.of(termsWithoutDates(searchTerm).split(" "))
            .filter(term -> !term.isEmpty())
            .forEach(term -> boolQueryBuilder.should().add(prefixQuery("firstName", term.toLowerCase()).boost(11)));

        val highlight = new HighlightBuilder().
                highlighterType("unified").
                field("*").
                preTags("").
                postTags("");

        val searchSource = new SearchSourceBuilder()
            .query(boolQueryBuilder)
            .highlighter(highlight)
            .postFilter(termQuery("softDeleted", false))
            .explain(Logger.isDebugEnabled())
            .size(pageSize)
            .from(pageSize * aValidPageNumberFor(pageNumber))
            .suggest(suggestionsFor(searchTerm));

        Logger.debug(searchSource.toString());
        return searchSource;
    }

    private String termsWithoutSingleLetters(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> term.length() > 1)
            .collect(joining(" "));
    }

    private List<String> termsThatLookLikeDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(DateTimeHelper::canBeConvertedToADate)
            .map(term -> DateTimeHelper.covertToCanonicalDate(term).get())
            .collect(toList());
    }

    private String termsWithoutDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> !DateTimeHelper.covertToCanonicalDate(term).isPresent())
            .collect(joining(" "));
    }

    private SuggestBuilder suggestionsFor(String searchTerm) {
        return new SuggestBuilder()
            .addSuggestion("surname", termSuggestion("surname").text(searchTerm))
            .addSuggestion("firstName", termSuggestion("firstName").text(searchTerm));
    }

    private CompletionStage<OffenderSearchResult> processSearchResponse(String bearerToken, String searchTerm, SearchResponse response) {
        logResults(response);

        val offenderNodesCompletionStages = stream(response.getHits().getHits())
                .map(searchHit -> {
                    JsonNode offender = parse(searchHit.getSourceAsString());
                    return embellishNode(bearerToken, searchTerm, offender, searchHit.getHighlightFields());
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

    private CompletionStage<ObjectNode> embellishNode(String bearerToken, String searchTerm, JsonNode node, Map<String, HighlightField> highlightFields) {
        return restrictViewOfOffenderIfNecessary(
                bearerToken,
                appendHighlightFields(appendOffendersAgeAndOneTimeNomisRef(bearerToken, (ObjectNode)node), searchTerm, highlightFields)
        );
    }

    private ObjectNode appendOffendersAgeAndOneTimeNomisRef(String bearerToken, ObjectNode rootNode) {

        val pipeline = new HashMap<Function<ObjectNode, Optional<JsonNode>>, BiFunction<ObjectNode, JsonNode, ObjectNode>>() {
            {
                put(
                        source -> Optional.ofNullable(source.get("dateOfBirth")),
                        (result, dateOfBirth) -> result.put("age", calculateAge(dateOfBirth.asText(), systemUTC()))
                );
                put(
                        source -> Optional.ofNullable(source.get("otherIds")).flatMap(otherIds -> Optional.ofNullable(otherIds.get("nomsNumber"))),
//                        source -> Optional.of(JsonNodeFactory.instance.textNode("A3597AE")),
                        (result, nomsNumber) -> result.put("oneTimeNomisRef", oneTimeNomisRef(bearerToken, nomsNumber.asText()))
                );
            }
        };

        for (val entry : pipeline.entrySet()) {

            val objectNode = rootNode;
            rootNode = entry.getKey().apply(objectNode).map(jsonNode -> entry.getValue().apply(objectNode, jsonNode)).orElse(rootNode);
        }

        return rootNode;
    }

    private ObjectNode appendOffendersAge(ObjectNode rootNode) {
        val dateOfBirth = dateOfBirth(rootNode);

        return Optional.ofNullable(dateOfBirth)
            .map(dob -> rootNode.put("age", calculateAge(dob, systemUTC())))
            .orElse(rootNode);
    }

    private String oneTimeNomisRef(String bearerToken, String nomisId) {

        val reference = ImmutableMap.of(                        // Creates a limited-time reference to the nomisId number
                "user", JwtHelper.principal(bearerToken),   // as a string that can only be used by the same user wthin
                "noms", nomisId,                            // a limited time frame. This allows safe access to a NomisId
                "tick", Instant.now().toEpochMilli()        // only by a User that has already had Offender canAccess() checked
        );

        return encrypter.apply(JsonHelper.stringify(reference));
    }


    private ObjectNode appendHighlightFields(ObjectNode rootNode, String searchTerm, Map<String, HighlightField> highlightFields) {
        val highlightNode = JsonNodeFactory.instance.objectNode();
        highlightFields.forEach((key, value) -> {
            val arrayNode = JsonNodeFactory.instance.arrayNode();
            Stream.of(value.fragments()).forEach(text -> arrayNode.add(text.string()));
            highlightNode.set(key, arrayNode);
        });

        if (shouldHighlightDateOfBirth(rootNode, searchTerm)) {
            val arrayNode = JsonNodeFactory.instance.arrayNode();
            arrayNode.add(dateOfBirth(rootNode));
            highlightNode.set("dateOfBirth", arrayNode);
        }

        rootNode.set("highlight", highlightNode);
        return rootNode;
    }

    private String dateOfBirth(ObjectNode rootNode) {
        val dateOfBirth = rootNode.get("dateOfBirth");
        return Optional.ofNullable(dateOfBirth).map(JsonNode::asText).orElse(null);
    }

    private boolean shouldHighlightDateOfBirth(ObjectNode rootNode, String searchTerm) {
        val dateOfBirth = dateOfBirth(rootNode);

        return Optional.ofNullable(dateOfBirth).map(dob -> doAnySearchTermsMatchDateOfBirth(searchTerm, dob)).orElse(false);
    }

    private boolean doAnySearchTermsMatchDateOfBirth(String searchTerm, String dateOfBirth) {
        return termsThatLookLikeDates(searchTerm).stream().anyMatch(currentDate -> currentDate.equals(dateOfBirth));
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
        val restrictedAccessRootNode = JsonNodeFactory.instance.objectNode();
        restrictedAccessRootNode
            .put("accessDenied", true)
            .put("offenderId", rootNode.get("offenderId").asLong())
            .set("otherIds", JsonNodeFactory.instance.objectNode().put("crn", rootNode.get("otherIds").get("crn").asText()));
        return restrictedAccessRootNode;
    }
}
