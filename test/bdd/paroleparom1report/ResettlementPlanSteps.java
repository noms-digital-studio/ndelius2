package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.ResettlementPlanPage;

import javax.inject.Inject;

public class ResettlementPlanSteps {
    @Inject
    private ResettlementPlanPage page;

    @Given("^Delius user is on the \"Resettlement plan for release\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Resettlement plan for release\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Does the prisoner require a resettlement plan for release?");
        page.fillTextArea("Detail the resettlement plan for release", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
