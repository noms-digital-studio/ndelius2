package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RoshAnalysisPage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RoshAnalysisPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RoshAnalysisPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.ROSH_ANALYSIS);
        return this;
    }
}
