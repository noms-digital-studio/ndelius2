package controllers;

import data.ReportData;
import lombok.val;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;

/**
 * Report form controller for Play Java
 */
public class ReportController extends Controller {

    private final Form<ReportData> reportForm;

    @Inject
    public ReportController(FormFactory formFactory) {
        this.reportForm = formFactory.form(ReportData.class);
    }

    public Result reportGet() {
        return ok(views.html.report.form.render(reportForm));
    }

    public Result reportPost() {

        val boundForm = reportForm.bindFromRequest();

        if (boundForm.hasErrors()) {
            return badRequest(views.html.report.form.render(boundForm));
        } else {
            flash("success", "Report " + boundForm.get());
            return redirect(routes.ReportController.reportGet());
        }
    }
}
