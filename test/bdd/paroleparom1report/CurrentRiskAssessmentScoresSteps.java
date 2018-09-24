package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.CurrentRiskAssessmentScoresPage;

import javax.inject.Inject;

public class CurrentRiskAssessmentScoresSteps {
    @Inject
    private CurrentRiskAssessmentScoresPage page;

    @Given("^Delius user is on the \"Current risk assessment scores\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable { page.navigateHere(); }
}
