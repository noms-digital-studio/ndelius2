package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class SourcesPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public SourcesPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public SourcesPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.SOURCES);
        return this;
    }
}
