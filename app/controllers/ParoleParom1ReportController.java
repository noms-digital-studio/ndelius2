package controllers;

import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.ReportGeneratorWizardController;
import data.ParoleParom1ReportData;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import interfaces.PrisonerApi;
import lombok.val;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.data.Form;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static controllers.SessionKeys.OFFENDER_API_BEARER_TOKEN;
import static java.util.Optional.ofNullable;

public class ParoleParom1ReportController extends ReportGeneratorWizardController<ParoleParom1ReportData> {

    private final views.html.paroleParom1Report.cancelled cancelledTemplate;
    private final views.html.paroleParom1Report.completed completedTemplate;
    private final views.html.paroleParom1Report.tester analyticsTesterTemplate;
    private final PrisonerApi prisonerApi;


    @Inject
    public ParoleParom1ReportController(HttpExecutionContext ec,
                                        WebJarsUtil webJarsUtil,
                                        Config configuration,
                                        Environment environment,
                                        EncryptedFormFactory formFactory,
                                        PdfGenerator pdfGenerator,
                                        DocumentStore documentStore,
                                        views.html.paroleParom1Report.cancelled cancelledTemplate,
                                        views.html.paroleParom1Report.completed completedTemplate,
                                        views.html.paroleParom1Report.tester analyticsTesterTemplate,
                                        OffenderApi offenderApi,
                                        PrisonerApi prisonerApi) {

        super(ec, webJarsUtil, configuration, environment, formFactory, ParoleParom1ReportData.class, pdfGenerator, documentStore, offenderApi);
        this.cancelledTemplate = cancelledTemplate;
        this.completedTemplate = completedTemplate;
        this.analyticsTesterTemplate = analyticsTesterTemplate;
        this.prisonerApi = prisonerApi;
    }

    @Override
    protected Map<String, String> storeOffenderDetails(Map<String, String> params, OffenderApi.Offender offender) {
        params.put("gender", offender.getGender());

        ofNullable(offender.getOtherIds())
            .filter(otherIds -> otherIds.containsKey("nomsNumber"))
            .map(otherIds -> otherIds.get("nomsNumber"))
            .ifPresent(nomsNumber -> {
                params.put("prisonerDetailsNomisNumber", nomsNumber);
            });

        params.put("prisonerDetailsPrisonersFullName", offender.displayName());
        return params;
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {
        return super.initialParams().thenComposeAsync(params -> {
            val prisonerDetailsNomisNumber = params.get("prisonerDetailsNomisNumber");
            val prisonerFuture = Optional.ofNullable(prisonerDetailsNomisNumber)
                    .map(nomsNumber -> prisonerApi.getOffenderByNomsNumber(nomsNumber).toCompletableFuture())
                    .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty()));

            val crn = params.get("crn");
            val institutionalReportId = params.get("entityId");
            val institutionalReportFuture = offenderApi.getInstitutionalReport(session(OFFENDER_API_BEARER_TOKEN), crn, institutionalReportId).toCompletableFuture();

            return CompletableFuture.allOf(prisonerFuture, institutionalReportFuture)
                    .thenApply(notUsed ->
                            storeCustodyData(
                                    params,
                                    prisonerFuture.join()))
                    .thenApply(notUsed ->
                            storeOffenderData(
                                    params,
                                    institutionalReportFuture.join()))
                ;
        }, ec.current());
    }

    private Map<String, String> storeCustodyData(Map<String, String> params, Optional<PrisonerApi.Offender> maybeOffender) {
        maybeOffender.ifPresent(offender -> {
            params.put("prisonerDetailsPrisonInstitution", offender.getInstitution().getDescription());
        });
        return params;
    }

    private Map<String, String> storeOffenderData(Map<String, String> params, OffenderApi.InstitutionalReport institutionalReport) {
        Logger.info("institutionalReport: " + institutionalReport);
        Logger.info("Params: " + params);
        if (params.containsKey("createJourney")) {
            params.put("prisonerDetailsOffence", institutionalReport.getConviction().getMainOffence().offenceDescription());
        }
        return params;
    }

    @Override
    protected String templateName() {

        return "paroleParom1Report";
    }

    @Override
    protected String documentEntityType() {
        return "INSTITUTIONALREPORT";
    }

    @Override
    protected String documentTableName() {
        return "INSTITUTIONAL_REPORT";
    }


    @Override
    protected Content renderCancelledView() {

        val boundForm = wizardForm.bindFromRequest();

        return cancelledTemplate.render(boundForm, viewEncrypter, reviewPageNumberFor(boundForm));
    }

    @Override
    protected Content renderCompletedView(Byte[] bytes) {

        val boundForm = wizardForm.bindFromRequest();

        return completedTemplate.render(boundForm, viewEncrypter, reviewPageNumberFor(boundForm));
    }

    private Integer reviewPageNumberFor(Form<ParoleParom1ReportData> boundForm) {
        return boundForm.value().map(form -> form.totalPages() - 1).orElse(1);
    }

    public Result analyticsTester() {
        return ok(analyticsTesterTemplate.render());
    }


}
