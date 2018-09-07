package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SupervisionPlanPage;

import javax.inject.Inject;

public class SupervisionPlanSteps {
    @Inject
    private SupervisionPlanPage page;

    @Given("^Delius User is on the \"Supervision plan for release\" UI$")
    public void deliusUserIsOnTheUI() {
        page.navigateHere();
    }
}
