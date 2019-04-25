package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class OffenceDetailsPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public OffenceDetailsPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public OffenceDetailsPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.OFFENCE_DETAILS);
        return this;
    }

    public OffenceDetailsPage gotoNext() {
        fillTextAreaById("mainOffence", "Main offence");
        fillTextAreaById("offenceSummary", "Offence summary");
        $(id("nextButton")).click();
        return this;
    }
}
