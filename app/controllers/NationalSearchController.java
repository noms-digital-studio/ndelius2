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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static controllers.SessionKeys.OFFENDER_API_BEARER_TOKEN;
import static controllers.SessionKeys.USERNAME;
import static helpers.FluentHelper.not;
import static helpers.JwtHelper.principal;
import static helpers.JwtHelper.probationAreaCodes;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.MUST;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.SHOULD;

public class NationalSearchController extends Controller implements ParamsValidator {

    private static final String SEARCH_ANALYTICS_GROUP_ID = "searchAnalyticsGroupId";

    private final views.html.nationalSearch template;
    private final views.html.nationalSearchMaintenance maintenanceTemplate;
    private final OffenderSearch offenderSearch;
    private final OffenderApi offenderApi;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final boolean inMaintenanceMode;
    private final Config configuration;


    @Inject
    public NationalSearchController(
            HttpExecutionContext ec,
            Config configuration,
            views.html.nationalSearch template,
            views.html.nationalSearchMaintenance maintenanceTemplate,
            OffenderSearch offenderSearch,
            OffenderApi offenderApi) {

        this.ec = ec;
        this.template = template;
        this.maintenanceTemplate = maintenanceTemplate;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
        this.configuration = configuration;

        inMaintenanceMode = configuration.getBoolean("maintenance.offender.search");

        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey).orElse("");
    }

    @Override
    public Config getConfiguration() {
        return configuration;
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
            session(USERNAME, username);
            session(SEARCH_ANALYTICS_GROUP_ID, UUID.randomUUID().toString());

            return bearerToken;

        }, ec.current())
        .thenCompose(bearerToken -> offenderApi.probationAreaDescriptions(bearerToken, probationAreaCodes(bearerToken))
        .thenApplyAsync(template::render, ec.current()))
        .thenApply(Results::ok);

        Logger.info("AUDIT:{}: About to login {}", "anonymous", username);

        final Runnable errorReporter = () -> Logger.error(String.format("National search request did not receive a valid user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
        return invalidCredentials(username, epochRequestTime, errorReporter).
                map(result -> (CompletionStage<Result>) CompletableFuture.completedFuture(result)).
                orElseGet(renderedPage).
                exceptionally(throwable -> {

                    Logger.info("AUDIT:{}: Unable to login {}", "anonymous", username);
                    Logger.error("Unable to logon to offender API", throwable);

                    return internalServerError();
                });
    }

    public CompletionStage<Result> searchOffender(String searchTerm, String searchType, Optional<String> areasFilter, int pageSize, int pageNumber) {

        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN)).map(bearerToken -> {

            Logger.info("AUDIT:{}: Search performed with term '{}'", principal(bearerToken), searchTerm);
            return offenderSearch.search(bearerToken,
                                            toList(areasFilter),
                                            searchTerm,
                                            pageSize,
                                            pageNumber,
                                            "exact".equals(searchType) ? MUST : SHOULD).
                                thenApply(JsonHelper::okJson).
                                thenApply(result -> result.withHeader(CACHE_CONTROL, "no-cache, no-store, must-revalidate")).
                                thenApply(result -> result.withHeader(PRAGMA, "no-cache")).
                                thenApply(result -> result.withHeader(EXPIRES, "0"));


        }).orElseGet(() -> {

            Logger.warn("Unauthorized search attempted for search term '{}'. No Offender API bearer token found in session", searchTerm);
            return CompletableFuture.completedFuture(Results.unauthorized());
        });
    }

    private List<String> toList(Optional<String> areasFilter) {
        return areasFilter
                .map(filter -> Arrays.stream(filter.split(",")).filter(not(String::isEmpty)).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

}
