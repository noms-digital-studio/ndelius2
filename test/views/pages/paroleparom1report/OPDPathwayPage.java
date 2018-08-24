package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class OPDPathwayPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public OPDPathwayPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public OPDPathwayPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.OPD_PATHWAY);
        return this;
    }

    public boolean isOPDPathwayAdviceContentPresent() {
        return $(id("opd-pathway-details-yes")).first().displayed();
    }
    public boolean isNotOPDPathwayAdviceContentPresent() {
        return !$(id("opd-pathway-details-yes")).first().displayed();
    }
}
