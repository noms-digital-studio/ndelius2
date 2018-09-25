package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.MappaPage;

import javax.inject.Inject;

public class MappaSteps {
    @Inject
    private MappaPage page;

    @Given("^Delius User is on the \"MAPPA\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() {
        page.navigateHere();
    }
}
