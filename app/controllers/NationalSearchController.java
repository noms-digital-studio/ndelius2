package controllers;

import javax.inject.Inject;
import play.mvc.*;
import views.html.nationalSearch;

public class NationalSearchController extends Controller {

    private final nationalSearch template;

    @Inject
    public NationalSearchController(nationalSearch template) {

        this.template = template;
    }

    public Result index() {

        return ok(template.render());
    }
}
