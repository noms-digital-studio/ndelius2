package views.pages.paroleparom1report;

import lombok.val;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;

public class CheckYourReportPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public CheckYourReportPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public CheckYourReportPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.CHECK_YOUR_REPORT);
        return this;
    }

    public String statusTextForPage(String pageName) {
        val row = $(By.linkText(pageName)).find(By.xpath("../.."));
        val statusCell = row.find(By.cssSelector("td:nth-child(3)"));
        return statusCell.text();
    }
}
