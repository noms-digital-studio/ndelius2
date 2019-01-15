package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import com.google.common.collect.ImmutableList;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OffenderAlertsSteps {
    @Inject
    private OffenderSummaryPage page;
    @Inject
    private OffenderApiMock offenderApiMock;

    @Given("^that the offender has not breached any conditions$")
    public void thatTheOffenderHasNotBreachedAnyConditions() {
        offenderApiMock.stubOffenderWithConvictions(ImmutableList.of());
    }

    @Then("^they should not see a breached conditions alert$")
    public void theyShouldNotSeeBreachedConditionsAlert() {
        assertThat(page.isElementRendered(".qa-alert-breach")).isFalse();
    }

    @Given("^that the offender has breached conviction conditions$")
    public void thatTheOffenderHasBreachedConvictionConditions() {
        offenderApiMock.stubOffenderWithConvictionsWithBreach(ImmutableList.of());
    }

    @Then("^they should see a breached conditions alert$")
    public void theyShouldSeeBreachedConditionsAlert() {
        assertThat(page.isElementRendered(".qa-alert-breach")).isTrue();
    }
}
