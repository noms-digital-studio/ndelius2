package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class CurrentRiskAssessmentScoresPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public CurrentRiskAssessmentScoresPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public CurrentRiskAssessmentScoresPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.CURRENT_RISK_ASSESSMENT_SCORES);
        return this;
    }
}
