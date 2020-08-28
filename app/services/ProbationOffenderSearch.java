package services;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.CourtDefendant;
import data.MatchedOffenders;
import interfaces.HealthCheckResult;
import interfaces.OffenderSearch;
import lombok.val;
import play.Logger;
import play.cache.AsyncCacheApi;
import play.libs.ws.WSClient;
import services.helpers.SearchQueryBuilder;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.libs.Json.parse;
import static play.mvc.Http.Status.OK;

public class ProbationOffenderSearch implements OffenderSearch {
    private final WSClient wsClient;
    private final AsyncCacheApi cache;
    private final int cacheTime;
    private final String apiBaseUrl;


    @Inject
    public ProbationOffenderSearch(Config configuration, WSClient wsClient, AsyncCacheApi cache) {
        apiBaseUrl = configuration.getString("probation.offender.search.url");
        this.wsClient = wsClient;
        cacheTime = configuration.getInt("offender.api.probationAreas.cache.time.seconds");
        this.cache = cache;
    }

    @Override
    public CompletionStage<Map<String, Object>> search(String bearerToken, List<String> probationAreasFilter, String searchTerm, int pageSize, int pageNumber, SearchQueryBuilder.QUERY_TYPE queryType) {
        return CompletableFuture.completedFuture(ImmutableMap.of(
                "offenders", parse("[]"),
                "total", 0,
                "suggestions", parse("{}")
        ));
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return wsClient.url(apiBaseUrl + "health/ping").
                get().
                thenApply(wsResponse -> {
                    val healthy = wsResponse.getStatus() == OK;
                    if (!healthy) {
                        Logger.warn("probation offender search response status: " + wsResponse.getStatus());
                        return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                    }
                    return healthy(wsResponse.asJson());
                }).
                exceptionally(throwable -> {
                    Logger.error("Error while checking probation offender search connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }

    @Override
    public CompletionStage<MatchedOffenders> findMatch(String bearerToken, CourtDefendant offender) {
        return CompletableFuture.completedFuture(MatchedOffenders.noMatch());
    }
}
