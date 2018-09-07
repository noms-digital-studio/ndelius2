package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class SupervisionPlanPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public SupervisionPlanPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public SupervisionPlanPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.SUPERVISION_PLAN);
        return this;
    }
}
