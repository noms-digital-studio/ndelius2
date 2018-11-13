package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class CheckYourReportPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public CheckYourReportPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public CheckYourReportPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.CHECK_YOUR_REPORT);
        return this;
    }
}
