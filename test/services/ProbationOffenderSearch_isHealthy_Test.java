package services;

import com.typesafe.config.ConfigFactory;
import interfaces.UserAwareApiToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.cache.AsyncCacheApi;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProbationOffenderSearch_isHealthy_Test {

    private ProbationOffenderSearch probationOffenderSearch;

    @Mock
    private WSClient wsClient;

    @Mock
    private WSRequest wsRequest;

    @Mock
    private WSResponse wsResponse;

    @Mock
    private AsyncCacheApi cache;

    @Mock
    private UserAwareApiToken userAwareApiToken;


    @Before
    public void setup() {
        probationOffenderSearch = new ProbationOffenderSearch(ConfigFactory.load(), wsClient, cache, userAwareApiToken);
        when(wsClient.url(any())).thenReturn(wsRequest);
        when(wsRequest.addHeader(any(), any())).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
    }

    @Test
    public void returnsHealthyWhenSearchApiReturns200Response() {
        when(wsResponse.getStatus()).thenReturn(200);

        assertThat(probationOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(true);
    }

    @Test
    public void returnsUnhealthyWhenSearchApiReturnsNon200Response() {
        when(wsResponse.getStatus()).thenReturn(404);

        assertThat(probationOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(false);
    }

    @Test
    public void returnsUnHealthyWhenSearchApiCallThrowsException() {
        when(wsRequest.get()).thenReturn(supplyAsync(() -> { throw new RuntimeException("Boom!"); }));

        assertThat(probationOffenderSearch.isHealthy().toCompletableFuture().join().isHealthy()).isEqualTo(false);
    }

}
