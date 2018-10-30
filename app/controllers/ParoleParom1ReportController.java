package controllers;

import com.typesafe.config.Config;
import controllers.base.EncryptedFormFactory;
import controllers.base.ReportGeneratorWizardController;
import data.ParoleParom1ReportData;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import lombok.val;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.data.Form;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class ParoleParom1ReportController extends ReportGeneratorWizardController<ParoleParom1ReportData> {

    private final views.html.paroleParom1Report.cancelled cancelledTemplate;
    private final views.html.paroleParom1Report.completed completedTemplate;
    private final views.html.paroleParom1Report.tester analyticsTesterTemplate;

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
                                        OffenderApi offenderApi) {

        super(ec, webJarsUtil, configuration, environment, formFactory, ParoleParom1ReportData.class, pdfGenerator, documentStore, offenderApi);
        this.cancelledTemplate = cancelledTemplate;
        this.completedTemplate = completedTemplate;
        this.analyticsTesterTemplate = analyticsTesterTemplate;
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
    protected String templateName() {

        return "paroleParom1Report";
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
