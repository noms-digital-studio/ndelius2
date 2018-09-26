package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RiskToPrisonerPage;

import javax.inject.Inject;

public class RiskToPrisonerSteps {
    @Inject
    private RiskToPrisonerPage page;

    @Given("^Delius user is on the \"Risk to the prisoner\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Risk to the prisoner\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickElementWithId("selfHarmCommunity_yes");
        page.clickElementWithId("selfHarmCustody_yes");
        page.clickElementWithId("othersHarmCommunity_yes");
        page.clickElementWithId("othersHarmCustody_yes");
        page.clickButton("Continue");
    }
}
