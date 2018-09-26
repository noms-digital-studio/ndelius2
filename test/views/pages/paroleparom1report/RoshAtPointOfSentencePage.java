package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RoshAtPointOfSentencePage extends ParoleParom1PopupReportPage  {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RoshAtPointOfSentencePage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RoshAtPointOfSentencePage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.ROSH_AT_POINT_OF_SENTENCE);
        return this;
    }
}
