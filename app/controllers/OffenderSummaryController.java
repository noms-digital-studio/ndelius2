package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;
import interfaces.OffenderApi;
import lombok.val;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static controllers.SessionKeys.*;
import static helpers.JwtHelper.principal;

public class OffenderSummaryController extends Controller implements ParamsValidator {

    private final views.html.offenderSummary template;
    private final views.html.offenderNotAccessibleSummary notAccessibleTemplate;
    private final OffenderApi offenderApi;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final Config configuration;


    @Inject
    public OffenderSummaryController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.offenderSummary template,
            views.html.offenderNotAccessibleSummary notAccessibleTemplate,
            OffenderApi offenderApi) {

        this.ec = ec;
        this.template = template;
        this.notAccessibleTemplate = notAccessibleTemplate;
        this.offenderApi = offenderApi;
        this.configuration = configuration;

        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey).orElse("");
    }

    @Override
    public Config getConfiguration() {
        return configuration;
    }

    public CompletionStage<Result> index(String offenderId, Optional<String> maybeEncryptedUsername, Optional<String> maybeEncryptedEpochRequestTimeMills) {
        final Optional<String> maybeUsername = maybeEncryptedUsername
                .map(encryptedUsername -> decryptUsername(encryptedUsername, maybeEncryptedEpochRequestTimeMills))
                .orElseGet(() -> Optional.ofNullable(session(USERNAME)));

        Logger.info("AUDIT:{}: About to login {}", "anonymous", maybeUsername.orElse("unknown"));

        return maybeUsername.
                map(logonAndRenderPage(offenderId)).
                orElseGet(() -> CompletableFuture.completedFuture(badRequest("either username supplied or active session required"))).
                exceptionally(throwable -> {

                    Logger.info("AUDIT:{}: Unable to login {}", "anonymous", maybeUsername.orElse("unknown"));
                    Logger.error("Unable to logon to offender API", throwable);

                    return internalServerError();
                });
    }

    private Function<String, CompletionStage<Result>> logonAndRenderPage(String offenderId) {
        return (username) -> offenderApi.logon(username).thenApplyAsync(bearerToken -> {

            Logger.info("AUDIT:{}: Successful logon for user {}", principal(bearerToken), username);

            session(OFFENDER_API_BEARER_TOKEN, bearerToken);
            session(USERNAME, username);

            return bearerToken;

        }, ec.current())
        .thenCompose(bearerToken -> offenderApi.canAccess(bearerToken, Long.valueOf(offenderId)))
        .thenApply(accessible -> {
            if (accessible) {
                session(OFFENDER_ID, offenderId);
            }
            return accessible;
        })
        .thenApply(accessible -> accessible ? template.render() : notAccessibleTemplate.render())
        .thenApply(Results::ok);
    }

    private Optional<String> decryptUsername(String encryptedUsername, Optional<String> maybeEncryptedEpochRequestTimeMills) {
        val username = decrypter.apply(encryptedUsername);

        return maybeEncryptedEpochRequestTimeMills.map(encryptedEpochRequestTimeMills -> {
            val epochRequestTimeMills = decrypter.apply(encryptedEpochRequestTimeMills);

            final Runnable errorReporter = () -> Logger.error(String.format("Offender summary request did not receive a valid user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));


            return invalidCredentials(username, epochRequestTimeMills, errorReporter).map(notUsed -> Optional.<String>empty()).orElse(Optional.of(username));
        }).orElse(Optional.empty());

    }


}
