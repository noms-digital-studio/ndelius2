package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.VictimsPage;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class VictimsSteps {
    @Inject
    private VictimsPage page;

    @Given("^that the Delius user is on the \"Victims\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }

    @Then("^the screen should expand to show additional VPS content to the user$")
    public void theScreenShouldExpandToShowAdditionalVPSContentToTheUser() {
        assertThat(page.isVPSAdviceContentPresent()).isTrue();
    }

    @Then("^the screen should hide additional VPS content to the user$")
    public void theScreenShouldHideAdditionalVPSContentToTheUser() {
        assertThat(page.isVPSAdviceContentPresent()).isFalse();
    }
}