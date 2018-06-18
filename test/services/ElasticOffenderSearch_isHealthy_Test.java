package services;

import com.typesafe.config.ConfigFactory;
import interfaces.OffenderApi;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearch_isHealthy_Test {

    private ElasticOffenderSearch elasticOffenderSearch;

    @Mock
    private RestHighLevelClient elasticSearchClient;

    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setup() {
        elasticOffenderSearch = new ElasticOffenderSearch(ConfigFactory.load(), elasticSearchClient, offenderApi);
    }

    @Test
    public void returnsHealthyWhenElasticSearchClientPingReturnsTrue() throws IOException {
        when(elasticSearchClient.ping()).thenReturn(true);

        assertThat(elasticOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(true);
    }

    @Test
    public void returnsUnhealthyWhenElasticSearchClientPingReturnsFalse() throws IOException {
        when(elasticSearchClient.ping()).thenReturn(false);

        assertThat(elasticOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(false);
    }

    @Test
    public void returnsUnHealthyWhenElasticSearchClientPingThrowsException() throws IOException {
        when(elasticSearchClient.ping()).thenThrow(new IOException("Boom!"));

        assertThat(elasticOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(false);
    }

}
