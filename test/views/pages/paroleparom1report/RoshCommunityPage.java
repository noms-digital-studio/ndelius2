package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RoshCommunityPage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RoshCommunityPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RoshCommunityPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.CURRENT_ROSH_COMMUNITY);
        return this;
    }
}
