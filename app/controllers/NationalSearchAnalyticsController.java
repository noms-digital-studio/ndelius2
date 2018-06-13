package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class NationalSearchAnalyticsController extends Controller {

    private final AnalyticsStore analyticsStore;
    private final views.html.nationalSearchAnalytics template;

    @Inject
    public NationalSearchAnalyticsController(AnalyticsStore analyticsStore, views.html.nationalSearchAnalytics template) {

        this.analyticsStore = analyticsStore;
        this.template = template;
    }

    public Result index() {

        return ok(template.render());
    }

    public CompletionStage<Result> filterCounts(String from) {
        return analyticsStore.filterCounts(fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> uniqueUserVisits(String from) {
        return analyticsStore.uniquePageVisits("search-index", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> allVisits(String from) {
        return analyticsStore.pageVisits("search-index", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> allSearches(String from) {
        return analyticsStore.pageVisits("search-request", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> rankGrouping(String from) {
        return analyticsStore.rankGrouping("search-offender-details", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> durationBetweenStartEndSearch(String from) {
        return analyticsStore.durationBetween("search-request", "search-offender-details", fromDateTime(from), 60).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> eventOutcome(String from) {
        return analyticsStore.eventOutcome("search-index", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> searchFieldMatch(String from) {
        return analyticsStore.countGroupingArray("search-offender-details", "fieldMatch", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> userAgentTypeCounts(String from) {
        return analyticsStore.userAgentTypeCounts("search-index", fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> searchTypeCounts(String from) {
        return analyticsStore.searchTypeCounts(fromDateTime(from)).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> satisfaction() {
        return analyticsStore.weeklySatisfactionScores()
            .thenApply(data -> JsonHelper.okJson(ImmutableMap.of("satisfactionCounts", data)));
    }

    private LocalDateTime fromDateTime(String from) {
        return Optional.ofNullable(from).
                map(text -> LocalDateTime.parse(text, ISO_OFFSET_DATE_TIME)).
                orElse(LocalDateTime.of(2017, 1, 1, 0, 0));
    }
}
