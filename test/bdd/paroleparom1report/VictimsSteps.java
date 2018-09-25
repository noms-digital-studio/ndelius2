package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.VictimsPage;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class VictimsSteps {
    @Inject
    private VictimsPage page;

    @Given("^that the Delius user is on the \"Victims\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() {
        page.navigateHere();
    }

    @Then("^the screen should expand to show additional VPS content to the user$")
    public void theScreenShouldExpandToShowAdditionalVPSContentToTheUser() {
        assertThat(page.isVPSAdviceContentPresent()).isTrue();
    }

    @Then("^the screen should hide additional VPS content to the user$")
    public void theScreenShouldHideAdditionalVPSContentToTheUser() {
        assertThat(page.isVPSAdviceContentPresent()).isFalse();
    }

    @Given("^that the Delius user has completed all the relevant fields for the \"Victims\" UI$")
    public void thatTheDeliusUserHasCompletedAllTheRelevantFieldsForTheUI() {
        page.navigateHere();
        page.fillTextArea("Analyse the impact of the offence on the victims", "Victims impact detail text");
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Day", "19");
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Month", "07");
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Year", "2018");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Are the victims engaged in the Victim Contact Scheme (VCS)?");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Do the victims wish to submit a Victim Personal Statement (VPS)?");
        page.clickButton("Continue");
    }
}