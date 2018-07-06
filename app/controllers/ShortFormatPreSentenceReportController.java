package controllers;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.ReportGeneratorWizardController;
import data.ShortFormatPreSentenceReportData;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.data.Form;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import static helpers.DateTimeHelper.calculateAge;
import static helpers.DateTimeHelper.format;
import static helpers.JwtHelper.principal;
import static java.time.Clock.systemUTC;
import static java.util.Optional.ofNullable;

public class ShortFormatPreSentenceReportController extends ReportGeneratorWizardController<ShortFormatPreSentenceReportData> {

    private final views.html.shortFormatPreSentenceReport.cancelled cancelledTemplate;
    private final views.html.shortFormatPreSentenceReport.completed completedTemplate;
    protected final OffenderApi offenderApi;


    @Inject
    public ShortFormatPreSentenceReportController(HttpExecutionContext ec,
                                                  WebJarsUtil webJarsUtil,
                                                  Config configuration,
                                                  Environment environment,
                                                  AnalyticsStore analyticsStore,
                                                  EncryptedFormFactory formFactory,
                                                  PdfGenerator pdfGenerator,
                                                  DocumentStore documentStore,
                                                  views.html.shortFormatPreSentenceReport.cancelled cancelledTemplate,
                                                  views.html.shortFormatPreSentenceReport.completed completedTemplate,
                                                  OffenderApi offenderApi) {

        super(ec, webJarsUtil, configuration, environment, analyticsStore, formFactory, ShortFormatPreSentenceReportData.class, pdfGenerator, documentStore);
        this.cancelledTemplate = cancelledTemplate;
        this.completedTemplate = completedTemplate;
        this.offenderApi = offenderApi;
    }

    @Override
    protected String templateName() {

        return "shortFormatPreSentenceReport";
    }

    @Override
    protected CompletionStage<Map<String, String>> addPageAndDocumentId(Map<String, String> origParams) {

        return super.addPageAndDocumentId(origParams).thenCompose(params -> {

            val username = decrypter.apply(params.get("user"));
            val crn = params.get("crn");

            return offenderApi.logon(username)
                .thenApplyAsync(bearerToken -> {
                    Logger.info("AUDIT:{}: ShortFormatPreSentenceReportController: Successful logon for user {}", principal(bearerToken), username);
                    return bearerToken;

                }, ec.current())
                .thenCompose(bearerToken -> offenderApi.getOffenderByCrn(bearerToken, crn))
                .thenApply(offender -> {

                    params.put("name", offender.displayName());

                    ofNullable(offender.getDateOfBirth()).ifPresent(dob -> {
                        params.put("dateOfBirth", format(dob));
                        params.put("age", String.format("%d", calculateAge(dob, systemUTC())));
                    });

                    ofNullable(offender.getOtherIds())
                        .filter(otherIds -> otherIds.containsKey("pncNumber"))
                        .map(otherIds -> otherIds.get("pncNumber"))
                        .ifPresent(pnc -> params.put("pnc", pnc));

                    ofNullable(offender.getContactDetails())
                        .flatMap(OffenderApi.ContactDetails::currentAddress)
                        .map(OffenderApi.OffenderAddress::render)
                        .ifPresent(address -> params.put("address", address));

                    Logger.info("Creating report. Params: " + params);

                    return params;
                });

        });
    }

    @Override
    protected CompletionStage<Map<String, String>> initialParams() {
        return super.initialParams().thenApply(params -> {
            params.putIfAbsent("pncSupplied", Boolean.valueOf(!Strings.isNullOrEmpty(params.get("pnc"))).toString());
            params.putIfAbsent("addressSupplied", Boolean.valueOf(!Strings.isNullOrEmpty(params.get("address"))).toString());
            return migrateLegacyReport(params);
        });
    }


    private Map<String, String> migrateLegacyReport(Map<String, String> params) {
        return migrateLegacyOffenderAssessmentIssues(params);
    }

    private Map<String, String> migrateLegacyOffenderAssessmentIssues(Map<String, String> params) {
        if(Boolean.parseBoolean(params.get("issueDrugs"))) {
            params.putIfAbsent("issueSubstanceMisuse", "true");
        }
        if(Boolean.parseBoolean(params.get("issueAlcohol"))) {
            params.putIfAbsent("issueSubstanceMisuse", "true");
        }
        if(StringUtils.isNotBlank(params.get("offenderAssessment"))) {
            params.putIfAbsent("issueOther", "true");
            params.putIfAbsent("issueOtherDetails", params.get("offenderAssessment"));
        }
        return params;
    }

    @Override
    protected Map<String, String> modifyParams(Map<String, String> params, Consumer<String> paramEncrypter) {

        if ("2".equals(params.get("pageNumber"))) {

            if ("false".equals(params.get("pncSupplied"))) {

                paramEncrypter.accept("pnc");
            }

            if ("false".equals(params.get("addressSupplied"))) {

                paramEncrypter.accept("address");
            }
        }

        if ("3".equals(params.get("pageNumber"))) {

            paramEncrypter.accept("court");
            paramEncrypter.accept("dateOfHearing");
            paramEncrypter.accept("localJusticeArea");
        }

        return params;
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

    private Integer reviewPageNumberFor(Form<ShortFormatPreSentenceReportData> boundForm) {
        return boundForm.value().map(form -> form.totalPages() - 1).orElse(1);
    }

    @Override
    protected Content renderErrorMessage(String errorMessage) {

        return views.html.shortFormatPreSentenceReport.error.render("Report error", errorMessage, webJarsUtil);
    }
}
