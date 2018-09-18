package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RoshCustodyPage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RoshCustodyPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RoshCustodyPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.CURRENT_ROSH_CUSTODY);
        return this;
    }
}
