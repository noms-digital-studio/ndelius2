package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RiskToPrisonerPage;

import javax.inject.Inject;

public class RiskToPrisonerSteps {
    @Inject
    private RiskToPrisonerPage page;

    @Given("^Delius user is on the \"Risk to the prisoner\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
