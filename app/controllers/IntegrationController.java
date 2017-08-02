package controllers;

import org.webjars.play.WebJarsUtil;
import play.mvc.*;

import javax.inject.Inject;

public class IntegrationController extends Controller {

    private final WebJarsUtil webJarsUtil;

    @Inject
    public IntegrationController(WebJarsUtil webJarsUtil) {

        this.webJarsUtil = webJarsUtil;
    }

    public Result index() {
        return ok(views.html.integration.index.render(webJarsUtil));
    }

    public Result initiate() {
        return ok(views.html.integration.initiate.render(webJarsUtil));
    }

    public Result completed() {

        return ok(views.html.integration.completed.render(request().body().asFormUrlEncoded().get("message")[0], webJarsUtil));
    }
}
