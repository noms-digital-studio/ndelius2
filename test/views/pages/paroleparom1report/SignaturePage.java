package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class SignaturePage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public SignaturePage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public SignaturePage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.SIGNATURE);
        return this;
    }
}
