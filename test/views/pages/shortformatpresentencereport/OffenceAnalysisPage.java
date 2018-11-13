package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

public class OffenceAnalysisPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public OffenceAnalysisPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public OffenceAnalysisPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.OFFENCE_ANALYSIS);
        return this;
    }
}
