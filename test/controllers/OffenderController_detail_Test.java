package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.rx.client.MongoClient;
import helpers.JwtHelperTest;
import interfaces.OffenderApi;
import interfaces.PrisonerApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

@RunWith(MockitoJUnitRunner.class)
public class OffenderController_detail_Test extends WithApplication implements ResourceLoader {
    @Mock
    private OffenderApi offenderApi;

    @Test
    public void detailReturnedForOffenderInSession() {
        when(offenderApi.getOffenderDetailByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offender.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/detail");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).contains("\"firstName\":\"John\"");
    }

    @Test
    public void nomisImageReferenceAddedToResponse() {
        when(offenderApi.getOffenderDetailByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offender.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/detail");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).contains("\"oneTimeNomisRef\":");
    }

    @Test
    public void nomisImageReferenceNotAddedToResponseWhenNoNOMSNumber() {
        when(offenderApi.getOffenderDetailByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(removeNOMSNumber(loadJsonResource("/deliusoffender/offender.json"))));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/detail");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).doesNotContain("\"oneTimeNomisRef\":");
    }

    @Test
    public void offenderRetrievedUsingValueInSession() {
        when(offenderApi.getOffenderDetailByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offender.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/detail");

        route(app, request);

        verify(offenderApi).getOffenderDetailByOffenderId(JwtHelperTest.generateToken(), "123");
    }

    @Test
    public void badRequestWhenOffenderNotInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .method(GET)
                .uri("/offender/detail");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(BAD_REQUEST);
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(OffenderApi.class).toInstance(offenderApi),
                        bind(PrisonerApi.class).toInstance(mock(PrisonerApi.class)),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "1h")
                .build();
    }

    private static JsonNode removeNOMSNumber(JsonNode node) {
        val offender = ObjectNode.class.cast(node);
        ObjectNode.class.cast(offender.get("otherIds")).remove("nomsNumber");
        return node;
    }


}