package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshAtPointOfSentencePage;

import javax.inject.Inject;

public class RoshAtPointOfSentenceSteps {
    @Inject
    private RoshAtPointOfSentencePage page;

    @Given("^that the Delius user is on the \"RoSH at point of sentence\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"RoSH at point of sentence\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickButton("Continue");
    }

}
