package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.PrisonerContactPage;

import javax.inject.Inject;

public class PrisonerContactSteps {
    @Inject
    private PrisonerContactPage page;

    @Given("^that the Delius user is on the \"Prisoner contact\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }

    @Given("^that the Delius user wants to enter all the contact details that an offender manager has had with a prisoner, their family and prison staff$")
    public void theyWantToEnterTheInterventionDetailsForAPrisoner() {
        // no page action required
    }

}