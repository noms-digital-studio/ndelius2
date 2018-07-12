package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.paroleparom1report.LandingPage;
import views.pages.paroleparom1report.PrisonerDetailsPage;

import javax.inject.Inject;

public class LandingPageSteps {
    @Inject
    private LandingPage landingPage;

    @Inject
    private PrisonerDetailsPage prisonerDetailsPage;

    @Given("^that the user is on the Parole Report landing page$")
    public void that_the_user_is_on_the_Parole_Report_landing_page() {
        landingPage.navigateHere();
    }

    @When("^they select \"Start now\" button$")
    public void they_select_button() {
        landingPage.gotoNext();
    }

    @Then("^the user should be directed to \"([^\"]*)\" UI$")
    public void the_user_should_be_directed_to_UI(String header) {
        prisonerDetailsPage.isAt(header);
    }
}