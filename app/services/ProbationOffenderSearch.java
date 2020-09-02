package services;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.CourtDefendant;
import data.MatchedOffenders;
import helpers.JsonHelper;
import helpers.JwtHelper;
import interfaces.HealthCheckResult;
import interfaces.OffenderApi;
import interfaces.OffenderSearch;
import interfaces.UserAwareApiToken;
import lombok.val;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import services.helpers.SearchQueryBuilder;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.HeaderNames.CONTENT_TYPE;
import static play.mvc.Http.MimeTypes.JSON;
import static play.mvc.Http.Status.OK;

public class ProbationOffenderSearch implements OffenderSearch {
    private final WSClient wsClient;
    private final String apiBaseUrl;
    private final UserAwareApiToken userAwareApiToken;
    private final OffenderApi offenderApi;


    @Inject
    public ProbationOffenderSearch(Config configuration, WSClient wsClient, UserAwareApiToken userAwareApiToken, OffenderApi offenderApi) {
        apiBaseUrl = configuration.getString("probation.offender.search.url");
        this.wsClient = wsClient;
        this.userAwareApiToken = userAwareApiToken;
        this.offenderApi = offenderApi;
    }

    @Override
    public CompletionStage<Map<String, Object>> search(String bearerToken, List<String> probationAreasFilter, String searchTerm, int pageSize, int pageNumber, SearchQueryBuilder.QUERY_TYPE queryType) {
        return userAwareApiToken.get(JwtHelper.username(bearerToken))
                .thenCompose(token -> wsClient
                        .url(String.format("%sphrase", apiBaseUrl))
                        .addQueryParameter("page", String.valueOf(pageNumber - 1))
                        .addQueryParameter("size", String.valueOf(pageSize))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .addHeader(CONTENT_TYPE, JSON)
                        .post(JsonHelper.stringify(ImmutableMap.of(
                                "phrase", searchTerm,
                                "matchAllTerms", queryType == SearchQueryBuilder.QUERY_TYPE.MUST,
                                "probationAreasFilter", probationAreasFilter)))
                        .thenApply(this::assertOkResponse)
                        .thenApply(WSResponse::asJson)
                        .thenApply(JsonHelper::jsonToObjectMap)
                        .thenApply(body -> ImmutableMap.of(
                                "offenders", body.get("content"),
                                "aggregations", getProbationAreaAggregations(body),
                                "total", body.get("totalElements"),
                                "suggestions", body.get("suggestions")
                        ))
                        .thenCompose(body -> withProbationAreaDescriptions(bearerToken, body)));
    }

    private CompletionStage<Map<String, Object>> withProbationAreaDescriptions(String bearerToken, ImmutableMap<String, Object> body) {
        Map<String, List<Map<String, Object>>> aggregationsWrapper = aggregationsWrapper(body);
        return offenderApi.probationAreaDescriptions(bearerToken,
                extractProbationAreaCodes(aggregationsWrapper))
                .thenApply(areas -> ImmutableMap.of(
                        "offenders", body.get("offenders"),
                        "aggregations", addProbationAreaDescription(aggregationsWrapper, areas),
                        "total", body.get("total"),
                        "suggestions", body.get("suggestions")
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

    private WSResponse assertOkResponse(WSResponse response) {
        if (response.getStatus() != OK) {
            Logger.error("{} API bad response {}", "search", response.getStatus());
            throw new RuntimeException(String.format("Unable to call %s. Status = %d", "search", response.getStatus()));
        }
        return response;
    }

    private List<String> extractProbationAreaCodes(Map<String, List<Map<String, Object>>> aggregationsWrapper) {
        List<Map<String, Object>> aggregations = aggregationsWrapper.get("byProbationArea");
        return aggregations.stream().map(area -> area.get("code")).map(Object::toString).collect(Collectors.toList());
    }

    private Map<String, List<Map<String, Object>>> getProbationAreaAggregations(Map<String, Object> body) {
        List<Map<String, Object>> aggregations = probationAggregationsFromResults(body);

        return ImmutableMap.of("byProbationArea", aggregations.stream().map(areaAggregation -> ImmutableMap.of(
                "code", areaAggregation.get("code"),
                "count", areaAggregation.get("count")
        )).collect(Collectors.toList()));
    }

    private Map<String, List<Map<String, Object>>> addProbationAreaDescription(Map<String, List<Map<String, Object>>> aggregationsWrapper, Map<String, String> descriptions) {
        List<Map<String, Object>> aggregations = aggregationsWrapper.get("byProbationArea");

        return ImmutableMap.of("byProbationArea", aggregations.stream().map(areaAggregation -> {
            String code = areaAggregation.get("code").toString();
            return ImmutableMap.of(
                    "code", code,
                    "count", areaAggregation.get("count"),
                    "description", descriptions.get(code)
            );
        }).collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, Object>>> aggregationsWrapper(ImmutableMap<String, Object> body) {
        return (Map<String, List<Map<String, Object>>>) body.get("aggregations");
    }
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> probationAggregationsFromResults(Map<String, Object> body) {
        return (List<Map<String, Object>>) body.get("probationAreaAggregations");
    }

}
