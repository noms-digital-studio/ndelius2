package controllers;

import interfaces.AnalyticsStore;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import views.html.analytics;

public class AnalyticsController extends Controller {

    private final AnalyticsStore analyticsStore;
    private final analytics template;

    @Inject
    public AnalyticsController(AnalyticsStore analyticsStore, analytics template) {

        this.analyticsStore = analyticsStore;
        this.template = template;
    }

    public Result index() {

        return ok(template.render());
    }

    public CompletionStage<Result> recentEvents(int limit) {

        return analyticsStore.recentEvents(limit).thenApply(documents -> ok(Json.toJson(documents)));
    }
}
