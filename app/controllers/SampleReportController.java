package controllers;

import controllers.base.WizardController;
import data.SampleReportData;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import play.Environment;
import play.Logger;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

public class SampleReportController extends WizardController<SampleReportData>
{
    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;

    @Inject
    public SampleReportController(HttpExecutionContext ec,
                                  Environment environment,
                                  FormFactory formFactory,
                                  PdfGenerator pdfGenerator,
                                  DocumentStore documentStore) {

        super(ec, environment, formFactory, SampleReportData.class, "views.html.sampleReport.page");

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
    }

    @Override
    protected CompletionStage<Result> completedWizard(SampleReportData sampleReportData) {

        sampleReportData.setReportDate(new SimpleDateFormat("dd MMMM yyy").format(new Date()));

        Logger.info("Sample Report Data: " + sampleReportData);

        return pdfGenerator.generate("helloWorld", sampleReportData).thenApply(result -> {

//              documentStore.uploadNewPdf(result, "sampleReport.pdf", "someUser", "crn", "author", 12345);
                return ok(ArrayUtils.toPrimitive(result)).as("application/pdf");
        });
    }
}
