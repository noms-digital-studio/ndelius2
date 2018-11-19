package bdd.paroleparom1report;

import bdd.wiremock.AlfrescoStoreMock;
import bdd.wiremock.CustodyApiMock;
import bdd.wiremock.OffenderApiMock;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.val;
import views.pages.paroleparom1report.LandingPage;
import views.pages.paroleparom1report.PrisonerDetailsPage;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;

import static bdd.wiremock.AlfrescoDocumentBuilder.standardDocument;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static views.pages.paroleparom1report.Page.CURRENT_ROSH_CUSTODY;

public class LandingPageSteps {
    @Inject
    private LandingPage landingPage;

    @Inject
    private PrisonerDetailsPage prisonerDetailsPage;

    @Inject
    private AlfrescoStoreMock alfrescoStoreMock;

    @Inject
    private OffenderApiMock offenderApiMock;

    @Inject
    private CustodyApiMock custodyApiMock;


    @Given("^that the user is on the Parole Report landing page$")
    @When("^the user is on the Parole Report landing page$")
    public void that_the_user_is_on_the_Parole_Report_landing_page() {
        landingPage.navigateHere();
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

    @When("^they select the \"([^\"]*)\" button on the landing page$")
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


    @Given("^the prisoner named \"([^\"]*)\" has a valid NOMS number in NOMIS where he is known as \"([^\"]*)\"$")
    public void thePrisonerNamedHasAValidNOMSNumberInNOMISWhereHeIsKnownAs(String deliusName, String nomisName) {
        offenderApiMock.stubOffenderWithName(deliusName);
        custodyApiMock.stubOffenderWithName(nomisName);
    }

    @Given("^the prisoner named \"([^\"]*)\" has no NOMS number$")
    public void thePrisonerNamedHasNoNOMSNumber(String deliusName) {
        offenderApiMock.stubOffenderWithNameAndNoNomsNumber(deliusName);
    }

    @Given("^the prisoner named \"([^\"]*)\" has a NOMS number that matches no prisoner$")
    public void thePrisonerNamedHasANOMSNumberThatMatchesNoPrisoner(String deliusName) {
        offenderApiMock.stubOffenderWithName(deliusName);
        custodyApiMock.stubOffenderNotFound();
    }

    @Given("^the connection for NOMIS API is not working$")
    public void theConnectionForNOMISAPIIsNotWorking() {
        custodyApiMock.stubOffenderUnavailable();
    }

    @Then("^the user must see an image of the prisoner$")
    public void theUserMustSeeAnImageOfThePrisoner() {
        assertThat(landingPage.verifyHasImage()).isTrue();
    }

    @And("^the user must see the prisoner name \"([^\"]*)\"$")
    public void theUserMustSeeThePrisonerName(String fullName) {
        assertThat(landingPage.fullNameText()).isEqualTo(fullName);
    }

    @And("^the user must see the message \"([^\"]*)\"$")
    public void theUserMustSeeTheMessage(String message) {
        assertThat(landingPage.mainWarningMessage()).isEqualTo(message);
    }

}