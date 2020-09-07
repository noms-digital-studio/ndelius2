package bdd.nationalsearch;

import bdd.wiremock.ProbationSearchApiMock;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.nationalsearch.NationalSearchPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NationalSearchSteps {

    @Inject
    private NationalSearchPage page;

    @Inject
    private ProbationSearchApiMock probationSearchApiMock;

    @When("^the user is on the National Search page$")
    public void theUserIsOnTheNationalSearchPage() {
        page.navigateHere();
    }

    @Then("^the user must see an input field for a search phrase$")
    public void theUserMustSeeAnInputFieldForASearchPhrase() {
        assertThat(page.verifySearchInput()).isTrue();
    }

    @And("^see a link to \"([^\"]*)\"$")
    public void seeALinkTo(String linkText) {
        assertThat(page.hasLinkWithTest(linkText)).isTrue();
    }

    @And("^that the offender has the following data from json file \"([^\"]*)\" in elastic search$")
    public void thatTheOffenderHasTheFollowingDataFromJsonFileInElasticSearch(String fileName) {
        probationSearchApiMock.stubSearchWithResource(fileName + ".json");
    }

    @When("^I search for \"([^\"]*)\"$")
    public void iSearchFor(String searchTerm) {
        page.enterSearchPhrase(searchTerm);
    }

    @And("^the search results are returned$")
    public void theSearchResultsIReturned() {
        assertThat(page.searchResultsPresent()).isTrue();
    }

    @Then("^I see \"([^\"]*)\" search result\\(s\\)$")
    public void iSeeSearchResultS(String expectedCounted) {
        assertThat(page.countResultsDisplayed()).isEqualTo(Integer.parseInt(expectedCounted));
    }

    @And("^The offender with crn \"([^\"]*)\" in the results$")
    public void theOffenderWithCrnInTheResults(String crn) {
        assertThat(page.allResultsText()).contains(crn);
    }

    @Then("^I see \"([^\"]*)\" as a suggested search alternative$")
    public void iSeeAsASuggestedSearchAlternative(String suggest)  {
        assertThat(page.suggestionText()).contains(suggest);
    }

    @And("^wait a little while$")
    public void waitALittleWhile() {
        page.await().atMost(10000).until((e) -> false);
    }

    @Then("^search field is pre filled with \"([^\"]*)\"$")
    public void searchFieldIsPreFilledWith(String searchTerm)  {
        assertThat(page.enteredSearchPhrase()).isEqualTo(searchTerm);
    }

    @Then("^I see my providers filter$")
    public void iSeeMyProvidersFilter() {
        assertThat(page.myProvidersFilterPresent()).isTrue();
    }

    @And("^I see other providers filter$")
    public void iSeeOtherProvidersFilter() {
        assertThat(page.otherProvidersFilterPresent()).isTrue();
    }
}
