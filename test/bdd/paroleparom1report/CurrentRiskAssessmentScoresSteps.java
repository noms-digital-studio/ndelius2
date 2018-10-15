package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.CurrentRiskAssessmentScoresPage;

import javax.inject.Inject;

public class CurrentRiskAssessmentScoresSteps {
    @Inject
    private CurrentRiskAssessmentScoresPage page;

    @Given("^Delius user is on the \"Current risk assessment scores\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable { page.navigateHere(); }

    @Given("^Delius User completes the \"Current risk assessment scores\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillInputWithId("riskAssessmentRSRScore", "2.54");
        page.fillInputWithId("riskAssessmentOGRS3ReoffendingProbability", "23");
        page.fillInputWithId("riskAssessmentOGPReoffendingProbability", "12");
        page.fillInputWithId("riskAssessmentOVPReoffendingProbability", "31");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has a Risk Matrix 2000 assessment been completed?");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Risk Matrix 2000");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has a Spousal Assault Risk Assessment (SARA) been completed?");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Spousal Assault Risk Assessment (SARA)");
        page.clickButton("Continue");
    }
}
