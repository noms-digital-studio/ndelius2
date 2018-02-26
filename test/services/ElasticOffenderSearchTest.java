package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import helpers.FutureListener;
import interfaces.OffenderApi;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Environment;
import play.Mode;
import scala.io.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearchTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse searchResponse;

    @Mock
    private OffenderApi offenderApi;

    @Captor
    private ArgumentCaptor<SearchRequest> searchRequest;

    private ElasticOffenderSearch elasticOffenderSearch;

    @Before
    public void setup() {
        elasticOffenderSearch = new ElasticOffenderSearch(ConfigFactory.load(), restHighLevelClient, offenderApi);
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());
    }

    @Test
    public void dateOnlySearchDoesNotAddAPrefixSearch() {
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), 1, 42));

        elasticOffenderSearch.search("bearer-token", "15-09-1970", 10, 3);
        verify(restHighLevelClient).searchAsync(searchRequest.capture(), any());

        val query = (BoolQueryBuilder) searchRequest.getValue().source().query();
        assertThat((query.should())).doesNotContain(prefixQuery("firstName", "").boost(11));
    }

    @Test
    public void searchesOnlySubsetOfFields() {
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), 1, 42));

        elasticOffenderSearch.search("bearer-token", "15-09-1970 a smith 1/2/1992", 10, 3);

        verify(restHighLevelClient).searchAsync(searchRequest.capture(), any());
        assertThat(searchRequest.getValue().source().query()).isInstanceOfAny(BoolQueryBuilder.class);

        val query = (BoolQueryBuilder) searchRequest.getValue().source().query();
        val queryBuilder1 = (MultiMatchQueryBuilder)query.should().get(0);
        assertThat(queryBuilder1.value()).isEqualTo("a smith");
        assertThat(queryBuilder1.fields()).containsOnlyKeys(
            "firstName",
            "surname",
            "middleNames",
            "offenderAliases.firstName",
            "offenderAliases.surname",
            "contactDetails.addresses.town");

        val queryBuilder2 = (MultiMatchQueryBuilder)query.should().get(1);
        assertThat(queryBuilder2.value()).isEqualTo("smith");
        assertThat(queryBuilder2.fields()).containsOnlyKeys(
            "gender",
            "otherIds.crn",
            "otherIds.nomsNumber",
            "otherIds.niNumber",
            "otherIds.pncNumber",
            "otherIds.croNumber",
            "contactDetails.addresses.streetName",
            "contactDetails.addresses.county",
            "contactDetails.addresses.postcode");

        assertThat(((MultiMatchQueryBuilder)query.should().get(2)).value()).isEqualTo("1970-09-15");

        assertThat(((MultiMatchQueryBuilder)query.should().get(3)).value()).isEqualTo("1992-02-01");

        assertThat(((PrefixQueryBuilder)query.should().get(4)).value()).isEqualTo("a");

        assertThat(((PrefixQueryBuilder)query.should().get(5)).value()).isEqualTo("smith");

        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) searchRequest.getValue().source().postFilter();
        assertThat(termQueryBuilder.fieldName()).isEqualTo("softDeleted");
        assertThat(termQueryBuilder.value()).isEqualTo(false);
    }
    @Test
    public void unifiedHighlighterIsRequested() {
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), 1, 42));

        elasticOffenderSearch.search("bearer-token", "15-09-1970 a smith 1/2/1992", 10, 3);

        verify(restHighLevelClient).searchAsync(searchRequest.capture(), any());
        assertThat(searchRequest.getValue().source().query()).isInstanceOfAny(BoolQueryBuilder.class);

        val highlighter = searchRequest.getValue().source().highlighter();
        assertThat(highlighter.highlighterType()).isEqualTo("unified");
    }

    @Test
    public void returnsSearchResults() {

        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token","smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getTotal()).isEqualTo(totalHits);
        assertThat(result.getOffenders().size()).isEqualTo(totalHits);
        assertThat(result.getOffenders().get(0).get("offenderId").asInt()).isEqualTo(123);
        assertThat(result.getOffenders().get(0).get("age").asInt()).isNotEqualTo(0);
    }

    @Test
    public void returnsHighlightsInSearchResults() {

        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(
                        "forename", new HighlightField("forename", new Text[]{new Text("bob")}),
                        "surname", new HighlightField("surname", new Text[]{new Text("smith"), new Text("smithy")})
                ),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false)), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token","smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getOffenders().get(0).get("highlight")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("forename")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("forename").get(0).asText()).isEqualTo("bob");
        assertThat(result.getOffenders().get(0).get("highlight").get("surname")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("surname").get(0).asText()).isEqualTo("smith");
        assertThat(result.getOffenders().get(0).get("highlight").get("surname").get(1).asText()).isEqualTo("smithy");
    }

    @Test
    public void returnsDateOfBirthHighlightWhenDatesMatch() {

        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false, "dateOfBirth", "1965-07-19")), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token","19/7/1965", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getOffenders().get(0).get("highlight")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("dateOfBirth")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("dateOfBirth").get(0).asText()).isEqualTo("1965-07-19");
    }

    @Test
    public void doesNotReturnDateOfBirthHighlightWhenDatesDoNotMatch() {

        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false, "dateOfBirth", "1965-07-19")), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token","3/11/1999", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getOffenders().get(0).get("highlight")).isNotNull();
        assertThat(result.getOffenders().get(0).get("highlight").get("dateOfBirth")).isNull();
    }

    @Test
    public void highlightsNotReturnedForRestrictedInSearchResults() {
        // given
        val totalHits = 1;
        when(offenderApi.canAccess("bearer-token", 1)).thenReturn(CompletableFuture.completedFuture(false));

        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(
                        "forename", new HighlightField("forename", new Text[]{new Text("bob")})
                ),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", true, "currentExclusion", true)), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token","smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getOffenders().get(0).get("highlight")).isNull();
    }

    @Test
    public void calculatesTheCorrectSearchSourceFromValueWhenPageNumberIsZero() {

        // given
        val searchHits = getSearchHitArray();
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        val results = elasticOffenderSearch.search("bearer-token","smith", 10, 0);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getTotal()).isEqualTo(searchHits.length);
        assertThat(result.getOffenders().size()).isEqualTo(searchHits.length);
        assertThat(result.getOffenders().get(0).get("offenderId").asInt()).isEqualTo(123);
        assertThat(result.getOffenders().get(0).get("age").asInt()).isNotEqualTo(0);
    }

    @Test
    public void userAccessIsNotCheckedIfNoRecordsAreExcludedOrRestricted() {
        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false),
                ImmutableMap.of("offenderId", 12, "crn", "X2", "currentRestriction", false, "currentExclusion", false),
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", false)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        elasticOffenderSearch.search("bearer-token","smith", 10, 0).toCompletableFuture().join();

        verify(offenderApi, never()).canAccess(anyString(), anyInt());
    }

    @Test
    public void userAccessIsCheckedForEachRecordThatsIsExcludedOrRestricted() {
        when(offenderApi.canAccess(anyString(), anyInt())).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 11, "crn", "X1", "currentRestriction", true, "currentExclusion", false),
                ImmutableMap.of("offenderId", 12, "crn", "X2", "currentRestriction", false, "currentExclusion", false),
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", true)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        elasticOffenderSearch.search("bearer-token","smith", 10, 0).toCompletableFuture().join();

        ArgumentCaptor<Long> offenderIds = ArgumentCaptor.forClass(Long.class);
        verify(offenderApi, times(2)).canAccess(eq("bearer-token"), offenderIds.capture());
        assertThat(offenderIds.getAllValues()).contains(11L, 13L);
    }

    @Test
    public void offendersWhichCanNotBeAccessedHaveRestrictedData() {
        when(offenderApi.canAccess("bearer-token", 11)).thenReturn(CompletableFuture.completedFuture(true));
        when(offenderApi.canAccess("bearer-token", 13)).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 11, "crn", "X1", "currentRestriction", true, "currentExclusion", false),
                ImmutableMap.of("offenderId", 12, "crn", "X2", "currentRestriction", false, "currentExclusion", false),
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", true)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        val searchResult = elasticOffenderSearch.search("bearer-token", "smith", 10, 0).toCompletableFuture().join();
        val allowedAccessOffender = searchResult.getOffenders().get(0);
        val nonRestrictedExcludedOffender = searchResult.getOffenders().get(1);
        val notAllowedAccessOffender = searchResult.getOffenders().get(2);

        assertThat(accessDenied(allowedAccessOffender)).isFalse();
        assertThat(accessDenied(nonRestrictedExcludedOffender)).isFalse();
        assertThat(accessDenied(notAllowedAccessOffender)).isTrue();
    }

    @Test
    public void offendersWhichAreRestrictedViewContainJustPrimaryIds() {
        when(offenderApi.canAccess("bearer-token", 13)).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", true)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        val searchResult = elasticOffenderSearch.search("bearer-token","smith", 10, 0).toCompletableFuture().join();
        val offender = searchResult.getOffenders().get(0);

        assertThat(getChildNodeNames(offender)).containsExactlyInAnyOrder("offenderId", "accessDenied", "otherIds");
        assertThat(getChildNodeNames(offender.get("otherIds"))).containsExactlyInAnyOrder("crn");
    }

    @Test
    public void offendersWhichAreRestructedViewHaveOffenderIdAndCrnInTheClear() {
        when(offenderApi.canAccess("bearer-token", 13)).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", true)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));
        // when
        val searchResult = elasticOffenderSearch.search("bearer-token", "smith", 10, 0).toCompletableFuture().join();
        val offender = searchResult.getOffenders().get(0);

        assertThat(offender.get("offenderId").asLong()).isEqualTo(13);
        assertThat(offender.get("otherIds").get("crn").asText()).isEqualTo("X3");
    }

    private boolean accessDenied(JsonNode offender) {
        return Optional.ofNullable(offender.get("accessDenied")).map(JsonNode::asBoolean).orElse(false);
    }

    private SearchHit[] getSearchHitArray() {
        return getSearchHitArray(ImmutableMap.of("offenderId", 123, "crn", "X1224", "currentRestriction", false, "currentExclusion", false));
    }

    private List<String> getChildNodeNames(JsonNode node) {
        val iterator = node.fieldNames();
        return  StreamSupport.stream(((Iterable<String>) () -> iterator).spliterator(), false).collect(toList());
    }

    @SafeVarargs
    private final SearchHit[] getSearchHitArrayWithHighlights(Map<String, HighlightField> highlightFields, Map<String, Object>... replacements) {
        return stream(replacements).map((replacement) -> toSearchHit(highlightFields, replacement)).collect(toList()).toArray(new SearchHit[replacements.length]);
    }

    @SafeVarargs
    private final SearchHit[] getSearchHitArray(Map<String, Object>... replacements) {
        return stream(replacements).map((replacement) -> toSearchHit(ImmutableMap.of(), replacement)).collect(toList()).toArray(new SearchHit[replacements.length]);
    }

    private SearchHit toSearchHit(Map<String, HighlightField> highlightFields, Map<String, Object> replacementMap) {
        val searchHitMap = new HashMap<String, Object>();
        val environment = new Environment(null, this.getClass().getClassLoader(), Mode.TEST);

        val offenderSearchResultsTemplate = Source.fromInputStream(environment.resourceAsStream("offender-search-result.json.template"), "UTF-8").mkString();

        val offenderSearchResults =
                withDefaults(replacementMap).
                        keySet().
                        stream().
                        reduce(offenderSearchResultsTemplate,
                                (template, key) -> template.replace(format("${%s}", key), withDefaults(replacementMap).get(key).toString()));


        val bytesReference = new BytesArray(offenderSearchResults);
        searchHitMap.put("_source", bytesReference);
        searchHitMap.put("highlight", highlightFields);
        return SearchHit.createFromMap(searchHitMap);
    }

    private Map<String, Object> withDefaults(Map<String, Object> replacementMap) {
        if (!replacementMap.containsKey("dateOfBirth")) {
            return ImmutableMap.<String, Object>builder().putAll(replacementMap).put("dateOfBirth", "1978-01-16").build();
        }
        return replacementMap;
    }

}