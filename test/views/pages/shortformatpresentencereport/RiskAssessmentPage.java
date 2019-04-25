package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class RiskAssessmentPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public RiskAssessmentPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public RiskAssessmentPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.RISK_ASSESSMENT);
        return this;
    }

    public RiskAssessmentPage gotoNext() {
        fillTextAreaById("likelihoodOfReOffending", "Likelihood Of ReOffending");
        fillTextAreaById("riskOfSeriousHarm", "Likelihood Of ReOffending");
        $(id("previousSupervisionResponse_Good")).click();
        fillTextAreaById("additionalPreviousSupervision", "Additional previous supervision");
        $(id("nextButton")).click();
        return this;
    }
}
