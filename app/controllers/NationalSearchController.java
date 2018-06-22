package controllers;

import com.github.coveo.ua_parser.Parser;
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
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static controllers.SessionKeys.OFFENDER_API_BEARER_TOKEN;
import static helpers.FluentHelper.not;
import static helpers.JwtHelper.principal;
import static helpers.JwtHelper.probationAreaCodes;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.MUST;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.SHOULD;

public class NationalSearchController extends Controller {

    private static final String SEARCH_ANALYTICS_GROUP_ID = "searchAnalyticsGroupId";

    private final views.html.nationalSearch template;
    private final views.html.nationalSearchMaintenance maintenanceTemplate;
    private final OffenderSearch offenderSearch;
    private final OffenderApi offenderApi;
    private final AnalyticsStore analyticsStore;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final boolean inMaintenanceMode;
    private final int recentSearchMinutes;
    private final ParamsValidator paramsValidator;

    @Inject
    public NationalSearchController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.nationalSearch template,
            views.html.nationalSearchMaintenance maintenanceTemplate,
            OffenderSearch offenderSearch,
            OffenderApi offenderApi,
            AnalyticsStore analyticsStore,
            ParamsValidator paramsValidator) {

        this.ec = ec;
        this.template = template;
        this.maintenanceTemplate = maintenanceTemplate;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
        this.analyticsStore = analyticsStore;
        this.paramsValidator = paramsValidator;

        recentSearchMinutes = configuration.getInt("recent.search.minutes");
        inMaintenanceMode = configuration.getBoolean("maintenance.offender.search");

        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey).orElse("");
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        if (inMaintenanceMode) {
            return CompletableFuture.completedFuture(ok(maintenanceTemplate.render()));
        }

        val username = decrypter.apply(encryptedUsername);
        val epochRequestTime = decrypter.apply(encryptedEpochRequestTimeMills);

        final Supplier<CompletionStage<Result>> renderedPage = () -> offenderApi.logon(username).thenApplyAsync(bearerToken -> {

            Logger.info("AUDIT:{}: Successful logon for user {}", principal(bearerToken), username);

            session(OFFENDER_API_BEARER_TOKEN, bearerToken);
            session(SEARCH_ANALYTICS_GROUP_ID, UUID.randomUUID().toString());

            analyticsStore.recordEvent(ImmutableMap.<String, Object>builder()
                    .put("type", "search-index")
                    .putAll(analyticsContext())
                    .putAll(agentAnalytics(request())).build());
            return bearerToken;

        }, ec.current())
        .thenCompose(bearerToken -> offenderApi.probationAreaDescriptions(bearerToken, probationAreaCodes(bearerToken))
        .thenApplyAsync(probationAreas -> ok(template.render(recentSearchMinutes, probationAreas)), ec.current()));

        Logger.info("AUDIT:{}: About to login {}", "anonymous", username);

        final Runnable errorReporter = () -> Logger.error(String.format("National search request did not receive a valid user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
        return paramsValidator.invalidCredentials(username, epochRequestTime, errorReporter).
                map(result -> (CompletionStage<Result>) CompletableFuture.completedFuture(result)).
                orElseGet(renderedPage).
                exceptionally(throwable -> {

                    Logger.info("AUDIT:{}: Unable to login {}", "anonymous", username);
                    Logger.error("Unable to logon to offender API", throwable);

                    return internalServerError();
                });
    }

    private Map<String, Object> agentAnalytics(Http.Request request) {
        return request.getHeaders().get(USER_AGENT).map(userAgent -> {
            try {
                Parser uaParser = new Parser();
                return ImmutableMap.<String, Object>of(
                        "client",
                        JsonHelper.jsonToObjectMap(uaParser.parse(userAgent).toString()));
            } catch (Exception e) {
                Logger.warn("Unable to parse client agent", e);
                return ImmutableMap.<String, Object>of();
            }

        }).orElseGet(ImmutableMap::of);
    }

    public CompletionStage<Result> searchOffender(String searchTerm, String searchType, Optional<String> areasFilter, int pageSize, int pageNumber) {

        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN)).map(bearerToken -> {

            Logger.info("AUDIT:{}: Search performed with term '{}'", principal(bearerToken), searchTerm);
            analyticsStore.recordEvent(combine(analyticsContext(), ImmutableMap.of(
                    "type", "search-request",
                    "filter", ImmutableMap.of(
                            "myProviderCount", (long)probationAreaCodes(bearerToken).size(),
                            "myProviderSelectedCount", myProviderCount(probationAreaCodes(bearerToken), toList(areasFilter)),
                            "otherProviderSelectedCount", otherProviderCount(probationAreaCodes(bearerToken), toList(areasFilter))
                            ),
                    "searchType", searchType)));

            return offenderSearch.search(bearerToken,
                                            toList(areasFilter),
                                            searchTerm,
                                            pageSize,
                                            pageNumber,
                                            "exact".equals(searchType) ? MUST : SHOULD).
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

    private Map<String, Object> combine(Map<String, Object> map, Map<String, Object> other) {
        return ImmutableMap.<String, Object>builder().putAll(map).putAll(other).build();
    }
}
