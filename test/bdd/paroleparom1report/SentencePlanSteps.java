package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SentencePlanPage;

import javax.inject.Inject;

public class SentencePlanSteps {
    @Inject
    private SentencePlanPage page;

    @Given("^that the Delius user is on the \"Current sentence plan and response\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^that the Delius user wants to enter details of the current sentence plan in the offender parole report$")
    public void thatTheDeliusUserWantsToEnterDetailsOfTheCurrentSentencePlanInTheOffenderParoleReport() throws Throwable {
        // no page action required
    }

    @Given("^Delius User completes the \"Prison sentence plan and response\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillTextArea("Detail their prison sentence plan. Include their response", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
