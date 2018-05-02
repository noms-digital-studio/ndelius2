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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Predicates.not;
import static helpers.JwtHelper.principal;
import static helpers.JwtHelper.probationAreaCodes;

public class NationalSearchController extends Controller {

    private static final String OFFENDER_API_BEARER_TOKEN = "offenderApiBearerToken";
    private static final String SEARCH_ANALYTICS_GROUP_ID = "searchAnalyticsGroupId";

    private final views.html.nationalSearch template;
    private final views.html.nationalSearchMaintenance maintenanceTemplate;
    private final OffenderSearch offenderSearch;
    private final Duration userTokenValidDuration;
    private final OffenderApi offenderApi;
    private final AnalyticsStore analyticsStore;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final boolean inMaintenanceMode;
    private final int recentSearchMinutes;

    @Inject
    public NationalSearchController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.nationalSearch template,
            views.html.nationalSearchMaintenance maintenanceTemplate,
            OffenderSearch offenderSearch,
            OffenderApi offenderApi,
            AnalyticsStore analyticsStore) {

        this.ec = ec;
        this.template = template;
        this.maintenanceTemplate = maintenanceTemplate;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
        this.analyticsStore = analyticsStore;

        recentSearchMinutes = configuration.getInt("recent.search.minutes");
        inMaintenanceMode = configuration.getBoolean("maintenance.offender.search");
        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");

        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        if (inMaintenanceMode) {
            return CompletableFuture.completedFuture(ok(maintenanceTemplate.render()));
        }

        val username = decrypter.apply(encryptedUsername);

        final Supplier<CompletionStage<Result>> renderedPage = () -> offenderApi.logon(username).thenApplyAsync(bearerToken -> {

            Logger.info("AUDIT:{}: Successful logon for user {}", principal(bearerToken), username);

            session(OFFENDER_API_BEARER_TOKEN, bearerToken);
            session(SEARCH_ANALYTICS_GROUP_ID, UUID.randomUUID().toString());

            analyticsStore.recordEvent(combine(analyticsContext(), "type", "search-index"));
            return bearerToken;

        }, ec.current())
        .thenCompose(bearerToken -> offenderApi.probationAreaDescriptions(bearerToken, probationAreaCodes(bearerToken))
        .thenApplyAsync(probationAreas -> ok(template.render(recentSearchMinutes, probationAreas)), ec.current()));

        Logger.info("AUDIT:{}: About to login {}", "anonymous", username);

        return invalidCredentials(encryptedUsername, encryptedEpochRequestTimeMills, username).
                map(result -> (CompletionStage<Result>) CompletableFuture.completedFuture(result)).
                orElseGet(renderedPage).
                exceptionally(throwable -> {

                    Logger.info("AUDIT:{}: Unable to login {}", "anonymous", username);
                    Logger.error("Unable to logon to offender API", throwable);

                    return internalServerError();
                });
    }

    public CompletionStage<Result> searchOffender(String searchTerm, Optional<String> areasFilter, int pageSize, int pageNumber) {

        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN)).map(bearerToken -> {

            Logger.info("AUDIT:{}: Search performed with term '{}'", principal(bearerToken), searchTerm);
            analyticsStore.recordEvent(combine(analyticsContext(), ImmutableMap.of(
                    "type", "search-request",
                    "filter", ImmutableMap.of(
                            "myProviderCount", (long)probationAreaCodes(bearerToken).size(),
                            "myProviderSelectedCount", myProviderCount(probationAreaCodes(bearerToken), toList(areasFilter)),
                            "otherProviderSelectedCount", otherProviderCount(probationAreaCodes(bearerToken), toList(areasFilter))
                            ))));

            return offenderSearch.search(bearerToken,
                                            toList(areasFilter),
                                            searchTerm,
                                            pageSize,
                                            pageNumber).
                                thenApplyAsync(this::recordSearchResultsAnalytics, ec.current()).
                                thenApply(JsonHelper::okJson);

        }).orElseGet(() -> {

            Logger.warn("Unauthorized search attempted for search term '{}'. No Offender API bearer token found in session", searchTerm);
            return CompletableFuture.completedFuture(Results.unauthorized());
        });
    }

    private long myProviderCount(List<String> myProviders, List<String> filter) {
        return filter.stream().filter(myProviders::contains).count();
    }

    private long otherProviderCount(List<String> myProviders, List<String> filter) {
        return filter.stream().filter(not(myProviders::contains)).count();
    }

    private List<String> toList(Optional<String> areasFilter) {
        return areasFilter
                .map(filter -> Arrays.stream(filter.split(",")).filter(not(String::isEmpty)).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

    public Result recordSearchOutcome() {

        analyticsStore.recordEvent(combine(analyticsContext(), JsonHelper.jsonToObjectMap(request().body().asJson())));
        return created();
    }

    private Optional<Result> invalidCredentials(String encryptedUsername, String encryptedEpochRequestTimeMills, String username) {

        val epochRequestTime = decrypter.apply(encryptedEpochRequestTimeMills);

        if (username == null || epochRequestTime == null) {

            Logger.error(String.format("Request did not receive user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
            return Optional.of(badRequest("one or both of 'user' or 't' not supplied"));
        }

        val timeNowInstant = Instant.now();
        val epochRequestInstant = Instant.ofEpochMilli(Long.valueOf(epochRequestTime));

        if (Math.abs(timeNowInstant.toEpochMilli() - epochRequestInstant.toEpochMilli()) > userTokenValidDuration.toMillis()) {
            Logger.warn(String.format(
                    "Request not authorised because time currently is %s but token time %s",
                    timeNowInstant.toString(),
                    epochRequestInstant.toString()));

            return Optional.of(Results.unauthorized());
        }

        return Optional.empty();
    }

    private Map<String, Object> analyticsContext() {

        return ImmutableMap.of(
                "correlationId", session(SEARCH_ANALYTICS_GROUP_ID),
                "username", principal(session(OFFENDER_API_BEARER_TOKEN)),
                "sessionId", Optional.ofNullable(session("id")).orElseGet(() -> UUID.randomUUID().toString()),
                "dateTime", DateTime.now().toDate()
        );
    }

    private Map<String, Object> recordSearchResultsAnalytics(Map<String, Object> results) {

        analyticsStore.recordEvent(combine(analyticsContext(), ImmutableMap.of("type", "search-results", "total", results.get("total"))));
        return results;
    }

    private Map<String, Object> combine(Map<String, Object> map, String key, Object value) {
        return ImmutableMap.<String, Object>builder().putAll(map).put(key, value).build();
    }

    private Map<String, Object> combine(Map<String, Object> map, Map<String, Object> other) {
        return ImmutableMap.<String, Object>builder().putAll(map).putAll(other).build();
    }
}
