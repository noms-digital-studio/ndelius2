package controllers;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import lombok.val;
import org.joda.time.DateTime;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static helpers.JwtHelper.principal;

public class NationalSearchController extends Controller {

    private static final String OFFENDER_API_BEARER_TOKEN = "offenderApiBearerToken";
    private static final String SEARCH_ANALYTICS_GROUP_ID = "searchAnalyticsGroupId";
    private final views.html.nationalSearch template;
    private final OffenderSearch offenderSearch;
    private final Duration userTokenValidDuration;
    private final String paramsSecretKey;
    private final OffenderApi offenderApi;
    private final AnalyticsStore analyticsStore;
    private final HttpExecutionContext ec;


    @Inject
    public NationalSearchController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.nationalSearch template,
            OffenderSearch offenderSearch,
            OffenderApi offenderApi,
            AnalyticsStore analyticsStore) {
        this.template = template;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
        paramsSecretKey = configuration.getString("params.secret.key");
        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");
        this.ec = ec;
        this.analyticsStore = analyticsStore;
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        val username = Encryption.decrypt(encryptedUsername, paramsSecretKey);
        Logger.info("AUDIT:{}: About to login {}", "anonymous", username);

        return validate(encryptedUsername, encryptedEpochRequestTimeMills, username)
            .orElseGet(() -> offenderApi.logon(username)
                .thenApplyAsync(bearerToken -> {
                    Logger.info("AUDIT:{}: Successful logon for user {}", principal(bearerToken), username);
                    session(OFFENDER_API_BEARER_TOKEN, bearerToken);
                    session(SEARCH_ANALYTICS_GROUP_ID, UUID.randomUUID().toString());
                    analyticsStore.recordEvent(combine(analyticsContext(), "type", "search-index"));
                    return ok(template.render());
                }, ec.current())
                .exceptionally(e -> {
                    Logger.info("AUDIT:{}: Unable to login {}", "anonymous", username);
                    Logger.error("Unable to logon to offender API", e);
                    return internalServerError();
                }));
    }

    public CompletionStage<Result> searchOffender(String searchTerm, int pageSize, int pageNumber) {
        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN))
                .map(bearerToken -> {
                    Logger.info("AUDIT:{}: Search performed with term '{}'", principal(bearerToken), searchTerm);
                    analyticsStore.recordEvent(combine(analyticsContext(), "type", "search-request"));
                    return offenderSearch.search(bearerToken, searchTerm, pageSize, pageNumber).thenApply(JsonHelper::okJson);
                })
                .orElseGet(() -> CompletableFuture.supplyAsync(() -> {
                    Logger.warn("Unauthorized search attempted for search term '{}'. No Offender API bearer token found in session", searchTerm);
                    return Results.unauthorized();
                }));
    }

    public CompletionStage<Result>  recordSearchOutcome() {
        analyticsStore.recordEvent(combine(analyticsContext(), JsonHelper.jsonToObjectMap(request().body().asJson())));
        return CompletableFuture.supplyAsync(Results::created);
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

    private Map<String, Object> analyticsContext() {
        return combine(
                ImmutableMap.of(
                        "correlationId", session(SEARCH_ANALYTICS_GROUP_ID),
                        "username", principal(session(OFFENDER_API_BEARER_TOKEN)),
                        "sessionId", Optional.ofNullable(session("id")).orElseGet(() -> UUID.randomUUID().toString())

                ),
                "dateTime",
                DateTime.now().toDate()
        );
    }

    private Map<String, Object> combine(Map<String, Object> map, String key, Object value) {
        return ImmutableMap.<String, Object>builder().putAll(map).put(key, value).build();
    }
    private Map<String, Object> combine(Map<String, Object> map, Map<String, Object> other) {
        return ImmutableMap.<String, Object>builder().putAll(map).putAll(other).build();
    }
}
