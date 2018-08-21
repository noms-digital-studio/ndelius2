package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class PrisonerContactPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public PrisonerContactPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public PrisonerContactPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.PRISONER_CONTACT);
        return this;
    }
}
