package bdd.paroleparom1report;

import bdd.wiremock.AlfrescoStoreMock;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.val;
import views.pages.paroleparom1report.LandingPage;
import views.pages.paroleparom1report.PrisonerDetailsPage;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;

import static bdd.paroleparom1report.PageHeadings.CURRENT_ROSH_CUSTODY;
import static bdd.wiremock.AlfrescoDocumentBuilder.standardDocument;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LandingPageSteps {
    @Inject
    private LandingPage landingPage;

    @Inject
    private PrisonerDetailsPage prisonerDetailsPage;

    @Inject
    private AlfrescoStoreMock alfrescoStoreMock;


    @Given("^that the user is on the Parole Report landing page$")
    public void that_the_user_is_on_the_Parole_Report_landing_page() {
        landingPage.navigateHere();
    }

    @Then("^the user should be directed to \"([^\"]*)\" UI$")
    public void the_user_should_be_directed_to_UI(String header) {
        prisonerDetailsPage.isAt(header);
    }

    @Given("^that the user is on the Parole Report landing page for an existing report$")
    public void thatTheUserIsOnTheParoleReportLandingPageForAnExistingReport() {
        val documentId = "12345";
        alfrescoStoreMock.stubExistingDocument(
                documentId,
                standardDocument().
                        withValuesItem("pageNumber", CURRENT_ROSH_CUSTODY.getPageNumber()).
                        build());
        landingPage.navigateWithExistingReport(documentId);
    }

    @When("^they select the \"([^\"]*)\" button$")
    public void theySelectTheButton(String button) {
        landingPage.clickButton(button);
    }

    @Then("^the user should be directed to the last page that they were on working on$")
    public void theUserShouldBeDirectedToTheLastPageThatTheyWereOnWorkingOn() {
        prisonerDetailsPage.isAt(CURRENT_ROSH_CUSTODY.getPageHeader());
    }

    @Given("^I had previously edited a report \"([^\"]*)\" minutes ago$")
    public void iHadPreviouslyEditedAReportAgo(String minutes) {
        val lastModifiedDate = now().minusMinutes(Long.parseLong(minutes));
        alfrescoStoreMock.stubExistingDocument(
                "12345",
                standardDocument().
                        withItem("lastModifiedDate", lastModifiedDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).
                        build());
    }

    @When("^when I navigate to the Parole Report landing page for that report$")
    public void whenINavigateToTheParoleReportLandingPageForThatReport() {
        landingPage.navigateWithExistingReport("12345");
    }

    @Then("^I should the the timestamp \"([^\"]*)\" display indicating when the report was last edited$")
    public void iShouldTheTheTimestampDisplayIndicatingWhenTheReportWasLastEdited(String timestamp) {
        assertThat(landingPage.lastUpdatedText()).isEqualTo(timestamp);
    }


}