package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import interfaces.AnalyticsStore;
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

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackController_sfpsr_Test extends WithApplication {

    @Mock
    private AnalyticsStore analyticsStore;

    @Before
    public void beforeEach() {
        when(analyticsStore.sfpsrFeedback())
                .thenReturn(CompletableFuture.completedFuture(ImmutableList.of(
                        ImmutableMap.of(
                                "dateTime", new Date(),
                                "feedback", "It is really good"),
                        ImmutableMap.of(
                                "dateTime", new Date(),
                                "feedback", "It is rubbish"),
                        ImmutableMap.of(
                                "dateTime", new Date(),
                                "feedback", "Not bothered")
                )));

    }

    @Test
    public void rendersFeedbackWithCorrectCredentials() {
        val result = route(app, addCSRFToken(new Http.RequestBuilder()
                .method(GET)
                .header("Authorization", String.format("Basic %s", credentials("andymarke", "secret")))
                .uri("/feedback/sfpsr")));

        assertThat(result.status()).isEqualTo(OK);
    }

    @Test
    public void renderedPageIncludesRowsForEachFeedback() {
        val result = route(app, addCSRFToken(new Http.RequestBuilder()
                .method(GET)
                .header("Authorization", String.format("Basic %s", credentials("andymarke", "secret")))
                .uri("/feedback/sfpsr")));

        val content = Helpers.contentAsString(result);

        assertThat(content).contains("It is really good");
        assertThat(content).contains("It is rubbish");
        assertThat(content).contains("Not bothered");
    }

    @Test
    public void userIsChallengedForCredentialsWhenNotSupplied() {
        val result = route(app, addCSRFToken(new Http.RequestBuilder()
                .method(GET)
                .uri("/feedback/sfpsr")));

        assertThat(result.status()).isEqualTo(UNAUTHORIZED);
        assertThat(result.header(WWW_AUTHENTICATE).isPresent()).isTrue();
        assertThat(result.header(WWW_AUTHENTICATE).get()).isEqualTo("Basic realm=Feedback");
    }

    @Test
    public void invalidUsernameInCredentialsAreRejected() {
        val result = route(app, addCSRFToken(new Http.RequestBuilder()
                .method(GET)
                .header("Authorization", String.format("Basic %s", credentials("bobby", "secret")))
                .uri("/feedback/sfpsr")));

        assertThat(result.status()).isEqualTo(UNAUTHORIZED);
        assertThat(result.header(WWW_AUTHENTICATE).isPresent()).isFalse();
    }

    @Test
    public void invalidPasswordInCredentialsAreRejected() {
        val result = route(app, addCSRFToken(new Http.RequestBuilder()
                .method(GET)
                .header("Authorization", String.format("Basic %s", credentials("andymarke", "bananas")))
                .uri("/feedback/sfpsr")));

        assertThat(result.status()).isEqualTo(UNAUTHORIZED);
        assertThat(result.header(WWW_AUTHENTICATE).isPresent()).isFalse();
    }


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(AnalyticsStore.class).toInstance(analyticsStore),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("auth.feedback.user", "andymarke")
                .configure("auth.feedback.password", "secret")
                .build();
    }


    private String credentials(String username, String password) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());
    }

}
