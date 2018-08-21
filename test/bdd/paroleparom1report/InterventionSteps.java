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

    @Given("^that the Delius user has entered details into \"([^\"]*)\" and \"([^\"]*)\" field$")
    public void thatTheDeliusUserHasEnteredDetailsIntoAndField(String label1, String label2) {
        page.fillTextArea(label1, String.format("Any text for %s", label1));
        page.fillTextArea(label2, String.format("Any text for %s", label2));
    }
}