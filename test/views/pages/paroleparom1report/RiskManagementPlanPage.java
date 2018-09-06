package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RiskManagementPlanPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RiskManagementPlanPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RiskManagementPlanPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.RISK_MANAGEMENT_PLAN);
        return this;
    }
}
