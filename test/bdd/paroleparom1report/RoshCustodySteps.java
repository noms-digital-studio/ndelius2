package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshCustodyPage;

import javax.inject.Inject;

public class RoshCustodySteps {
    @Inject
    private RoshCustodyPage page;

    @Given("^Delius user is on the \"Current RoSH custody\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }
}
