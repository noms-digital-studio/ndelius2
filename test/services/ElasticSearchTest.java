package services;

import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import services.search.ElasticSearch;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchTest {

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Test
    @Ignore
    public void returnsSearchResults() {

        val elasticSearch = new ElasticSearch(restHighLevelClient);
        val results = elasticSearch.search("smith", 10, 0);

        assertThat(results.toCompletableFuture().join().getOffenders().size()).isEqualTo(3);
        assertThat(results.toCompletableFuture().join().getOffenders()).isEqualTo("");
    }

}