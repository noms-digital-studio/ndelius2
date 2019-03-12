package controllers;

import com.mongodb.rx.client.MongoClient;
import helpers.JwtHelperTest;
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
public class OffenderController_convictions_Test extends WithApplication implements ResourceLoader {
    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setUp() {
        when(offenderApi.getOffenderConvictionsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderConvictions.json")));
    }
    @Test
    public void convictionsReturnedForOffenderInSession() {
        when(offenderApi.getOffenderConvictionsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderConvictions.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/convictions");

        val result = route(app, request);
        val content = Json.parse(Helpers.contentAsString(result));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content.size()).isEqualTo(20);
    }




    @Test
    public void convictionsRetrievedUsingOffenderValueInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/convictions");

        route(app, request);

        verify(offenderApi).getOffenderConvictionsByOffenderId(JwtHelperTest.generateToken(), "123");
    }

    @Test
    public void badRequestWhenOffenderNotInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .method(GET)
                .uri("/offender/convictions");

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

}