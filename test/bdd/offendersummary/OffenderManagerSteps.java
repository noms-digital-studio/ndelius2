package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.val;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OffenderManagerSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;


    @Given("^that the offender has the following offender manager in Delius$")
    public void thatTheOffenderHasTheFollowingNoteSavedWithinDelius(DataTable data) {
        val offenderManager = data.transpose().asMap(String.class, String.class);
        offenderApiMock.stubOffenderWithOffenderManager(OffenderApiMock.OffenderManager
                .builder()
                .probationAreaDescription(offenderManager.get("Current Provider"))
                .teamDescription(offenderManager.get("Current Team"))
                .boroughDescription(offenderManager.get("Cluster"))
                .districtDescription(offenderManager.get("LDU"))
                .teamTelephone(offenderManager.get("Team Telephone number"))
                .allocationReasonDescription(offenderManager.get("Reason for allocation"))
                .staffForenames(offenderManager.get("Current Offender Manager").split(", ")[1])
                .staffSurname(offenderManager.get("Current Offender Manager").split(", ")[0])
                .fromDate(LocalDate.parse(offenderManager.get("Date allocated"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build());
    }

    @Then("^they should see the following offender manager details$")
    public void theyShouldSeeTheFollowingNote(DataTable data) {
        val appointment = data.transpose().asMap(String.class, String.class);
        appointment.forEach((field, value) -> assertThat( page.getFieldValueInSection(".qa-offender-manager", field)).describedAs(field).isEqualTo(value));
    }
}
