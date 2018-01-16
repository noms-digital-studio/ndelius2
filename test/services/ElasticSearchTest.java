package services;

import data.offendersearch.OffenderSearchResult;
import helpers.FutureListener;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import play.Environment;
import play.Mode;
import scala.io.Source;
import services.search.ElasticSearch;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse searchResponse;

    @Test
    @Ignore
    public void returnsSearchResults() {

        // given
        val elasticSearch = new ElasticSearch(restHighLevelClient);

        val totalHits = 1;
        val maxScore = 42;
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(), totalHits, maxScore));

        doAnswer((Answer) invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());

        // when
        val results = elasticSearch.search("smith", 10, 3);

        // then
        verify(restHighLevelClient, times(1)).searchAsync(eq(getSearchRequestStartingFrom(20)), isA(FutureListener.class));

        OffenderSearchResult result = results.toCompletableFuture().join();
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getOffenders().size()).isEqualTo(1);
        assertThat(result.getOffenders().get(0).get("offenderId").asInt()).isEqualTo(123);
        assertThat(result.getOffenders().get(0).get("age").asInt()).isNotEqualTo(0);
    }

    @Test
    @Ignore
    public void calculatesTheCorrectSearchSourceFromValueWhenPageNumberIsZero() {

        val elasticSearch = new ElasticSearch(restHighLevelClient);

        restHighLevelClient.searchAsync(any(), any());

        val results = elasticSearch.search("smith", 10, 0);

        verify(restHighLevelClient, times(1)).searchAsync(eq(getSearchRequestStartingFrom(0)), isA(FutureListener.class));
    }

    private SearchHit[] getSearchHitArray() {
        Map<String, Object> searchHitMap = new HashMap<>();
        val environment = new Environment(null, this.getClass().getClassLoader(), Mode.TEST);
        val offenderSearchResults = Source.fromInputStream(environment.resourceAsStream("offender-search-result.json"), "UTF-8").mkString();
        val bytesReference = new BytesArray(offenderSearchResults);
        searchHitMap.put("_source", bytesReference);
        val searchHit = SearchHit.createFromMap(searchHitMap);
        return new SearchHit[]{searchHit};
    }

    private SearchRequest getSearchRequestStartingFrom(int i) {
        val searchSource = new SearchSourceBuilder().query(multiMatchQuery("smith", "surname", "firstName", "gender"));
        searchSource.size(10);
        searchSource.from(i);
        return new SearchRequest("offender").source(searchSource);
    }

}