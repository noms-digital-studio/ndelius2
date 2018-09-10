package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

public class RecommendationPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public RecommendationPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public RecommendationPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.RECOMMENDATION);
        return this;
    }
}
