package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class BehaviourInPrisonPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public BehaviourInPrisonPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public BehaviourInPrisonPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.BEHAVIOUR_IN_PRISON);
        return this;
    }

    public void doNotNavigateHere() {
        // navigate somewhere other than here
        prisonerDetailsPage.navigateHere();
    }
}
