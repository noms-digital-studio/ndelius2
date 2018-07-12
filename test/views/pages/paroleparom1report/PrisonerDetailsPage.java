package views.pages.paroleparom1report;

import org.openqa.selenium.support.FindBy;
import play.test.TestBrowser;

import javax.inject.Inject;

@FindBy(tagName = "h1")
public class PrisonerDetailsPage extends ParoleParom1PopupReportPage {
    @Inject
    public PrisonerDetailsPage(TestBrowser control) {
        super(control);
    }


}
