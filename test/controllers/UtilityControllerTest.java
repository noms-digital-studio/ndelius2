package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import interfaces.AnalyticsStore;
import interfaces.OffenderSearch;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class UtilityControllerTest extends WithApplication {

    @Rule
    public  WireMockRule wireMock = new WireMockRule(8080);

    @Mock
    private AnalyticsStore analyticsStore;

    @Mock
    private OffenderSearch offenderSearch;

    @Before
    public void setup() {
        stubPdfGeneratorWithStatus("OK");
        stubDocumentStoreToReturn(ok());
        stubOffenderApiToReturn(ok());
        when(analyticsStore.isUp()).thenReturn(CompletableFuture.supplyAsync(() -> true));
        when(offenderSearch.isHealthy()).thenReturn(CompletableFuture.supplyAsync(() -> true));
    }

    @Test
    public void healthEndpointIncludesCorrectSections() throws IOException {

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

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenPdfGeneratorIsHealthy() throws IOException {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("pdf-generator", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesFailedWhenPdfGeneratorIsUnhealthy() throws IOException {
        stubPdfGeneratorWithStatus("FAILED");
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("pdf-generator", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenDocumentStoreIsHealthy() throws IOException {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("document-store", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesFailedWhenDocumentStoreIsUnhealthy() throws IOException {
        stubDocumentStoreToReturn(serverError());
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("document-store", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenAnalyticsStoreIsHealthy() throws IOException {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("analytics-store", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenAnalyticsStoreIsUnhealthy() throws IOException {
        when(analyticsStore.isUp()).thenReturn(CompletableFuture.supplyAsync(() -> false));
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("analytics-store", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenElasticSearchIsHealthy() throws IOException {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("offender-search", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesFailedWhenElasticSearchIsUnhealthy() throws IOException {
        when(offenderSearch.isHealthy()).thenReturn(CompletableFuture.supplyAsync(() -> false));

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("offender-search", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesOkWhenOffenderApiIsHealthy() throws IOException {
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("offender-api", "OK"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void healthEndpointIndicatesFailedWhenOffenderApiIsUnhealthy() throws IOException {
        stubOffenderApiToReturn(serverError());

        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("offender-api", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("FAILED");
    }

    private Map<String, Object> convertToJson(Result result) throws IOException {

        val mapper = Json.mapper();
        return mapper.readValue(contentAsString(result),  new TypeReference<Map<String, Object>>() {});
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
            get(urlEqualTo("/api/healthcheck"))
                .willReturn(response));
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
            .overrides(
                bind(AnalyticsStore.class).toInstance(analyticsStore),
                bind(OffenderSearch.class).toInstance(offenderSearch)
            )
            .build();
    }

}
