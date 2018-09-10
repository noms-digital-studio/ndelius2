package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.ResettlementPlanPage;

import javax.inject.Inject;

public class ResettlementPlanSteps {
    @Inject
    private ResettlementPlanPage page;

    @Given("^Delius user is on the \"Resettlement plan for release\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
