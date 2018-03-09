package controllers;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import interfaces.AnalyticsStore;
import lombok.val;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class FeedbackController extends Controller {

    private final AnalyticsStore analyticsStore;
    private final views.html.viewFeedback viewFeedbackTemplate;
    private final HttpExecutionContext ec;
    private final Config configuration;

    @Inject
    public FeedbackController(AnalyticsStore analyticsStore, views.html.viewFeedback viewFeedbackTemplate, HttpExecutionContext ec, Config configuration) {
        this.analyticsStore = analyticsStore;
        this.viewFeedbackTemplate = viewFeedbackTemplate;
        this.ec = ec;
        this.configuration = configuration;
    }

    public CompletionStage<Result> viewFeedback() {
        return request().header("Authorization")
                .map(authHeader ->
                        invalidCredentials(getTokenFromHeader(authHeader))
                            .map(result -> (CompletionStage<Result>) CompletableFuture.completedFuture(result))
                            .orElseGet(this::renderPage))
                .orElseGet(() -> CompletableFuture.completedFuture(unauthorized().withHeader(WWW_AUTHENTICATE, "Basic realm=Feedback")));
    }

    private CompletionStage<Result> renderPage() {
        return analyticsStore.feedback()
                .thenApplyAsync(feedback -> ok(viewFeedbackTemplate.render(
                        feedback, String.format(configuration.getString("ldap.string.format"), "(.*)"))), ec.current());
    }

    private Map<String, String> getTokenFromHeader(String authHeader) {
        val encoded_credentials = authHeader.substring(6);
        val decoded_credentials = Base64.getDecoder().decode(encoded_credentials);
        try {
            val credentials = new String(decoded_credentials, "UTF-8").split(":");
            return ImmutableMap.of("username", credentials[0], "password", credentials[1]);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException((e));
        }
    }

    private Optional<Result> invalidCredentials(Map<String, String> credentials) {
        if (!credentials.get("username").equals(configuration.getString("auth.feedback.user"))) {
            Logger.warn("AUDIT:{}: invalid username for view feedback", credentials.get("username"));
            return Optional.of(Results.unauthorized());
        }

        if (!credentials.get("password").equals(configuration.getString("auth.feedback.password"))) {
            Logger.warn("AUDIT:{}: invalid password for view feedback", credentials.get("username"));
            return Optional.of(Results.unauthorized());
        }

        Logger.info("AUDIT:{}: logged into to view feedback", credentials.get("username"));

        return Optional.empty();
    }
}
