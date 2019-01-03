package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Data;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class RegistrationSteps {
    @Data
    class RegistrationEntry {
        private String flag;
        private String type;
        private String date;
        private String colour;
    }

    @Inject
    private OffenderSummaryPage page;

    @Inject
    private OffenderApiMock offenderApiMock;

    @Given("^that the Kieron Robinson is not on any registers and warning lists within Delius$")
    public void thatTheKieronRobinsonIsNotOnAnyRegistersAndWarningListsWithinDelius() {
        offenderApiMock.stubOffenderWithRegistrations(ImmutableList.of());
    }

    @Given("^that the following alert and registration information is saved for an offender in Delius$")
    public void thatTheFollowingAlertAndRegistrationInformationIsSavedForAnOffenderInDelius(DataTable data) {
        offenderApiMock.stubOffenderWithRegistrations(toRegistrations(data));
    }

    @When("^the Delius user selects the \"([^\"]*)\" link on the \"Offender Summary\" UI$")
    public void theDeliusUserSelectsTheLinkOnTheUI(String accordionLink) {
        page.clickAccordion(accordionLink);
    }


    @Then("^then they should see the following alert and registrations information$")
    public void thenTheyShouldSeeTheFollowingAlertAndRegistrationsInformation(DataTable data) {
        toRegistrationRowEntry(data).forEach(row -> assertThat(page.hasRegistrationTableWithRow(row)).describedAs(row.toString()).isTrue());
    }

    private List<OffenderSummaryPage.RegistrationTableRow> toRegistrationRowEntry(DataTable data) {
        AtomicInteger rowNumber = new AtomicInteger();
        return data.asList(OffenderSummaryPage.RegistrationTableRow.class)
                .stream()
                .map(tableRow -> tableRow.toBuilder().rowNumber(rowNumber.getAndIncrement()).build())
                .collect(Collectors.toList());
    }

    @Then("^they should see the following alert and registration text \"([^\"]*)\"$")
    public void theyShouldSeeTheFollowingAlertAndRegistrationText(String text) {
        assertThat(page.getRegistrationTableText()).isEqualToIgnoringCase(text);
    }


    private List<OffenderApiMock.Registration> toRegistrations(DataTable data) {
        return data.asList(RegistrationEntry.class)
                .stream()
                .map(entry -> OffenderApiMock.Registration
                        .builder()
                        .register(entry.getFlag())
                        .type(entry.getType())
                        .riskColour(entry.getColour())
                        .startDate(LocalDate.parse(entry.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .build())
                .collect(Collectors.toList());
    }
}
