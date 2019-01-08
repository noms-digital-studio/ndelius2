package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NotesSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;


    @Given("^that the offender has the following notes saved within Delius$")
    public void thatTheOffenderHasTheFollowingNoteSavedWithinDelius(DataTable data) {
        offenderApiMock.stubOffenderWithNotes(data.asList(String.class));
    }

    @Then("^they should see the following note$")
    public void theyShouldSeeTheFollowingNote(DataTable data) {
        assertThat(page.getNotes()).isEqualTo(String.join("\n", data.asList(String.class)));
    }

    @Given("^that the offender has no note saved within Delius$")
    public void thatTheOffenderHasNoNoteSavedWithinDelius() {
        offenderApiMock.stubOffenderWithNotes(ImmutableList.of());

    }

    @Then("^they should see an empty field for the note$")
    public void theyShouldSeeAnEmptyFieldForTheNote() {
        assertThat(page.getNotes()).isEqualTo("");
    }
}
