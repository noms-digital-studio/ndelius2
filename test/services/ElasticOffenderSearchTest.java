package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import helpers.FutureListener;
import interfaces.OffenderApi;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static utils.data.OffenderESDataFactory.getSearchHitArray;
import static utils.data.OffenderESDataFactory.getSearchHitArrayWithHighlights;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearchTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse searchResponse;

    @Mock
    private OffenderApi offenderApi;

    private ElasticOffenderSearch elasticOffenderSearch;

    @Before
    public void setup() {
        elasticOffenderSearch = new ElasticOffenderSearch(ConfigFactory.load(), restHighLevelClient, offenderApi);
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());
        when(offenderApi.probationAreaDescriptions(Mockito.any(), Mockito.any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                "N01", "N01 Area",
                "N02", "N02 Area",
                "N03", "N03 Area"
        )));
    }

    @Test
    public void returnsSearchResults() {
        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token", emptyList(), "smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(Long.valueOf(result.get("total").toString())).isEqualTo(totalHits);
        assertThat(((List) result.get("offenders")).size()).isEqualTo(totalHits);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("offenderId").asInt()).isEqualTo(123);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("age").asInt()).isNotEqualTo(0);
    }

    @Test
    public void returnsSearchResultsGroupingMatchingNamesAndCurrentOffendersAtTheTop() {
        // given
        val totalHits = 4;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithMultipleHits(), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(Long.valueOf(result.get("total").toString())).isEqualTo(totalHits);
        assertThat(((List) result.get("offenders")).size()).isEqualTo(totalHits);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("offenderId").asInt()).isEqualTo(102);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(1)).get("offenderId").asInt()).isEqualTo(100);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(2)).get("offenderId").asInt()).isEqualTo(103);
        assertThat(((JsonNode) ((List) result.get("offenders")).get(3)).get("offenderId").asInt()).isEqualTo(101);
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
        val results = elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("forename")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("forename").get(0).asText()).isEqualTo("bob");
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("surname")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("surname").get(0).asText()).isEqualTo("smith");
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("surname").get(1).asText()).isEqualTo("smithy");
    }

    @Test
    public void returnsDateOfBirthHighlightWhenDatesMatch() {
        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false, "dateOfBirth", "1965-07-19")), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token", emptyList(),"19/7/1965", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("dateOfBirth")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("dateOfBirth").get(0).asText()).isEqualTo("1965-07-19");
    }

    @Test
    public void doesNotReturnDateOfBirthHighlightWhenDatesDoNotMatch() {
        // given
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArrayWithHighlights(
                ImmutableMap.of(),
                ImmutableMap.of("offenderId", 1, "crn", "X1", "currentRestriction", false, "currentExclusion", false, "dateOfBirth", "1965-07-19")), totalHits, 42));

        // when
        val results = elasticOffenderSearch.search("bearer-token", emptyList(),"3/11/1999", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight")).isNotNull();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight").get("dateOfBirth")).isNull();
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
        val results = elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(((JsonNode) ((List) result.get("offenders")).get(0)).get("highlight")).isNull();
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
        elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 0).toCompletableFuture().join();

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
        elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 0).toCompletableFuture().join();

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
        val searchResult = elasticOffenderSearch.search("bearer-token", emptyList(), "smith", 10, 0).toCompletableFuture().join();
        val allowedAccessOffender = ((JsonNode) ((List) searchResult.get("offenders")).get(0));
        val nonRestrictedExcludedOffender = ((JsonNode) ((List) searchResult.get("offenders")).get(1));
        val notAllowedAccessOffender = ((JsonNode) ((List) searchResult.get("offenders")).get(2));

        assertThat(accessDenied(allowedAccessOffender)).isFalse();
        assertThat(accessDenied(nonRestrictedExcludedOffender)).isFalse();
        assertThat(accessDenied(notAllowedAccessOffender)).isTrue();
    }

    @Test
    public void restrictedViewOffendersContainPrimaryIdsAndOffenderManagersOnly() {
        when(offenderApi.canAccess("bearer-token", 13)).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 13, "crn", "X3", "currentRestriction", false, "currentExclusion", true)
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));

        // when
        val searchResult = elasticOffenderSearch.search("bearer-token", emptyList(),"smith", 10, 0).toCompletableFuture().join();
        val offender = ((JsonNode) ((List) searchResult.get("offenders")).get(0));

        assertThat(getChildNodeNames(offender)).containsExactlyInAnyOrder("offenderId", "accessDenied", "otherIds", "offenderManagers");
        assertThat(getChildNodeNames(offender.get("otherIds"))).containsExactlyInAnyOrder("crn");
    }

    @Test
    public void offendersWhichAreRestrictedViewHaveOffenderIdCrnAndOffenderManagersInTheClear() {
        when(offenderApi.canAccess("bearer-token", 13)).thenReturn(CompletableFuture.completedFuture(false));

        // given
        val searchHits = getSearchHitArray(
                ImmutableMap.of("offenderId", 13,
                    "crn", "X3",
                    "currentRestriction", false,
                    "currentExclusion", true,
                    "offenderManagers", ImmutableList.of())
        );
        when(searchResponse.getHits()).thenReturn(new SearchHits(searchHits, searchHits.length, 42));

        // when
        val searchResult = elasticOffenderSearch.search("bearer-token", emptyList(), "smith", 10, 0).toCompletableFuture().join();
        val offender = ((JsonNode) ((List) searchResult.get("offenders")).get(0));

        assertThat(offender.get("offenderId").asLong()).isEqualTo(13);
        assertThat(offender.get("otherIds").get("crn").asText()).isEqualTo("X3");
        assertThat(offender.get("offenderManagers").get(0).get("trustOfficer").get("surname").asText()).isEqualTo("Dolphin");
        assertThat(offender.get("offenderManagers").get(1).get("trustOfficer").get("surname").asText()).isEqualTo("Staff");
    }

    private boolean accessDenied(JsonNode offender) {
        return Optional.ofNullable(offender.get("accessDenied")).map(JsonNode::asBoolean).orElse(false);
    }

    private static SearchHit[] getSearchHitArrayWithMultipleHits() {
        return getSearchHitArray(
            ImmutableMap.<String, Object>builder()
                .put("offenderId", 100)
                .put("firstName", "john")
                .put("surname", "smith")
                .put("crn", "X0001")
                .put("currentRestriction", false)
                .put("currentExclusion", false)
                .build(),
            ImmutableMap.<String, Object>builder()
                .put("offenderId", 101)
                .put("firstName", "fred")
                .put("surname", "Jones")
                .put("crn", "X0002")
                .put("currentRestriction", false)
                .put("currentExclusion", false)
                .build(),
            ImmutableMap.<String, Object>builder()
                .put("offenderId", 102)
                .put("firstName", "JOHN")
                .put("surname", "smith")
                .put("crn", "X0003")
                .put("currentRestriction", false)
                .put("currentExclusion", false)
                .put("currentDisposal", "1")
                .build(),
            ImmutableMap.<String, Object>builder()
                .put("offenderId", 103)
                .put("firstName", "john")
                .put("surname", "SMITH")
                .put("crn", "X0003")
                .put("currentRestriction", false)
                .put("currentExclusion", false)
                .build()
        );
    }

    private List<String> getChildNodeNames(JsonNode node) {
        val iterator = node.fieldNames();
        return  StreamSupport.stream(((Iterable<String>) () -> iterator).spliterator(), false).collect(toList());
    }

}