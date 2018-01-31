package services;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import interfaces.OffenderApi;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DeliusOffenderApi_canAccess_Test {

    private OffenderApi offenderApi;

    @Mock
    private WSClient wsClient;

    @Mock
    private WSRequest wsRequest;

    @Mock
    private WSResponse wsResponse;

    @Before
    public void setup() {
        val config = ConfigFactory.load().withValue("offender.api.url", ConfigValueFactory.fromAnyRef("http://delius-api/api/"));
        offenderApi = new DeliusOffenderApi(config, wsClient);
        when(wsClient.url(any())).thenReturn(wsRequest);
        when(wsRequest.addHeader(any(), any())).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
    }

    @Test
    public void setsOffenderIdInUrl() {
        offenderApi.canAccess("ABC", 2500155552L);

        verify(wsClient).url(eq("http://delius-api/api/offenders/offenderId/2500155552/userAccess"));
    }

    @Test
    public void setsBearerTokenInHeader() {
        offenderApi.canAccess("ZXCVB", 123);

        verify(wsRequest).addHeader("Authorization", "Bearer ZXCVB");
    }

    @Test
    public void statusOf200MeansCanAccess() {
        when(wsResponse.getStatus()).thenReturn(200);

        val canAccess = offenderApi.canAccess("ABC", 123).toCompletableFuture().join();

        assertThat(canAccess).isTrue();
    }

    @Test
    public void statusOf403MeansCannotAccess() {
        when(wsResponse.getStatus()).thenReturn(403);

        val canAccess = offenderApi.canAccess("ABC", 123).toCompletableFuture().join();

        assertThat(canAccess).isFalse();
    }

    @Test
    public void treatAnyOtherStatusAsCannotAccess() {
        when(wsResponse.getStatus()).thenReturn(500);

        val canAccess = offenderApi.canAccess("ABC", 123).toCompletableFuture().join();

        assertThat(canAccess).isFalse();
    }

    @Test
    public void treatAnyExceptionsAsCannotAccess() {
        val error = new CompletableFuture<WSResponse>();
        error.completeExceptionally(new RuntimeException("Boom"));
        when(wsRequest.get()).thenReturn(error);

        val canAccess = offenderApi.canAccess("ABC", 123).toCompletableFuture().join();

        assertThat(canAccess).isFalse();
    }


}