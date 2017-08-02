package controllers;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.ReportGeneratorWizardController;
import data.ShortFormatPreSentenceReportData;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Content;

public class ShortFormatPreSentenceReportController extends ReportGeneratorWizardController<ShortFormatPreSentenceReportData>
{
    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  WebJarsUtil webJarsUtil,
                                                  Config configuration,
                                                  Environment environment,
                                                  EncryptedFormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore) {

        super(ec, webJarsUtil, configuration, environment, formFactory, ShortFormatPreSentenceReportData.class, pdfGenerator, documentStore);
    }

    @Override
    protected String templateName() {

        return "shortFormatPreSentenceReport";
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {

        return super.initialParams().thenApply(params -> {

            params.putIfAbsent("pncSupplied", Boolean.valueOf(!Strings.isNullOrEmpty(params.get("pnc"))).toString());
            return params;
        });
    }

    @Override
    protected Map<String, String> modifyParams(Map<String, String> params, Consumer<String> paramEncrypter) {

        if ("1".equals(params.get("pageNumber")) && "false".equals(params.get("pncSupplied"))) {

            paramEncrypter.accept("pnc");
        }

        if ("3".equals(params.get("pageNumber"))) {

            paramEncrypter.accept("court");
            paramEncrypter.accept("dateOfHearing");
            paramEncrypter.accept("localJusticeArea");
        }

        return params;
    }

    protected Content renderCancelledView() {

        return views.html.shortFormatPreSentenceReport.cancelled.render("Draft stored", webJarsUtil);
    }

    @Override
    protected Content renderCompletedView(Byte[] bytes) {

        return views.html.shortFormatPreSentenceReport.completed.render(
                String.format("PDF Created - %d bytes", bytes.length),
                Base64.getEncoder().encodeToString(ArrayUtils.toPrimitive(bytes)),
                webJarsUtil
        );
    }
}
