package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
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

    @Given("^that the a registration which is serious is saved for an offender in Delius$")
    public void thatTheARegistrationWhichIsSeriousIsSavedForAnOffenderInDelius() {
        offenderApiMock.stubOffenderWithRegistrations(ImmutableList.of(OffenderApiMock.Registration
                .builder()
                .register("Public Protection")
                .type("Danger to staff")
                .riskColour("Red")
                .startDate(LocalDate.now())
                .warnUser(true)
                .build()));
    }

    @When("^offender registrations are displayed$")
    public void offenderRegistrationsAreDisplayed() {
        // this waits until registration sections is available, hence registrations have been retrieved
        page.getRegistrationTableText();
    }

    @Given("^that the a registration which is not serious is saved for an offender in Delius$")
    public void thatTheARegistrationWhichIsNotSeriousIsSavedForAnOffenderInDelius() {
        offenderApiMock.stubOffenderWithRegistrations(ImmutableList.of(OffenderApiMock.Registration
                .builder()
                .register("Public Protection")
                .type("Danger to children")
                .riskColour("Red")
                .startDate(LocalDate.now())
                .warnUser(false)
                .build()));
    }

    @Then("^the serious registration message \"([^\"]*)\" is displayed$")
    public void theSeriousRegistrationMessageIsDisplayed(String message) {
        assertThat(page.getSeriousRegistrationsText()).contains(message);
    }

    @Then("^the serious registration message is not displayed$")
    public void theSeriousRegistrationMessageIsNotDisplayed() {
        assertThat(page.hasSeriousRegistrationMessage()).isFalse();
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
