package bdd.paroleparom1report;

import bdd.wiremock.AlfrescoStoreMock;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.val;
import play.test.TestBrowser;
import views.pages.paroleparom1report.ParoleParom1PopupReportPage;

import javax.inject.Inject;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GlobalSteps {
    @Inject
    private ParoleParom1PopupReportPage page;
    @Inject
    private TestBrowser control;
    @Inject
    private AlfrescoStoreMock alfrescoStoreMock;

    private Map<String, String> fieldNameToValues;


    @When("^they select the \"([^\"]*)\" button$")
    public void theySelectTheButton(String button) {
        page.clickButton(button);
    }

    @Then("^the user should be directed to the \"([^\"]*)\" UI$")
    public void the_user_should_be_directed_to_UI(String header) {
        page.isAt(header);
    }

    @When("^they enter the following information$")
    public void theyEnterTheFollowingInformation(DataTable fieldTexts) {
        val labelTextMap = fieldTexts.asMap(String.class, String.class);
        labelTextMap.forEach((label, text) -> page.fillTextArea(label, text));
        fieldNameToValues = toNameValues(labelTextMap);
    }

    private Map<String, String> toNameValues(Map<String, String> labelTextMap) {
        return labelTextMap.keySet().stream().map(label -> new SimpleEntry<>(nameFromLabel(label), labelTextMap.get(label))).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private String nameFromLabel(String label) {
        return page.fieldNameFromLabel(label);
    }

    @Then("^this information should be saved in the prisoner parole report$")
    public void thisInformationShouldBeSavedInThePrisonerParoleReport() {
        control.await().until(unused ->
                alfrescoStoreMock.verifySavedDocumentContainsValues(fieldNameToValues));
    }

    @Given("^the user does not any enter any characters in the free text fields on the page$")
    public void theUserDoesNotAnyEnterAnyCharactersInTheFreeTextFieldsOnThePage() {
        // no page action required
    }

    @Then("^the following error messages are displayed$")
    public void theFollowingErrorMessagesAreDisplayed(DataTable errorFieldMessages) {
        val nameErrorMessages = toNameValues(errorFieldMessages.asMap(String.class, String.class));
        nameErrorMessages.forEach((name, message) -> assertThat(page.errorMessage(name)).isEqualTo(nameErrorMessages.get(name)));
    }



}