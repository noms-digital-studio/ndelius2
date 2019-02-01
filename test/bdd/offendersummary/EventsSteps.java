package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.Data;
import lombok.val;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventsSteps {
    @Data
    class EventEntry {

        private String mainOffence;
        private String outcome;
        private String sentence;
        private String appDate;
        private String status;
        private boolean inBreach;
    }
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;

    @Given("^that the offender has no events saved within Delius$")
    public void thatTheOffenderHasNoEventsSavedWithinDelius() {
        offenderApiMock.stubOffenderWithConvictions(ImmutableList.of());
    }

    @Then("^they should see the following event text \"([^\"]*)\"$")
    public void theyShouldSeeTheFollowingEventText(String text) {
        assertThat(page.getEventTableText()).isEqualToIgnoringCase(text);
    }


    @Given("^that the offender has the following event information saved in Delius$")
    public void thatTheOffenderHasTheFollowingEventInformationSavedInDelius(DataTable data) {
        offenderApiMock.stubOffenderWithConvictions(toConvictions(data));

    }

    @Then("^they should see the following event information$")
    public void theyShouldSeeTheFollowingEventInformation(DataTable data) {
        assertThat(page.countEventTableWithRows()).isEqualTo(data.raw().size()-1);

        toEventRowEntry(data).forEach(row -> assertThat(page.hasEventTableWithRow(row)).describedAs(row.toString()).isTrue());

    }

    private List<OffenderSummaryPage.EventTableRow> toEventRowEntry(DataTable data) {
        AtomicInteger rowNumber = new AtomicInteger();
        return data.asList(OffenderSummaryPage.EventTableRow.class)
                .stream()
                .map(tableRow -> tableRow.toBuilder().rowNumber(rowNumber.getAndIncrement()).build())
                .collect(Collectors.toList());
    }



    private List<OffenderApiMock.Conviction> toConvictions(DataTable data) {
        return data.asList(EventEntry.class)
                .stream()
                .map(entry -> OffenderApiMock.Conviction
                        .builder()
                        .mainOffenceDescription(entry.getMainOffence())
                        .latestCourtAppearanceDescription(entry.getOutcome())
                        .referralDate(LocalDate.parse(entry.getAppDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .sentence(toSentence(entry.sentence))
                        .active(entry.getStatus().equalsIgnoreCase("Active"))
                        .inBreach(entry.isInBreach())
                        .build())
                .collect(Collectors.toList());
    }

    private OffenderApiMock.Sentence toSentence(String sentence) {
        val matcher = Pattern.compile("^(.*)\\(([0-9]+) (.*)\\)").matcher(sentence);

        if (matcher.find()) {
            return OffenderApiMock.Sentence
                    .builder()
                    .description(matcher.group(1))
                    .length(Integer.parseInt(matcher.group(2)))
                    .lengthUnit(matcher.group(3))
                    .build();
        }
        return null;
    }

}
