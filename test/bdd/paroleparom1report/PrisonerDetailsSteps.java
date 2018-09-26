package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.PrisonerDetailsPage;

import javax.inject.Inject;

public class PrisonerDetailsSteps {
    @Inject
    private PrisonerDetailsPage page;

    @Given("^that the Delius user is on the \"Prisoner details\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Prisoner details\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickButton("Continue");
    }
}
