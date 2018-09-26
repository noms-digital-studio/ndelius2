package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.PrisonerContactPage;

import javax.inject.Inject;

public class PrisonerContactSteps {
    @Inject
    private PrisonerContactPage page;

    @Given("^that the Delius user is on the \"Prisoner contact\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^that the Delius user wants to enter all the contact details that an offender manager has had with a prisoner, their family and prison staff$")
    public void theyWantToEnterTheInterventionDetailsForAPrisoner() {
        // no page action required
    }

    @Given("^that the Delius user has not completed all the relevant fields for \"Prisoner Contact\" UI$")
    public void thatTheDeliusUserHasNotCompletedAllTheRelevantFieldsForUI() {
        page.navigateHere();
        page.fillTextArea("How long have you managed the prisoner, and what contact have you had with them?", "Mauris cursus mattis molestie a iaculis at.");
        page.clickButton("Continue");
    }

    @Given("^Delius User completes the \"Prisoner contact\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillTextArea("How long have you managed the prisoner, and what contact have you had with them?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("What contact have you had with the prisoner`s family, partners or significant others?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("What contact have you had with other relevant agencies about the prisoner?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}