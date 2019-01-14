package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PersonalCircumstancesSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;

    @Data
    class PersonalCircumstanceEntry {
        private String circumstanceType;
        private String circumstanceSubtype;
        private String startDate;
        private String endDate;
    }


    @Given("^that the offender has no personal circumstance saved in Delius$")
    public void thatTheOffenderHasNoPersonalCircumstanceSavedInDelius() {
        offenderApiMock.stubOffenderWithPersonalCircumstances(ImmutableList.of());

    }

    @Then("^the screen should expand to show the following text \"([^\"]*)\"$")
    public void theScreenShouldExpandToShowTheFollowingText(String message) {
        assertThat(page.getPersonalCircumstancesTableText()).isEqualToIgnoringCase(message);
    }

    @Given("^that the following personal circumstance information is saved for an offender within Delius$")
    public void thatTheFollowingPersonalCircumstanceInformationIsSavedForAnOffenderWithinDelius(DataTable data) {
        offenderApiMock.stubOffenderWithPersonalCircumstances(toPersonalCircumstance(data));
    }

    @Then("^the following personal circumstance information must be displayed$")
    public void theFollowingPersonalCircumstanceInformationMustBeDisplayed(DataTable data) {
        assertThat(page.countPersonalCircumstancesTableWithRows()).isEqualTo(data.raw().size()-1);

        toPersonalCircumstanceRowEntry(data).forEach(row -> assertThat(page.hasPersonalCircumstanceTableWithRow(row)).describedAs(row.toString()).isTrue());
    }

    private List<OffenderApiMock.PersonalCircumstance> toPersonalCircumstance(DataTable data) {
        return data.asList(PersonalCircumstanceEntry.class)
                .stream()
                .map(entry -> OffenderApiMock.PersonalCircumstance
                        .builder()
                        .personalCircumstanceTypeDescription(entry.getCircumstanceType())
                        .personalCircumstanceSubTypeDescription(entry.getCircumstanceSubtype())
                        .startDate(LocalDate.parse(entry.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .endDate(StringUtils.isBlank(entry.getEndDate()) ? null : LocalDate.parse(entry.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .build())
                .collect(Collectors.toList());
    }

    private List<OffenderSummaryPage.PersonalCircumstanceTableRow> toPersonalCircumstanceRowEntry(DataTable data) {
        AtomicInteger rowNumber = new AtomicInteger();
        return data.asList(OffenderSummaryPage.PersonalCircumstanceTableRow.class)
                .stream()
                .map(tableRow -> tableRow.toBuilder().rowNumber(rowNumber.getAndIncrement()).build())
                .collect(Collectors.toList());
    }


}
