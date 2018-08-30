package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class SentencePlanPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public SentencePlanPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public SentencePlanPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.SENTENCE_PLAN);
        return this;
    }
}
