package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import helpers.Encryption;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
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
import services.helpers.SearchQueryBuilder.QUERY_TYPE;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static helpers.JwtHelperTest.*;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
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

    @Captor
    private ArgumentCaptor<List<String>> probationAreasFilter;

    @Captor
    private ArgumentCaptor<List<String>> probationAreasCode;

    @Before
    public void setUp() {

        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        when(offenderApi.probationAreaDescriptions(any(), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("N01", "N01 Area", "N02", "N02 Area")));
        when(elasticOffenderSearch.search(any(), any(),  any(), anyInt(), anyInt(), any(QUERY_TYPE.class))).thenReturn(completedFuture(ImmutableMap.of(
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
        assertThat(result.session().get("offenderApiBearerToken")).isEqualTo(JwtHelperTest.generateToken());
    }

    @Test
    public void indexPageRetrievedProbationAreaDescriptionsFromJWTProbationAreas() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(JwtHelperTest.generateTokenWithProbationAreaCodes(Arrays.asList("N02", "N03"))));
        val result = route(app, buildIndexPageRequest(59));

        verify(offenderApi).probationAreaDescriptions(any(), probationAreasCode.capture());
        assertThat(probationAreasCode.getValue()).containsExactly("N02", "N03");
    }

    @Test
    public void indexPageIsRenderedWithProbationAreas() throws UnsupportedEncodingException {
        when(offenderApi.probationAreaDescriptions(any(), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("N01", "N01 Area", "N02", "N02 Area")));
        val result = route(app, buildIndexPageRequest(59));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(contentAsString(result)).contains("{\"N01\":\"N01 Area\",\"N02\":\"N02 Area\"}");
    }

    @Test
    public void analyticsSearchIndexEventRecordedWhenLogonSucceeds() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(generateTokenWithSubject("cn=fake.user,cn=Users,dc=moj,dc=com")));
        route(app, buildIndexPageRequest(59));

        verify(analyticsStore).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getValue()).containsKeys("correlationId", "sessionId", "type", "username", "dateTime");
        assertThat(analyticsEventCaptor.getValue()).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getValue()).contains(entry("type", "search-index"));
    }

    @Test
    public void analyticsContainClientAgentData() throws UnsupportedEncodingException {
        when(offenderApi.logon(any())).thenReturn(CompletableFuture.completedFuture(generateToken()));
        route(app, buildIndexPageRequest(59).header(
                "User-Agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3"));

        verify(analyticsStore).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getValue()).containsKey("client");
        val client = (Map<String, Map<String, String>>)analyticsEventCaptor.getValue().get("client");
        assertThat(client).containsKeys("device", "os", "user_agent");
        assertThat(client.get("user_agent")).contains(
                entry("family", "Mobile Safari"),
                entry("major", "5"),
                entry("minor", "1"),
                entry("patch", "")
        );
        assertThat(client.get("device")).contains(
                entry("family", "iPhone")
        );
        assertThat(client.get("os")).contains(
                entry("family", "iOS"),
                entry("major", "5"),
                entry("minor", "1"),
                entry("patch", "1"),
                entry("patch_minor", "")
        );
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
                session("offenderApiBearerToken", generateToken()).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=broad");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"offenders\":[],\"suggestions\":[],\"total\":0}", contentAsString(result));
    }

    @Test
    public void analyticsSearchRequestEventRecordedBeforeAndAfterWhenSearchCalled() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", generateTokenWithSubject("cn=fake.user,cn=Users,dc=moj,dc=com")).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=exact");
        route(app, request);

        verify(analyticsStore, times(2)).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("type", "search-request"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("searchType", "exact"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("correlationId", "999-aaa-888"));

        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("username", "cn=fake.user,cn=Users,dc=moj,dc=com"));
        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("type", "search-results"));
        assertEquals(0, analyticsEventCaptor.getAllValues().get(1).get("total"));
        assertThat(analyticsEventCaptor.getAllValues().get(1)).contains(entry("correlationId", "999-aaa-888"));

    }

    @Test
    public void zeroFilterCountsRecordedWithSearchRequestAnalyticsEventWhenNoFilterPresent() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", generateTokenWithProbationAreaCodes(ImmutableList.of("N01", "N02"))).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=broad&areasFilter=");
        route(app, request);


        verify(analyticsStore, atLeastOnce()).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("type", "search-request"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("searchType", "broad"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("filter", ImmutableMap.of("myProviderSelectedCount", 0L, "otherProviderSelectedCount", 0L, "myProviderCount", 2L)));

    }

    @Test
    public void filterCountsRecordedAgainstProviderCategoriesWithSearchRequestAnalyticsEvent() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", generateTokenWithProbationAreaCodes(ImmutableList.of("N01", "N02"))).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=broad&areasFilter=N01,N02,N03,N04,N05");
        route(app, request);


        verify(analyticsStore, atLeastOnce()).recordEvent(analyticsEventCaptor.capture());

        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("type", "search-request"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("searchType", "broad"));
        assertThat(analyticsEventCaptor.getAllValues().get(0)).contains(entry("filter", ImmutableMap.of("myProviderSelectedCount", 2L, "otherProviderSelectedCount", 3L, "myProviderCount", 2L)));

    }

    @Test
    public void searchingWithoutABearerTokenReturns401() {
        val request = new Http.RequestBuilder().method(GET).uri("/searchOffender/smith?searchType=broad");
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

        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli()), secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

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
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        val overAnHourAgo = String.valueOf(Instant.now().minus(61, MINUTES).toEpochMilli());
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(overAnHourAgo, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void timeTokenValidDurationIsDeterminedByConfig() throws UnsupportedEncodingException {
        userTokenValidDuration = "101d";
        stopPlay();
        startPlay();

        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        val overAnHourAgo = String.valueOf(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(overAnHourAgo, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

        val request = new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void recordsSearchOutcomeEventWithData() {
        val request = new Http.
                RequestBuilder().
                method(POST).
                session("offenderApiBearerToken", generateTokenWithSubject("cn=fake.user,cn=Users,dc=moj,dc=com")).
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


    @Test
    public void areaFilterIsConvertedToListForSearchService() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", generateToken()).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=broad&areasFilter=N01,N02,N03");
        val result = route(app, request);

        verify(elasticOffenderSearch).search(anyString(), probationAreasFilter.capture(), anyString(), anyInt(), anyInt(), any(QUERY_TYPE.class));

        assertThat(probationAreasFilter.getValue()).containsExactlyInAnyOrder("N01", "N02", "N03");
    }

    @Test
    public void areaFilterDefaultIsEmptyForSearchService() {
        val request = new Http.RequestBuilder().
                session("offenderApiBearerToken", generateToken()).
                session("searchAnalyticsGroupId", "999-aaa-888").
                method(GET).uri("/searchOffender/smith?searchType=broad");
        val result = route(app, request);

        verify(elasticOffenderSearch).search(anyString(), probationAreasFilter.capture(), anyString(), anyInt(), anyInt(), any(QUERY_TYPE.class));

        assertThat(probationAreasFilter.getValue()).isEmpty();
    }

    private Http.RequestBuilder buildIndexPageRequest(int minutesDrift) throws UnsupportedEncodingException {

        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli() + (1000 * 60 * minutesDrift)), secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");

        return new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
    }

    private static <T, V> Map.Entry<T, V> entry(T key, V value) {
        return new SimpleImmutableEntry<>(key, value);
    }


    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(OffenderSearch.class).toInstance(elasticOffenderSearch),
                bind(OffenderApi.class).toInstance(offenderApi),
                bind(AnalyticsStore.class).toInstance(analyticsStore),
                bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                bind(MongoClient.class).toInstance(mock(MongoClient.class))
            )
            .configure("params.user.token.valid.duration", userTokenValidDuration)
            .build();
    }
}