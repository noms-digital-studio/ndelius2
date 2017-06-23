package controllers;

import lombok.val;
import play.mvc.*;

public class IntegrationController extends Controller {

    public Result index() {
        return ok(views.html.integration.index.render());
    }

    public Result initiate() {
        return ok(views.html.integration.initiate.render());
    }

    public Result completed() {

        return ok(views.html.integration.completed.render(request().body().asFormUrlEncoded().get("message")[0]));
    }
}
