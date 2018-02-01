package controllers;

import data.offendersearch.OffenderSearchResult;
import helpers.Encryption;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchControllerTest extends WithApplication {
    private static final int FIFTY_NINE_MINUTES = 1000 * 60 * 59;
    private String userTokenValidDuration = "1h";
    private String secretKey;

    @Mock
    private OffenderSearch elasticOffenderSearch;

    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setUp() {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture("bearerToken"));
        secretKey = "ThisIsASecretKey";
    }

    @After
    public void tearDown() {
        userTokenValidDuration = "1h";
    }

    @Test
    public void indexPageSessionContainsBearerTokenWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderApiBearerToken")).isEqualTo("bearerToken");
    }

    @Test
    public void returnsServerErrorWhenLogonFails() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(supplyAsync(() -> { throw new RuntimeException("boom"); }));

        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void searchTermReturnsResults() {
        when(elasticOffenderSearch.search(any(), any(), anyInt(), anyInt())).thenReturn(completedFuture(OffenderSearchResult.builder().build()));
        val request = new Http.RequestBuilder().session("offenderApiBearerToken", "bearer-token").method(GET).uri("/searchOffender/smith");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"offenders\":null,\"suggestions\":null,\"total\":0}", contentAsString(result));
    }

    @Test
    public void searchingWithoutABearerTokenReturns401() {
        val request = new Http.RequestBuilder().method(GET).uri("/searchOffender/smith");
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void badUserTokenReturns400Response() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch?user=bananas&t=0RDkaUIYRF5PyKB2hUt1iA%3D%3D");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void badTimeTokenReturns400Response() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=sausage");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void validUserAndTimeTokenReturns200Response() throws UnsupportedEncodingException {
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(System.currentTimeMillis()), secretKey), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenIsALittleBitInTheFutureDueToMachineTimeDriftReturns200Response() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest();
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void validUserAndOldTimeTokenReturns401Response() throws UnsupportedEncodingException {
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val overAnHourAgo = String.valueOf(LocalDateTime.now().minusMinutes(61).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(overAnHourAgo, secretKey), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void timeTokenValidDurationIsDeterminedByConfig() throws UnsupportedEncodingException {
        userTokenValidDuration = "101d";
        stopPlay();
        startPlay();

        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val overAnHourAgo = String.valueOf(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(overAnHourAgo, secretKey), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    private Http.RequestBuilder buildIndexPageRequest() throws UnsupportedEncodingException {
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(System.currentTimeMillis()+ FIFTY_NINE_MINUTES), secretKey), "UTF-8");

        return new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(OffenderSearch.class).toInstance(elasticOffenderSearch),
                bind(OffenderApi.class).toInstance(offenderApi)
            )
            .configure("params.user.token.valid.duration", userTokenValidDuration)
            .build();
    }

}