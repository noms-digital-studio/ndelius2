package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class MappaPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public MappaPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public MappaPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.MAPPA);
        return this;
    }
}
