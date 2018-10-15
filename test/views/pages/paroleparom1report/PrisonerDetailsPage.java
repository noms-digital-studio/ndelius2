package views.pages.paroleparom1report;

import org.openqa.selenium.support.FindBy;
import play.test.TestBrowser;

import javax.inject.Inject;

@FindBy(tagName = "h1")
public class PrisonerDetailsPage extends ParoleParom1PopupReportPage {
    private final LandingPage landingPage;
    @Inject
    public PrisonerDetailsPage(TestBrowser control, LandingPage landingPage) {
        super(control);
        this.landingPage = landingPage;
    }

    public void navigateHere() {
        landingPage.navigateHere().next();
    }

    public void navigateHereFemale() {
        landingPage.navigateHereForFemale().next();
    }
}
