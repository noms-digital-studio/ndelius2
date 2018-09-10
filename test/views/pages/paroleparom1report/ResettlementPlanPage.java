package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class ResettlementPlanPage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public ResettlementPlanPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public ResettlementPlanPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.RESETTLEMENT_PLAN);
        return this;
    }
}
