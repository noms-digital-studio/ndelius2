package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.val;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NextAppointmentSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;


    @Given("^that Kieron Robinson has the following details saved for their next appointment$")
    public void thatKieronRobinsonHasTheFollowingDetailsSavedForTheirNextAppointment(DataTable data) {
        val appointment = data.asMap(String.class, String.class);
        offenderApiMock.stubOffenderWithNextAppointment(OffenderApiMock.Appointment
                .builder()
                .appointmentTypeDescription(appointment.get("Contact type"))
                .appointmentDate(LocalDate.parse(appointment.get("Date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .appointmentStartTime(LocalTime.parse(appointment.get("Start time"), DateTimeFormatter.ofPattern("HH:mm")))
                .officeLocationDescription(appointment.get("Location"))
                .probationAreaDescription(appointment.get("Provider"))
                .teamDescription(appointment.get("Team"))
                .staffForenames(appointment.get("Officer").split(", ")[1])
                .staffSurname(appointment.get("Officer").split(", ")[0])
                .build());
    }

    @Given("^that the offender does not have a next appointment within Delius$")
    public void thatTheOffenderDoesNotHaveANextAppointmentWithinDelius() {
        offenderApiMock.stubOffenderWithNextAppointment(null);
    }

    @Then("^the screen should expand to show the following next appointment$")
    public void theScreenShouldExpandToShowTheFollowingNextAppointment(DataTable data) {
        val appointment = data.asMap(String.class, String.class);
        appointment.forEach((field, value) -> assertThat( page.getFieldValueInSection(".qa-next-appointment", field)).describedAs(field).isEqualTo(value));
    }
}
