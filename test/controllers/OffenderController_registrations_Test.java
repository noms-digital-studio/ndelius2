package controllers;

import com.mongodb.rx.client.MongoClient;
import helpers.JsonHelper;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.PrisonerApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
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
public class OffenderController_registrations_Test extends WithApplication implements ResourceLoader {
    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setUp() {
        when(offenderApi.getOffenderRegistrationsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderRegistrations.json")));
    }
    @Test
    public void registrationsReturnedForOffenderInSession() {
        when(offenderApi.getOffenderRegistrationsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderRegistrations.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/registrations");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).contains("\"description\":\"Risk to Known Adult\"");
    }


    @Test
    public void registrationsReturnedAreFilteredLeavingActiveOnes() {
        val originalJson = loadJsonResource("/deliusoffender/offenderRegistrations.json");
        assertThat(originalJson.size()).isEqualTo(13);

        when(offenderApi.getOffenderRegistrationsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(originalJson));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/registrations");

        val result = route(app, request);
        val content = Json.parse(Helpers.contentAsString(result));

        // 3 inactive records removed
        assertThat(content.size()).isEqualTo(10);
    }


    @Test
    public void registrationsRetrievedUsingOffenderValueInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/registrations");

        route(app, request);

        verify(offenderApi).getOffenderRegistrationsByOffenderId(JwtHelperTest.generateToken(), "123");
    }

    @Test
    public void badRequestWhenOffenderNotInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .method(GET)
                .uri("/offender/registrations");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(BAD_REQUEST);
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(OffenderApi.class).toInstance(offenderApi),
                        bind(PrisonerApi.class).toInstance(mock(PrisonerApi.class)),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "1h")
                .build();
    }

}