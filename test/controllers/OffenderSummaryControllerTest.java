package controllers;

import com.mongodb.rx.client.MongoClient;
import helpers.Encryption;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

@RunWith(MockitoJUnitRunner.class)
public class OffenderSummaryControllerTest extends WithApplication {
    @Mock
    private OffenderApi offenderApi;

    @Before
    public void before()  {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        when(offenderApi.canAccess(any(), anyLong())).thenReturn(CompletableFuture.completedFuture(true));
    }

    @Test
    public void indexPageSessionContainsBearerTokenWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderApiBearerToken")).isEqualTo(JwtHelperTest.generateToken());
    }

    @Test
    public void indexPageSessionContainsUsernameWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest( "barney.beats"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("username")).isEqualTo("barney.beats");
    }

    @Test
    public void indexPageSessionContainsOffenderIdWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest("bobby.beats", "321"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderId")).isEqualTo("321");
    }

    @Test
    public void indexPageRenderedWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(OK);
        val content = Helpers.contentAsString(result);

        val offenderSummaryPageContent = "<div id=\"content\"></div>";
        assertThat(content).contains(offenderSummaryPageContent);
    }

    @Test
    public void indexPageSessionDoesNotContainsOffenderIdWhenOffenderIsNotAccessibleToUser() throws UnsupportedEncodingException {
        when(offenderApi.canAccess(any(), anyLong())).thenReturn(CompletableFuture.completedFuture(false));

        val result = route(app, buildIndexPageRequest("bobby.beats", "321"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderId")).isNull();
    }

    @Test
    public void notAccessibleIndexPageRenderedWhenOffenderIsNotAccessibleToUser() throws UnsupportedEncodingException {
        when(offenderApi.canAccess(any(), anyLong())).thenReturn(CompletableFuture.completedFuture(false));
        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(OK);
        val content = Helpers.contentAsString(result);

        assertThat(content).contains("This offender is not allowed to be viewed");
    }


    @Test
    public void returnsServerErrorWhenLogonFails() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(supplyAsync(() -> { throw new RuntimeException("boom"); }));

        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void badUserTokenReturns400Response() {
        val request = new Http.RequestBuilder().method(GET).uri("/offenderSummary?offenderId=123&user=bananas&t=0RDkaUIYRF5PyKB2hUt1iA%3D%3D");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void badTimeTokenReturns400Response() {
        val request = new Http.RequestBuilder().method(GET).uri("/offenderSummary?offenderId=123&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=sausage");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }


    @Test
    public void usernameInSessionUsedForAPILogonForInternalForward() {
        val result = route(app, buildIndexPageForwardRequest( "barney.beats"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("username")).isEqualTo("barney.beats");
        verify(offenderApi).logon("barney.beats");
    }

    @Test
    public void indexPageSessionContainsBearerTokenWhenLogonSucceedsForForwardRequest() {
        val result = route(app, buildIndexPageForwardRequest( "barney.beats"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderApiBearerToken")).isEqualTo(JwtHelperTest.generateToken());
    }

    @Test
    public void indexPageSessionContainsOffenderIdWhenLogonSucceedsForForwardRequest() {
        val result = route(app, buildIndexPageForwardRequest( "barney.beats", "321"));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderId")).isEqualTo("321");
    }

    @Test
    public void missingUsernameInSessionReturns400Response() {
        val result = route(app, buildIndexPageForwardRequest( null));

        assertThat(result.status()).isEqualTo(BAD_REQUEST);
    }


    private Http.RequestBuilder buildIndexPageRequest(String username) throws UnsupportedEncodingException {
        return buildIndexPageRequest(username, "123");
    }

    private Http.RequestBuilder buildIndexPageRequest() throws UnsupportedEncodingException {
        return buildIndexPageRequest("roger.bobby", "123");
    }

    private Http.RequestBuilder buildIndexPageRequest(String username, String offenderId) throws UnsupportedEncodingException {
        val secretKey = "ThisIsASecretKey";

        val encryptedUser = URLEncoder.encode(Encryption.encrypt(username, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli() + (1000 * 60 * 59)), secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

        return new Http.RequestBuilder().method(GET).uri(String.format("/offenderSummary?offenderId=%s&user=%s&t=%s", offenderId, encryptedUser, encryptedTime));
    }

    private Http.RequestBuilder buildIndexPageForwardRequest(String username, String offenderId) {
        return new Http.RequestBuilder().session("username", username).method(GET).uri(String.format("/offenderSummary?offenderId=%s", offenderId));
    }

    private Http.RequestBuilder buildIndexPageForwardRequest(String username) {
        return buildIndexPageForwardRequest(username, "123");
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(OffenderApi.class).toInstance(offenderApi),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "1h")
                .build();
    }

}