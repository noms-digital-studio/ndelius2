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
        $(id("likelihoodOfReOffending")).fill().with("Likelihood Of ReOffending");
        $(id("riskOfSeriousHarm")).fill().with("Risk of Serious Harm");
        $(id("previousSupervisionResponse_Good")).click();
        $(id("additionalPreviousSupervision")).fill().with("Additional previous supervision");
        $(id("nextButton")).click();
        return this;
    }
}
