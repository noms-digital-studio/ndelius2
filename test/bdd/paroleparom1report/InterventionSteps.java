package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.InterventionPage;

import javax.inject.Inject;

public class InterventionSteps {
    @Inject
    private InterventionPage page;

    @Given("^that the Delius user is on the \"Interventions\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^they want to enter the intervention details for a prisoner$")
    public void theyWantToEnterTheInterventionDetailsForAPrisoner() {
        // no page action required
    }

    @Given("^Delius User completes the \"Interventions\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillTextArea("Detail the interventions the prisoner has completed", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("Interventions analysis", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}