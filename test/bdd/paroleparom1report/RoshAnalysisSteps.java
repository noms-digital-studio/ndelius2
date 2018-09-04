package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshAnalysisPage;

import javax.inject.Inject;

public class RoshAnalysisSteps {
    @Inject
    private RoshAnalysisPage page;

    @Given("^Delius user is on the \"RoSH analysis\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
