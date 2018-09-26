package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SupervisionPlanPage;

import javax.inject.Inject;

public class SupervisionPlanSteps {
    @Inject
    private SupervisionPlanPage page;

    @Given("^Delius User is on the \"Supervision plan for release\" UI$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Supervision plan for release\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Does the prisoner require a supervision plan for release?");
        page.fillTextArea("Detail the supervision plan for release", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
