package controllers;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.offendersearch.OffenderSearchResult;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static helpers.JwtHelper.principal;

public class NationalSearchController extends Controller {

    private static final String OFFENDER_API_BEARER_TOKEN = "offenderApiBearerToken";
    private static final String SEARCH_ANALYTICS_GROUP_ID = "searchAnalyticsGroupId";
    private final views.html.nationalSearch template;
    private final OffenderSearch offenderSearch;
    private final Duration userTokenValidDuration;
    private final OffenderApi offenderApi;
    private final AnalyticsStore analyticsStore;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;

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
        this.ec = ec;
        this.analyticsStore = analyticsStore;

        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");

        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        val username = decrypter.apply(encryptedUsername);
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
                    return offenderSearch.search(bearerToken, searchTerm, pageSize, pageNumber).
                            thenApplyAsync(this::recordSearchResultsAnalytics, ec.current()).
                            thenApply(JsonHelper::okJson);
                })
                .orElseGet(() -> CompletableFuture.supplyAsync(() -> {
                    Logger.warn("Unauthorized search attempted for search term '{}'. No Offender API bearer token found in session", searchTerm);
                    return Results.unauthorized();
                }));
    }

    public CompletionStage<Result> recordSearchOutcome() {
        analyticsStore.recordEvent(combine(analyticsContext(), JsonHelper.jsonToObjectMap(request().body().asJson())));
        return CompletableFuture.supplyAsync(Results::created);
    }

    private OffenderSearchResult recordSearchResultsAnalytics(OffenderSearchResult results) {
        analyticsStore.recordEvent(combine(analyticsContext(), ImmutableMap.of("type", "search-results", "total", results.getTotal())));
        return results;
    }

    private Optional<CompletionStage<Result>> validate(String encryptedUsername, String encryptedEpochRequestTimeMills, String username) {
        val epochRequestTime = decrypter.apply(encryptedEpochRequestTimeMills);

        if (username == null || epochRequestTime == null) {
            Logger.error(String.format("Request did not receive user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
            return Optional.of(CompletableFuture.supplyAsync(() -> badRequest("no 'user' or 't' supplied")));
        }

        val timeNowInstant = Instant.now();
        val epochRequestInstant = Instant.ofEpochMilli(Long.valueOf(epochRequestTime));

        if (Math.abs(timeNowInstant.toEpochMilli() - epochRequestInstant.toEpochMilli()) > userTokenValidDuration.toMillis()) {
            Logger.warn(String.format(
                    "Request not authorised because time currently is %s but token time %s",
                    timeNowInstant.toString(),
                    epochRequestInstant.toString()));
            return Optional.of(CompletableFuture.supplyAsync(Results::unauthorized));
        }

        return Optional.empty();
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
