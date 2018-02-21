package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import lombok.val;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    public CompletionStage<Result> visitCounts(String from) {
        val allVisits = analyticsStore.pageVisits("search-index", fromDateTime(from));
        val allSearches = analyticsStore.pageVisits("search-request", fromDateTime(from));
        val uniqueUserVisits = analyticsStore.uniquePageVisits("search-index", fromDateTime(from));
        val rankGrouping = analyticsStore.rankGrouping("search-offender-details", fromDateTime(from));
        val eventOutcome = analyticsStore.eventOutcome("search-index", fromDateTime(from));

        return CompletableFuture.allOf(allVisits, allSearches, uniqueUserVisits, rankGrouping, eventOutcome).
                thenApply(ignoredVoid -> ImmutableMap.of(
                        "uniqueUserVisits", uniqueUserVisits.join(),
                        "allVisits", allVisits.join(),
                        "allSearches", allSearches.join(),
                        "rankGrouping", rankGrouping.join(),
                        "eventOutcome", eventOutcome.join()
                        )).
                thenApply(JsonHelper::okJson);
    }

    private LocalDateTime fromDateTime(String from) {
        return Optional.ofNullable(from).
                map(text -> LocalDateTime.parse(text, ISO_OFFSET_DATE_TIME)).
                orElse(LocalDateTime.of(2017, 1, 1, 0, 0));
    }
}
