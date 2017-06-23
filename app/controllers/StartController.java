package controllers;

import play.mvc.*;

public class StartController extends Controller {

    public Result startReport() {
        return ok(views.html.startReport.render());
    }
}
