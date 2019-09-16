package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class ProposalPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public ProposalPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public ProposalPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.PROPOSAL);
        return this;
    }

    public ProposalPage gotoNext() {
        $(id("confirmEIF_yes")).click();
        fillInputWithId("proposal", "Proposal");
        $(id("nextButton")).click();
        return this;
    }

}
