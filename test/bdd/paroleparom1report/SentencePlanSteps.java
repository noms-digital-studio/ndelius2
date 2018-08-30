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
}
