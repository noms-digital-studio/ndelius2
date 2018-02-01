package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NationalSearchController extends Controller {

    private static final String OFFENDER_API_BEARER_TOKEN = "offenderApiBearerToken";
    private final views.html.nationalSearch template;
    private final OffenderSearch offenderSearch;
    private final Duration userTokenValidDuration;
    private final String paramsSecretKey;
    private final OffenderApi offenderApi;
    private final HttpExecutionContext ec;


    @Inject
    public NationalSearchController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.nationalSearch template,
            OffenderSearch offenderSearch,
            OffenderApi offenderApi) {
        this.template = template;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
        paramsSecretKey = configuration.getString("params.secret.key");
        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");
        this.ec = ec;
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        val username = Encryption.decrypt(encryptedUsername, paramsSecretKey);

        return validate(encryptedUsername, encryptedEpochRequestTimeMills, username)
            .orElseGet(() -> offenderApi.logon(username)
                .thenApplyAsync(bearerToken -> {
                    Logger.info("Successful logon to API for user {}", username);
                    session(OFFENDER_API_BEARER_TOKEN, bearerToken);
                    return ok(template.render());
                }, ec.current())
                .exceptionally(e -> {
                    Logger.error("Unable to logon to offender API", e);
                    return internalServerError();
                }));
    }

    public CompletionStage<Result> searchOffender(String searchTerm, int pageSize, int pageNumber) {
        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN))
                .map(bearerToken -> offenderSearch.search(bearerToken, searchTerm, pageSize, pageNumber).thenApply(JsonHelper::okJson))
                .orElse(CompletableFuture.supplyAsync(Results::unauthorized));
    }

    private Optional<CompletionStage<Result>> validate(String encryptedUsername, String encryptedEpochRequestTimeMills, String username) {
        val epochRequestTime = Encryption.decrypt(encryptedEpochRequestTimeMills, paramsSecretKey);

        if (username == null || epochRequestTime == null) {
            Logger.error(String.format("Request did not receive user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
            return Optional.of(CompletableFuture.supplyAsync(() -> badRequest("no 'user' or 't' supplied")));
        }

        if (Duration.between(toLocalDateTime(epochRequestTime), LocalDateTime.now()).compareTo(userTokenValidDuration) > 0) {
            Logger.warn(String.format(
                    "Request not authorised because time currently is %s but token time %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    toLocalDateTime(epochRequestTime).format(DateTimeFormatter.ISO_DATE_TIME)));
            return Optional.of(CompletableFuture.supplyAsync(Results::unauthorized));
        }

        return Optional.empty();
    }

    private LocalDateTime toLocalDateTime(String epochRequestTime) {
        return Instant.ofEpochMilli(Long.valueOf(epochRequestTime)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
