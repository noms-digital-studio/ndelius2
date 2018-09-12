package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.OralHearingPage;

import javax.inject.Inject;

public class OralHearingSteps {
    @Inject
    private OralHearingPage page;

    @Given("^Delius user is on the \"Oral hearing\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
