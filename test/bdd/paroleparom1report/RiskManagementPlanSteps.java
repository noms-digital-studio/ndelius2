package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RiskManagementPlanPage;

import javax.inject.Inject;

public class RiskManagementPlanSteps {
    @Inject
    private RiskManagementPlanPage page;

    @Given("^Delius user is on the \"Risk Managemant Plan (RMP)\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable { page.navigateHere(); }
}
