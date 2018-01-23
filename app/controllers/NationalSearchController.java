package controllers;

import helpers.JsonHelper;
import interfaces.OffenderSearch;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class NationalSearchController extends Controller {

    private final views.html.nationalSearch template;
    private final OffenderSearch offenderSearch;

    @Inject
    public NationalSearchController(views.html.nationalSearch template, OffenderSearch offenderSearch) {
        this.template = template;
        this.offenderSearch = offenderSearch;
    }

    public Result index() {
        return ok(template.render());
    }

    public CompletionStage<Result> searchOffender(String searchTerm, int pageSize, int pageNumber) {
        return offenderSearch.search(searchTerm, pageSize, pageNumber).thenApply(JsonHelper::okJson);
    }

}
