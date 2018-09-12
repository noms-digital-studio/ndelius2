package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RiskToPrisonerPage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RiskToPrisonerPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RiskToPrisonerPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.RISK_TO_PRISONER);
        return this;
    }
}
