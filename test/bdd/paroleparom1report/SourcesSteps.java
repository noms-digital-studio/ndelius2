package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SourcesPage;

import javax.inject.Inject;

public class SourcesSteps {
    @Inject
    private SourcesPage page;

    @Given("^Delius user is on the \"Sources\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() {
        page.navigateHere();
    }
}
