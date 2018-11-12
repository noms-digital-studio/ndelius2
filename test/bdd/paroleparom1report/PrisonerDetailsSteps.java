package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.PrisonerDetailsPage;

import javax.inject.Inject;

import static views.pages.paroleparom1report.Page.PRISONER_DETAILS;

public class PrisonerDetailsSteps {
    @Inject
    private PrisonerDetailsPage page;

    @Given("^that the Delius user is on the \"Prisoner details\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
        page.isAt(PRISONER_DETAILS.getPageHeader());
    }

    @Given("^that the Delius user is on the \"Prisoner details\" page within the Parole Report for a female prisoner$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReportForFemale() throws Throwable {
        page.navigateHereFemale();
        page.isAt(PRISONER_DETAILS.getPageHeader());
    }

    @Given("^Delius User completes the \"Prisoner details\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        thatTheDeliusUserHasCompletedAllTheRelevantFieldsWithinTheUI();
        page.clickButton("Continue");
    }

    @Given("^that the delius user want to enter for Male prisoner who has Indeterminate sentence$")
    public void thatTheDeliusUserWantToEnterForMalePrisonerWhoHasIndeterminateSentence() throws Throwable {
        // no page action required
    }

    @Given("^that the delius user want to enter for Male prisoner who has Determinate sentence$")
    public void thatTheDeliusUserWantToEnterForMalePrisonerWhoHasDeterminateSentence() throws Throwable {
        // no page action required
    }

    @Given("^that the delius user want to enter for Female prisoner who has Indeterminate sentence$")
    public void thatTheDeliusUserWantToEnterForFemalePrisonerWhoHasIndeterminateSentence() throws Throwable {
        // no page action required
    }

    @Given("^that the delius user want to enter for Female prisoner who has Determinate sentence$")
    public void thatTheDeliusUserWantToEnterForFemalePrisonerWhoHasDeterminateSentence() throws Throwable {
        // no page action required
    }

    @Given("^that the Delius user has completed all the relevant fields within the \"Prisoner details\" UI$")
    public void thatTheDeliusUserHasCompletedAllTheRelevantFieldsWithinTheUI() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("A", "Current prison category");
        page.fillTextArea("Offence", "Aggravated assault");
        page.fillTextArea("Sentence", "20 years");
        page.clickRadioButtonWithLabelWithinLegend("Determinate", "Sentence type");
        page.clickButton("Continue");
    }

}
