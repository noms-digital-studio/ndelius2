package controllers;

import controllers.base.WizardController;
import data.ReportData;
import interfaces.PdfGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import play.Environment;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

public class SampleReportController extends WizardController<ReportData>
{
    private final PdfGenerator pdfGenerator;

    @Inject
    public SampleReportController(HttpExecutionContext ec, Environment environment, FormFactory formFactory, PdfGenerator pdfGenerator) {

        super(ec, environment, formFactory, ReportData.class, "views.html.sampleReport.page");

        this.pdfGenerator = pdfGenerator;
    }

    @Override
    protected CompletionStage<Result> completedWizard(ReportData reportData) {

        reportData.setReportDate(new SimpleDateFormat("dd MMMM yyy").format(new Date()));

        return pdfGenerator.generate("helloWorld", reportData).thenApply(result ->
                ok(ArrayUtils.toPrimitive(result)).as("application/pdf"));
    }
}
