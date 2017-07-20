package controllers.base;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import data.base.ReportGeneratorWizardData;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.val;
import org.springframework.cglib.beans.BeanMap;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import play.twirl.api.Content;

import static helpers.FluentHelper.not;
import static helpers.FluentHelper.value;

public abstract class ReportGeneratorWizardController<T extends ReportGeneratorWizardData> extends WizardController<T> {

    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;

    protected ReportGeneratorWizardController(HttpExecutionContext ec,
                                              WebJarsUtil webJarsUtil,
                                              Config configuration,
                                              Environment environment,
                                              EncryptedFormFactory formFactory,
                                              Class<T> wizardType,
                                              PdfGenerator pdfGenerator,
                                              DocumentStore documentStore) {

        super(ec, webJarsUtil, configuration, environment, formFactory, wizardType);

        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {

        return super.initialParams().thenCompose(params ->

                originalData(params).orElse(CompletableFuture.supplyAsync(() -> params, ec.current()))
        );
    }

    @Override
    protected final CompletionStage<Result> completedWizard(T data) {

        return pdfGenerator.generate(templateName(), data).
                thenApply(Optional::of).
/*
                thenCompose(result -> storeReport(data, result).thenApply(stored ->

                    Optional.ofNullable(stored.get("ID")).filter(not(Strings::isNullOrEmpty)).map(value(result)))
                ).
*/
                thenApply(result -> result.map(bytes -> ok(renderCompletedView(bytes))).orElse(wizardFailed(data)));
    }

    protected abstract String templateName();

    protected abstract Content renderCompletedView(Byte[] bytes);

    private Optional<CompletionStage<Map<String, String>>> originalData(Map<String, String> params) {

        return Optional.ofNullable(params.get("documentId")).
                map(documentId -> documentStore.retrieveOriginalData(documentId, params.get("onBehalfOfUser"))).
                map(originalJson -> originalJson.thenApply(json -> JsonHelper.jsonToMap(Json.parse(json).get("values")))).
                map(originalInfo -> originalInfo.thenApply(info -> {

                    info.put("onBehalfOfUser", params.get("onBehalfOfUser"));
                    info.put("documentId", params.get("documentId"));

                    return info;
                }));
    }

    private CompletionStage<Map<String, String>> storeReport(T data, Byte[] document) {

        val filename = templateName() + ".pdf";
        val metaData = Json.stringify(Json.toJson(ImmutableMap.of(
                "templateName", templateName(),
                "values", BeanMap.create(data)
        )));

        if (Strings.isNullOrEmpty(data.getDocumentId())) {

            return documentStore.uploadNewPdf(
                    document,
                    filename,
                    data.getOnBehalfOfUser(),
                    metaData,
                    data.getCrn(),
                    data.getEntityId());
        } else {

            return documentStore.updateExistingPdf(
                    document,
                    filename,
                    data.getOnBehalfOfUser(),
                    metaData,
                    data.getDocumentId());
        }
    }
}
