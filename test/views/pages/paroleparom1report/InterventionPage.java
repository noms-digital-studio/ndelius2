package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class InterventionPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public InterventionPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public InterventionPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.INTERVENTION);
        return this;
    }
}
