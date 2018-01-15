package services;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import services.search.ElasticSearch;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchTest {

    @Test
    @Ignore
    public void returnsSearchResults() {

        val elasticSearch = new ElasticSearch(null);
        val results = elasticSearch.search("smith", 10, 0);

        assertThat(results.toCompletableFuture().join().getOffenders().size()).isEqualTo(3);
        assertThat(results.toCompletableFuture().join().getOffenders()).isEqualTo("");
    }

}