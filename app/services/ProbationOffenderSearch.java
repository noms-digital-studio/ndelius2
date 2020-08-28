package services;

import com.google.common.collect.ImmutableMap;
import data.CourtDefendant;
import data.MatchedOffenders;
import interfaces.HealthCheckResult;
import interfaces.OffenderSearch;
import services.helpers.SearchQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.parse;

public class ProbationOffenderSearch implements OffenderSearch {
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
        return CompletableFuture.completedFuture(HealthCheckResult.healthy());
    }

    @Override
    public CompletionStage<MatchedOffenders> findMatch(String bearerToken, CourtDefendant offender) {
        return CompletableFuture.completedFuture(MatchedOffenders.noMatch());
    }
}
