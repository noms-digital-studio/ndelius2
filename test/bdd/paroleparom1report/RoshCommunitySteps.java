package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshCommunityPage;

import javax.inject.Inject;

public class RoshCommunitySteps {
    @Inject
    private RoshCommunityPage page;

    @Given("^Delius user is on the \"Current RoSH community\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Current RoSH community\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Low", "Public");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Known adult");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Children");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Prisoners");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Staff");
        page.clickButton("Continue");
    }
}
