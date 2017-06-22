package controllers;

import controllers.base.WizardController;
import data.ShortFormatPreSentenceReportData;
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

public class ShortFormatPreSentenceReportController extends WizardController<ShortFormatPreSentenceReportData>
{
    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;

    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  Environment environment,
                                                  FormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore) {

        super(ec, environment, formFactory, ShortFormatPreSentenceReportData.class, "views.html.shortFormatPreSentenceReport.page");

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
    }

    @Override
    protected CompletionStage<Result> completedWizard(ShortFormatPreSentenceReportData shortFormatPreSentenceReportData) {

        shortFormatPreSentenceReportData.setReportDate(new SimpleDateFormat("dd MMMM yyy").format(new Date()));

        Logger.info("Short Format Pre Sentence Report Data: " + shortFormatPreSentenceReportData);

        return pdfGenerator.generate("helloWorld", shortFormatPreSentenceReportData).
//                thenCompose(result -> documentStore.uploadNewPdf(result, "shortFormatPreSentenceReport.pdf", "someUser", shortFormatPreSentenceReportData.getCrn(), "author", 12345).
//                        thenApply(map -> result)).
                thenApply(result -> ok(ArrayUtils.toPrimitive(result)).as("application/pdf"));
    }
}
