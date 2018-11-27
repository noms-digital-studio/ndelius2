package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class OffenderDetailsPage extends ShortFormatPreSentencePopupReportPage {
    private final LandingPage landingPage;

    @Inject
    public OffenderDetailsPage(TestBrowser control, LandingPage landingPage) {
        super(control);
        this.landingPage = landingPage;
    }

    public OffenderDetailsPage navigateHere() {
        landingPage.navigateHere().next();
        return this;
    }

    public OffenderDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }
}
