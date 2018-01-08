package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import interfaces.AnalyticsStore;
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
import utils.SimpleAnalyticsStoreMock;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
    private Supplier<Boolean> isMonogoDbUp;


    @Before
    public void setup() {
        stubPdfGeneratorWithStatus("OK");
        stubDocumentStoreToReturn(ok());
        when(isMonogoDbUp.get()).thenReturn(true);
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
        stubDocumentStoreToReturn(serviceUnavailable());
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
        when(isMonogoDbUp.get()).thenReturn(false);
        val request = new RequestBuilder().method(GET).uri("/healthcheck");

        val result = route(app, request);

        assertEquals(OK, result.status());
        assertThat((Map<String, Object>) convertToJson(result).get("dependencies")).contains(entry("analytics-store", "FAILED"));
        assertThat(convertToJson(result).get("status")).isEqualTo("OK");
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
            get(urlEqualTo("/alfresco/service/noms-spg/"))
                .willReturn(response));
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(AnalyticsStore.class).toInstance(new AnalyticsStoreMock())
            )
            .build();
    }

    class AnalyticsStoreMock extends SimpleAnalyticsStoreMock {
        @Override
        public CompletableFuture<Boolean> isUp() {
            return CompletableFuture.supplyAsync(isMonogoDbUp);
        }
    }

}
