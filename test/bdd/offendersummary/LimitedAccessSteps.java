package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LimitedAccessSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;


    @When("^they attempt to navigate to the offender summary page$")
    public void theyAttemptNavigateToTheOffenderSummaryPage() {
        page.navigateHereExpectLimitedAccess();
    }


    @Given("^that the user is on the exclusion list for the offender with an exclusion message of \"([^\"]*)\"$")
    public void thatTheUserIsOnTheExclusionListForTheOffenderWithAnExclusionMessageOf(String exclusionMessage) {
        offenderApiMock.stubOffenderWithLimitedAccessExclusion(exclusionMessage);
    }

    @Given("^that the user is not the restricted list for the offender with an restricted message of \"([^\"]*)\"$")
    public void thatTheUserIsNotTheRestrictedListForTheOffenderWithAnRestrictedMessageOf(String restrictedMessage) {
        offenderApiMock.stubOffenderWithLimitedAccessRestricted(restrictedMessage);
    }

    @Then("^they should not see an limited access message of \"([^\"]*)\"$")
    public void theyShouldNotSeeAnLimitedAccessMessageOf(String message) {
        assertThat(page.getErrorListText()).isEqualTo(message);
    }
}