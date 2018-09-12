package controllers;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import interfaces.HealthCheckResult;
import interfaces.OffenderSearch;
import interfaces.PrisonerApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class UtilityControllerTest extends WithApplication {

    @Rule
    public  WireMockRule wireMock = new WireMockRule(wireMockConfig().port(8080).jettyStopTimeout(10000L));

    @Mock
    private AnalyticsStore analyticsStore;

    @Mock
    private OffenderSearch offenderSearch;

    @Mock
    private PrisonerApi prisonerApi;

    @Before
    public void setup() {
        stubPdfGeneratorWithStatus("OK");
        stubDocumentStoreToReturn(ok("{}"));
        stubOffenderApiToReturn(ok("{}"));
        when(analyticsStore.isUp()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::healthy));
        when(offenderSearch.isHealthy()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::healthy));
        when(prisonerApi.isHealthy()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::healthy));
    }

    @Test
    public void healthEndpointIncludesCorrectSections() {

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);
        val jsonResult = convertToJson(result);

        assertEquals(OK, result.status());
        assertThat(jsonResult).containsOnlyKeys(
            "dateTime",
            "fileSystems",
            "localHost",
            "runtime",
            "version",
            "status",
            "dependencies");
    }

    @Test
    public void healthEndpointIndicatesOkWhenPdfGeneratorIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("pdf-generator", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWithDetailWhenPdfGeneratorIsHealthy() {
        stubPdfGeneratorWithStatus("OK");

        val request = new RequestBuilder().method(GET).uri("/healthcheck?detail=true");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependenciesWithDetail(result))
                .contains(entry(
                        "pdf-generator",
                        ImmutableMap.of("healthy", Boolean.TRUE, "detail", ImmutableMap.of("status", "OK"))));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesFailedWhenPdfGeneratorIsUnhealthy() {
        stubPdfGeneratorWithStatus("FAILED");
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("pdf-generator", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @Test
    public void healthEndpointIndicatesOkWhenDocumentStoreIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("document-store", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWithDetailWhenDocumentStoreIsHealthy() {
        stubDocumentStoreToReturn(ok("detail is ignored"));

        val request = new RequestBuilder().method(GET).uri("/healthcheck?detail=true");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependenciesWithDetail(result))
                .contains(entry(
                        "document-store",
                        ImmutableMap.of("healthy", Boolean.TRUE, "detail", "none")));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesFailedWhenDocumentStoreIsUnhealthy() {
        stubDocumentStoreToReturn(serverError());
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("document-store", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @Test
    public void healthEndpointIndicatesOkWhenAnalyticsStoreIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("analytics-store", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWithDetailWhenAnalyticsStoreIsHealthy() {
        when(analyticsStore.isUp()).thenReturn(CompletableFuture.supplyAsync(() -> HealthCheckResult.healthy("some detail")));
        val request = new RequestBuilder().method(GET).uri("/healthcheck?detail=true");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependenciesWithDetail(result))
                .contains(entry(
                        "analytics-store",
                        ImmutableMap.of("healthy", Boolean.TRUE, "detail", "some detail")));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWhenAnalyticsStoreIsUnhealthy() {
        when(analyticsStore.isUp()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::unhealthy));
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("analytics-store", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWhenElasticSearchIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("offender-search", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWithDetailWhenElasticSearchIsHealthy() {
        when(offenderSearch.isHealthy()).thenReturn(CompletableFuture.supplyAsync(() -> HealthCheckResult.healthy("some detail")));
        val request = new RequestBuilder().method(GET).uri("/healthcheck?detail=true");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependenciesWithDetail(result))
                .contains(entry(
                        "offender-search",
                        ImmutableMap.of("healthy", Boolean.TRUE, "detail", "some detail")));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesFailedWhenElasticSearchIsUnhealthy() {
        when(offenderSearch.isHealthy()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::unhealthy));

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("offender-search", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @Test
    public void healthEndpointIndicatesOkWhenPrisonerApiIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("prisoner-api", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesOkWithDetailWhenPrisonerApiIsHealthy() {
        when(prisonerApi.isHealthy()).thenReturn(CompletableFuture.supplyAsync(() -> HealthCheckResult.healthy("some detail")));
        val request = new RequestBuilder().method(GET).uri("/healthcheck?detail=true");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependenciesWithDetail(result))
                .contains(entry(
                        "prisoner-api",
                        ImmutableMap.of("healthy", Boolean.TRUE, "detail", "some detail")));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesFailedWhenPrisonerApiIsUnhealthy() {
        when(prisonerApi.isHealthy()).thenReturn(CompletableFuture.supplyAsync(HealthCheckResult::unhealthy));

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("prisoner-api", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @Test
    public void healthEndpointIndicatesOkWhenOffenderApiIsHealthy() {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("offender-api", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @Test
    public void healthEndpointIndicatesFailedWhenOffenderApiIsUnhealthy() {
        stubOffenderApiToReturn(serverError());

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat(dependencies(result)).contains(entry("offender-api", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    private Map<String, Object> convertToJson(Result result) {

        return JsonHelper.jsonToObjectMap(contentAsString(result));
    }

    private void stubPdfGeneratorWithStatus(String status) {
        wireMock.stubFor(
            get(urlEqualTo("/healthcheck"))
                .willReturn(
                    okForContentType("application/json", format("{\"status\": \"%s\"}", status))));
    }

    private void stubDocumentStoreToReturn(ResponseDefinitionBuilder response) {
        wireMock.stubFor(
            get(urlEqualTo("/alfresco/service/noms-spg/notificationStatus"))
                .willReturn(response));
    }

    private void stubOffenderApiToReturn(ResponseDefinitionBuilder response) {
        wireMock.stubFor(
            get(urlEqualTo("/api/health"))
                .willReturn(response));
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
            .overrides(
                bind(AnalyticsStore.class).toInstance(analyticsStore),
                bind(OffenderSearch.class).toInstance(offenderSearch),
                bind(PrisonerApi.class).toInstance(prisonerApi),
                bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                bind(MongoClient.class).toInstance(mock(MongoClient.class))
            )
            .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> dependenciesWithDetail(Result result) {
        return (Map<String, Map<String, Object>>) convertToJson(result).get("dependencies");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> dependencies(Result result) {
        return (Map<String, Object>) convertToJson(result).get("dependencies");
    }


}
