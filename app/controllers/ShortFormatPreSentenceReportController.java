package controllers;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.WizardController;
import data.ShortFormatPreSentenceReportData;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import static helpers.FluentHelper.not;
import static helpers.FluentHelper.value;

public class ShortFormatPreSentenceReportController extends WizardController<ShortFormatPreSentenceReportData>
{
    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;

    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  WebJarsUtil webJarsUtil,
                                                  Config configuration,
                                                  Environment environment,
                                                  EncryptedFormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore) {

        super(ec, webJarsUtil, configuration, environment, formFactory, ShortFormatPreSentenceReportData.class, "views.html.shortFormatPreSentenceReport.page");

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {

        return super.initialParams().thenCompose(params -> {

            val originalData = Optional.ofNullable(params.get("documentId")). // Retrieve JSON as Map<String, String>
                    map(decrypter).
                    map(documentId -> documentStore.retrieveOriginalData(documentId, params.get("onBehalfOfUser"))).
                    map(originalJson -> originalJson.thenApply(JsonHelper::jsonToMap)); //@TODO: Re-encrypt

            return originalData.orElse(CompletableFuture.supplyAsync(() -> {

                params.put("pncSupplied", Boolean.valueOf(!Strings.isNullOrEmpty(params.get("pnc"))).toString());
                return params;

            }, ec.current()));
        });
    }

    @Override
    protected Map<String, String> modifyParams(Map<String, String> params) {

        Consumer<String> encryptParam = key -> Optional.ofNullable(params.get(key)).map(value -> params.put(key, encrypter.apply(value)));

        if ("1".equals(params.get("pageNumber")) && "false".equals(params.get("pncSupplied"))) {

            encryptParam.accept("pnc");
        }

        if ("2".equals(params.get("pageNumber"))) {

            encryptParam.accept("court");
            encryptParam.accept("dateOfHearing");
            encryptParam.accept("localJusticeArea");
        }

        return params;
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
                        data.getOnBehalfOfUser(),
                        data.getCrn(),
                        data.getEntityId()

                ).thenApply(stored -> Optional.ofNullable(stored.get("ID")).
                        filter(not(Strings::isNullOrEmpty)).map(value(result)))).
*/
                thenApply(result -> result.map(bytes -> ok(

                        views.html.shortFormatPreSentenceReport.completed.render(
                                String.format("PDF Created - %d bytes", bytes.length),
                                Base64.getEncoder().encodeToString(ArrayUtils.toPrimitive(bytes)),
                                webJarsUtil
                        )))

                .orElse(wizardFailed(data)));
    }
}
