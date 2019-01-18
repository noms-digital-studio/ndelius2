package bdd.offendersummary;

import cucumber.api.java.en.Then;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OffenderCardsSteps {

    @Inject
    private OffenderSummaryPage page;

    @Then("^they should not see provider data$")
    public void theyShouldSeeProviderData() {
        assertThat(page.isElementRendered(".qa-card-provider")).isFalse();
    }

    @Then("^they should not see offender manager data$")
    public void theyShouldNotSeeOffenderManagerData() {
        assertThat(page.isElementRendered(".qa-card-offender-manager")).isFalse();
    }

    @Then("^they should not see active event data$")
    public void theyShouldNotSeeEventData() {
        assertThat(page.isElementRendered(".qa-card-active-event")).isFalse();
    }

    @Then("^they should see the number of events as \"([^\"]*)\"$")
    public void theyShouldSeeNumberOfEvents(String text) {
        assertThat(page.getPageTextByClassName("qa-card-events")).isEqualTo(text);
    }

    @Then("^they should see the active events data as \"([^\"]*)\"$")
    public void theyShouldSeeActiveEventData(String text) {
        assertThat(page.getPageTextByClassName("qa-card-active-event")).isEqualTo(text);
    }

    @Then("^they should see offender status as \"([^\"]*)\"$")
    public void theyShouldSeeOffenderStatus(String text) {
        assertThat(page.getPageTextByClassName("qa-card-current-status")).isEqualTo(text);
    }

    @Then("^they should see provider data as \"([^\"]*)\"$")
    public void theyShouldSeeProviderData(String text) {
        assertThat(page.getPageTextByClassName("qa-card-provider")).isEqualTo(text);
    }

    @Then("^they should see offender manager data as \"([^\"]*)\"$")
    public void theyShouldSeeOffenderManagerData(String text) {
        assertThat(page.getPageTextByClassName("qa-card-offender-manager")).isEqualTo(text);
    }
}
