package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.VictimsPage;

import javax.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class VictimsSteps {
    @Inject
    private VictimsPage page;

    @Given("^that the Delius user is on the \"Victims\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
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

    @Given("^Delius User completes the \"Victims\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();

        page.fillTextArea("Analyse the impact of the offence on the victims", "Victims impact detail text");
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Day", new SimpleDateFormat("dd").format(date));
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Month", new SimpleDateFormat("MM").format(date));
        page.fillInputInSectionWithLegend("On what date did you contact the VLO?", "Year", new SimpleDateFormat("yyyy").format(date));
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Are the victims engaged in the Victim Contact Scheme (VCS)?");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Do the victims wish to submit a Victim Personal Statement (VPS)?");
        page.clickButton("Continue");
    }
}