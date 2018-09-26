package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.OPDPathwayPage;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class OPDPathwaySteps {
    @Inject
    private OPDPathwayPage page;

    @Given("^Delius user is on the \"OPD Pathway\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Then("^the screen should expand to show additional OPD Pathway content to the user$")
    public void theScreenShouldExpandToShowAdditionalOPDPathwayContentToTheUser()  {
        assertThat(page.isOPDPathwayAdviceContentPresent()).isTrue();
    }

    @Then("^the screen should hide additional OPD Pathway content to the user$")
    public void theScreenShouldHideAdditionalOPDPathwayContentToTheUser() {
        assertThat(page.isNotOPDPathwayAdviceContentPresent()).isTrue();
    }

    @Given("^Delius User completes the \"OPD Pathway\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?");
        page.clickButton("Continue");
    }
}