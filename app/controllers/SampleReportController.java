package controllers;

import controllers.base.WizardController;
import data.SampleReportData;
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

public class SampleReportController extends WizardController<SampleReportData>
{
    private final PdfGenerator pdfGenerator;

    @Inject
    public SampleReportController(HttpExecutionContext ec, Environment environment, FormFactory formFactory, PdfGenerator pdfGenerator) {

        super(ec, environment, formFactory, SampleReportData.class, "views.html.sampleReport.page");

        this.pdfGenerator = pdfGenerator;
    }

    @Override
    protected CompletionStage<Result> completedWizard(SampleReportData sampleReportData) {

        sampleReportData.setReportDate(new SimpleDateFormat("dd MMMM yyy").format(new Date()));

        return pdfGenerator.generate("helloWorld", sampleReportData).thenApply(result ->
                ok(ArrayUtils.toPrimitive(result)).as("application/pdf"));
    }
}
