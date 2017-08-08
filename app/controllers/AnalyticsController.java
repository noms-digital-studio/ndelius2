package controllers;

import interfaces.AnalyticsStore;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class AnalyticsController extends Controller {

    private final AnalyticsStore analyticsStore;

    @Inject
    public AnalyticsController(AnalyticsStore analyticsStore) {

        this.analyticsStore = analyticsStore;
    }

    public CompletionStage<Result> recentEvents(int limit) {

        return analyticsStore.recentEvents(limit).thenApply(documents -> ok(Json.toJson(documents)));
    }
}
