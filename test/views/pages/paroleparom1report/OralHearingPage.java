package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class OralHearingPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public OralHearingPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public OralHearingPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.ORAL_HEARING);
        return this;
    }
}
