package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class SentencingCourtDetailsPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public SentencingCourtDetailsPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public SentencingCourtDetailsPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.SENTENCING_COURT_DETAILS);
        return this;
    }

    public SentencingCourtDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

}
