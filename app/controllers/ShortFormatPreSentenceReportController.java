package controllers;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import controllers.base.WizardController;
import data.ShortFormatPreSentenceReportData;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import play.Environment;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import static helpers.FluentHelper.not;
import static helpers.FluentHelper.value;

public class ShortFormatPreSentenceReportController extends WizardController<ShortFormatPreSentenceReportData>
{
    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;
    private final Function<String, String> decrypter;

    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  Environment environment,
                                                  FormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore,
                                                  Config configuration) {

        super(ec, environment, formFactory, ShortFormatPreSentenceReportData.class, "views.html.shortFormatPreSentenceReport.page");

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;

        val paramsSecretKey = configuration.getString("params.secret.key");
        this.decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {

        return super.initialParams().thenCompose(params -> {

            val originalData = Optional.ofNullable(params.get("documentId")). // Retrieve JSON as Map<String, String>
                    map(decrypter).
                    map(documentId -> documentStore.retrieveOriginalData(documentId, decrypter.apply(params.get("onBehalfOfUser")))).
                    map(originalJson -> originalJson.thenApply(JsonHelper::jsonToMap));

            return originalData.orElse(CompletableFuture.supplyAsync(() -> params, ec.current()));
        });
    }

    @Override
    protected CompletionStage<Result> completedWizard(ShortFormatPreSentenceReportData data) {

        Logger.info("Short Format Pre Sentence Report Data: " + data);

        return pdfGenerator.generate("helloWorld", data).
                thenApply(Optional::of).
/*
                thenCompose(result -> documentStore.uploadNewPdf(
                        result,
                        "shortFormatPreSentenceReport.pdf",
                        Json.stringify(Json.toJson(data)),
                        "someUser",
                        data.getCrn(),
                        12345

                ).thenApply(stored -> Optional.ofNullable(stored.get("ID")).
                        filter(not(Strings::isNullOrEmpty)).map(value(result)))).
*/
                thenApply(result -> result.map(bytes -> ok(

                        views.html.shortFormatPreSentenceReport.completed.render(
                                String.format("PDF Created - %d bytes", bytes.length),
                                Base64.getEncoder().encodeToString(ArrayUtils.toPrimitive(bytes))
                        )))

                .orElse(wizardFailed(data)));
    }
}
