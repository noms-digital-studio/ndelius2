package views.pages.shortformatpresentencereport;

import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

public class DraftSavedConfirmationPage extends ShortFormatPreSentencePopupReportPage {
    private final LandingPage landingPage;

    @Inject
    public DraftSavedConfirmationPage(TestBrowser control, LandingPage landingPage) {
        super(control);
        this.landingPage = landingPage;
    }

    public DraftSavedConfirmationPage navigateHere() {
        landingPage.navigateHere().next();
        return saveAsDraft();
    }

    public DraftSavedConfirmationPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    private DraftSavedConfirmationPage saveAsDraft() {
        $(id("exitLink")).click();
        return this;
    }

    public void isAt() {
        assertThat(window().title()).contains("Draft report saved - Short Format Pre Sentence Report");
    }


    public void updateReport() {
        $(By.cssSelector("#edit-pdf a")).click();
    }
}
