package bdd.paroleparom1report;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.CheckYourReportPage;

import javax.inject.Inject;

public class CheckYourReportSteps {
    @Inject
    private CheckYourReportPage page;

    @Given("^the Delius user is on \"Check your report\" UI$")
    public void theDeliusUserIsOnUI() {
        page.navigateHere();
    }

    @And("^they want to complete the fields within the \"Prisoner Contact\"$")
    public void theyWantToCompleteTheFieldsWithinThe() {
        // noop
    }

    @Given("^Delius User is ready to sign their Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickButton("Sign");
    }
}