package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RecommendationPage;
import views.pages.paroleparom1report.SourcesPage;

import javax.inject.Inject;

public class RecommendationSteps {
    @Inject
    private RecommendationPage page;

    @Given("^Delius User is on the \"Recommendation\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() {
        page.navigateHere();
    }
}
