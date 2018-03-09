package controllers;

import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class AnalyticsController extends Controller {

    private final AnalyticsStore analyticsStore;
    private final views.html.analytics template;

    @Inject
    public AnalyticsController(AnalyticsStore analyticsStore, views.html.analytics template) {

        this.analyticsStore = analyticsStore;
        this.template = template;
    }

    public Result index() {

        return ok(template.render());
    }

    public CompletionStage<Result> recentEvents(int limit) {

        return analyticsStore.recentEvents(limit).thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> pageVisits() {

        return analyticsStore.pageVisits().thenApply(JsonHelper::okJson);
    }
}
