package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static helpers.JwtHelperTest.FAKE_USER_BEARKER_TOKEN;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchControllerTest extends WithApplication {

    private String userTokenValidDuration = "1h";
    private String secretKey;

    @Mock
    private OffenderSearch elasticOffenderSearch;

    @Mock
    private OffenderApi offenderApi;

    @Mock
    private AnalyticsStore analyticsStore;

    @Captor
    private ArgumentCaptor<Map<String, Object>> analyticsEventCaptor;

    @Before
    public void setUp() {

        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture("bearerToken"));
        when(elasticOffenderSearch.search(any(), any(), anyInt(), anyInt())).thenReturn(completedFuture(ImmutableMap.of(
                "offenders", ImmutableList.of(),
                "suggestions", ImmutableList.of(),
                "total", 0
        )));
        secretKey = "ThisIsASecretKey";
    }

    @After
    public void tearDown() {
        userTokenValidDuration = "1h";
    }

    @Test
    public void indexPageSessionContainsBearerTokenWhenLogonSucceeds() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest(59));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(result.session().get("offenderApiBearerToken")).isEqualTo("bearerToken");
    }

    @Test
    public void analyticsSearchIndexEventRecordedWhenLogonSucceeds() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(FAKE_USER_BEARKER_TOKEN));
        route(app, buildIndexPageRequest(59));

        verify(analyticsStore).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getValue()).containsKeys("correlationId", "sessionId", "type", "username", "dateTime");
        assertThat(analyticsEventCaptor.getValue()).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getValue()).contains(entry("type", "search-index"));
    }

    @Test
    public void returnsServerErrorWhenLogonFails() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(supplyAsync(() -> { throw new RuntimeException("boom"); }));

        val result = route(app, buildIndexPageRequest(59));

        assertThat(result.status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void searchTermReturnsResults() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", FAKE_USER_BEARKER_TOKEN).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"offenders\":[],\"suggestions\":[],\"total\":0}", contentAsString(result));
    }

    @Test
    public void analyticsSearchRequestEventRecordedBeforeAndAfterWhenSearchCalled() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", FAKE_USER_BEARKER_TOKEN).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith");
        route(app, request);

        verify(analyticsStore, times(2)).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("type", "search-request"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("correlationId", "999-aaa-888"));

        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("type", "search-results"));
        assertEquals(0, analyticsEventCaptor.getAllValues().get(1).get("total"));
        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("correlationId", "999-aaa-888"));

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
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli()), secretKey), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenIsALittleBitInTheFutureDueToMachineTimeDriftReturns200Response() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest(59);
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenInFuture30MinsIsOk() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest(30);
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenInPast30MinsIsOk() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest(-30);
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenInFuture61MinsIsError() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest(61);
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void timeTokenInPast61MinsIsError() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildIndexPageRequest(-61);
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void validUserAndOldTimeTokenReturns401Response() throws UnsupportedEncodingException {
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val overAnHourAgo = String.valueOf(Instant.now().minus(61, MINUTES).toEpochMilli());
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

    @Test
    public void recordsSearchOutcomeEventWithData() {
        val request = new Http.
                RequestBuilder().
                method(POST).
                session("offenderApiBearerToken", FAKE_USER_BEARKER_TOKEN).
                session("searchAnalyticsGroupId", "999-aaa-888").
                uri("/nationalSearch/recordSearchOutcome").
                bodyJson(Json.toJson(ImmutableMap.of("type", "search-offender-details", "rankIndex", 23)));
        val result = route(app, request);

        assertEquals(CREATED, result.status());

        verify(analyticsStore).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getValue()).containsKeys("correlationId", "sessionId", "type", "username", "dateTime", "rankIndex");
        assertThat(analyticsEventCaptor.getValue()).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getValue()).contains(entry("type", "search-offender-details"));
        assertThat(analyticsEventCaptor.getValue()).contains(entry("rankIndex", 23));
        assertThat(analyticsEventCaptor.getValue()).contains(entry("correlationId", "999-aaa-888"));
    }



    private Http.RequestBuilder buildIndexPageRequest(int minutesDrift) throws UnsupportedEncodingException {

        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli() + (1000 * 60 * minutesDrift)), secretKey), "UTF-8");

        return new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
    }

    private static Map.Entry<String, Object> entry(String key, Object value) {
        return new SimpleImmutableEntry<>(key, value);
    }


    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(OffenderSearch.class).toInstance(elasticOffenderSearch),
                bind(OffenderApi.class).toInstance(offenderApi),
                bind(AnalyticsStore.class).toInstance(analyticsStore)
            )
            .configure("params.user.token.valid.duration", userTokenValidDuration)
            .build();
    }
}