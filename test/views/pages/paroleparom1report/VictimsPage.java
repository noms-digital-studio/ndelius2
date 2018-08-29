package views.pages.paroleparom1report;

import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class VictimsPage extends ParoleParom1PopupReportPage {
    private final PrisonerDetailsPage prisonerDetailsPage;
    @Inject
    public VictimsPage(PrisonerDetailsPage prisonerDetailsPage, TestBrowser control) {
        super(control);
        this.prisonerDetailsPage = prisonerDetailsPage;
    }

    public VictimsPage navigateHere() {
        prisonerDetailsPage.navigateHere();
        jumpTo(Page.VICTIMS);
        return this;
    }

    public boolean isVPSAdviceContentPresent() {
        return $(id("vps-advice-yes")).first().displayed();
    }
}
