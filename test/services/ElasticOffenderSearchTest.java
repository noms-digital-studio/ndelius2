package services;

import helpers.FutureListener;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Environment;
import play.Mode;
import scala.io.Source;
import services.search.ElasticOffenderSearch;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearchTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse searchResponse;

    @Captor
    private ArgumentCaptor<SearchRequest> searchRequest;

    @Test
    public void searchesOnlySubsetOfFields() {
        val elasticSearch = new ElasticOffenderSearch(restHighLevelClient);
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), 1, 42));

        elasticSearch.search("smith", 10, 3);

        verify(restHighLevelClient).searchAsync(searchRequest.capture(), any());
        assertThat(searchRequest.getValue().source().query()).isInstanceOfAny(MultiMatchQueryBuilder.class);

        val query = (MultiMatchQueryBuilder) searchRequest.getValue().source().query();

        assertThat(query.fields()).containsKeys("firstName");
        assertThat(query.fields()).containsKeys("surname");
        assertThat(query.fields()).containsKeys("dateOfBirth");
        assertThat(query.fields()).containsKeys("gender");
        assertThat(query.fields()).containsKeys("otherIds.crn");
        assertThat(query.fields()).containsKeys("otherIds.nomsNumber");
        assertThat(query.fields()).containsKeys("otherIds.pncNumber");
        assertThat(query.fields()).containsKeys("otherIds.croNumber");
        assertThat(query.fields()).containsKeys("offenderAliases.firstName");
        assertThat(query.fields()).containsKeys("offenderAliases.surname");
        assertThat(query.fields()).containsKeys("contactDetails.addresses.streetName");
        assertThat(query.fields()).containsKeys("contactDetails.addresses.town");
        assertThat(query.fields()).containsKeys("contactDetails.addresses.county");
        assertThat(query.fields()).containsKeys("contactDetails.addresses.postcode");
    }

    @Test
    public void returnsSearchResults() {

        // given
        val elasticSearch = new ElasticOffenderSearch(restHighLevelClient);
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), totalHits, 42));
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());

        // when
        val results = elasticSearch.search("smith", 10, 3);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getTotal()).isEqualTo(totalHits);
        assertThat(result.getOffenders().size()).isEqualTo(totalHits);
        assertThat(result.getOffenders().get(0).get("offenderId").asInt()).isEqualTo(123);
        assertThat(result.getOffenders().get(0).get("age").asInt()).isNotEqualTo(0);
    }

    @Test
    public void calculatesTheCorrectSearchSourceFromValueWhenPageNumberIsZero() {

        // given
        val elasticSearch = new ElasticOffenderSearch(restHighLevelClient);
        val totalHits = 1;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), totalHits, 42));
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());

        // when
        val results = elasticSearch.search("smith", 10, 0);

        // then
        val result = results.toCompletableFuture().join();
        assertThat(result.getTotal()).isEqualTo(totalHits);
        assertThat(result.getOffenders().size()).isEqualTo(totalHits);
        assertThat(result.getOffenders().get(0).get("offenderId").asInt()).isEqualTo(123);
        assertThat(result.getOffenders().get(0).get("age").asInt()).isNotEqualTo(0);
    }

    private SearchHit[] getSearchHitArray() {
        val searchHitMap = new HashMap<String, Object>();
        val environment = new Environment(null, this.getClass().getClassLoader(), Mode.TEST);
        val offenderSearchResults = Source.fromInputStream(environment.resourceAsStream("offender-search-result.json"), "UTF-8").mkString();
        val bytesReference = new BytesArray(offenderSearchResults);
        searchHitMap.put("_source", bytesReference);
        val searchHit = SearchHit.createFromMap(searchHitMap);
        return new SearchHit[]{searchHit};
    }

}