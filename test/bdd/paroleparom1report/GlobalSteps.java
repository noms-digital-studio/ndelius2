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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

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
    private String whatToIncludeFieldLabel;


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

    @When("^they enter the following information into a classic TextArea$")
    public void theyEnterTheFollowingInformationIntoAClassicTextArea(DataTable fieldTexts) {
        val labelTextMap = fieldTexts.asMap(String.class, String.class);
        labelTextMap.forEach((label, text) -> page.fillClassicTextArea(label, text));
        fieldNameToValues = toNameValues(labelTextMap);
    }

    @When("^they input the following information$")
    public void theyInputTheFollowingInformation(DataTable fieldTexts) {
        val labelTextMap = fieldTexts.asMap(String.class, String.class);
        labelTextMap.forEach((label, text) -> page.fillInput(label, text));
        fieldNameToValues = toNameValues(labelTextMap);
    }

    @When("^they enter the date \"([^\"]*)\" for \"([^\"]*)\"$")
    public void theyEnterTheDateFor(String dateText, String legend) throws ParseException {
        val date = new SimpleDateFormat("dd/MM/yyyy").parse(dateText);

        page.fillInputInSectionWithLegend(legend, "Day", new SimpleDateFormat("dd").format(date));
        page.fillInputInSectionWithLegend(legend, "Month", new SimpleDateFormat("MM").format(date));
        page.fillInputInSectionWithLegend(legend, "Year", new SimpleDateFormat("yyyy").format(date));
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

    @Then("^the following information should be saved in the prisoner parole report$")
    public void theFollowingInformationShouldBeSavedInThePrisonerParoleReport(DataTable fieldNameToValuesTable) throws Throwable {
        control.await().until(unused ->
                alfrescoStoreMock.verifySavedDocumentContainsValues(fieldNameToValuesTable.asMap(String.class, String.class)));
    }


    @Given("^the user does not any enter any characters in the free text fields on the page$")
    public void theUserDoesNotAnyEnterAnyCharactersInTheFreeTextFieldsOnThePage() {
        // no page action required
    }
    @Given("^that the user does not select an option on the page$")
    public void thatTheUserDoesNotSelectAnOptionOnThePage() {
        // no page action required
    }
    @Given("^that the user enters no information on the page$")
    public void thatTheUserEntersNoInformationOnThePage() {
        // no page action required
    }
    @When("^they don't enter text into the \"([^\"]*)\"$")
    public void theyDonTEnterTextIntoThe(String label) {
        // no page action required
    }

    @Then("^the following error messages are displayed$")
    public void theFollowingErrorMessagesAreDisplayed(DataTable errorFieldMessages) {
        val nameErrorMessages = toNameValues(errorFieldMessages.asMap(String.class, String.class));
        nameErrorMessages.forEach((name, message) -> assertThat(page.errorMessage(name)).isEqualTo(nameErrorMessages.get(name)));
    }

    @Then("^the following error messages for each field are displayed$")
    public void theFollowingErrorMessagesForEachFieldAreDisplayed(DataTable errorFieldMessages) {
        val nameErrorMessages = errorFieldMessages.asMap(String.class, String.class);
        nameErrorMessages.forEach((name, message) -> assertThat(page.errorMessage(name)).isEqualTo(nameErrorMessages.get(name)));
    }

    @When("^they select the radio button with id \"([^\"]*)\"$")
    public void theySelectTheOptionWithTheId(String id)  {
        page.clickElementWithId(id);
    }


    @When("^they select the \"([^\"]*)\" option on the \"([^\"]*)\"$")
    public void theySelectTheOptionOnThe(String label, String legend)  {
        page.clickRadioButtonWithLabelWithinLegend(label, legend);
    }

    @Given("^that the \"([^\"]*)\" is ticked$")
    public void thatTheIsTicked(String checkboxLabel) {
        page.clickCheckboxWithLabel(checkboxLabel);
    }

    @Given("^that the Delius user is unclear to what information they need to add to the \"([^\"]*)\" free text field$")
    public void thatTheDeliusUserIsUnclearToWhatInformationTheyNeedToAddToTheFreeTextField(String label) {
        this.whatToIncludeFieldLabel = label;
    }

    @When("^they select \"([^\"]*)\" hyperlink$")
    public void theySelectHyperlink(String text) {
        page.clickSpanWithSiblingLabel(text, whatToIncludeFieldLabel);
    }

    @Then("^the UI should expand to show additional content to the end user$")
    public void theUIShouldExpandToShowAdditionalContentToTheEndUser() {
        assertThat(page.whatToIncludeContentVisibleWithSiblingLabel(whatToIncludeFieldLabel)).isTrue();
    }

    @When("^they select \"([^\"]*)\" hyperlink from the UI$")
    public void theySelectHyperlinkFromTheUI(String linkText) {
        page.clickLink(linkText);
    }

    @When("^they are select \"([^\"]*)\" link on the navigation menu$")
    public void theyAreSelectLinkOnTheNavigationMenu(String linkText) {
        page.clickLink(linkText);
    }

    @Then("^they must be directed to \"([^\"]*)\" UI$")
    public void theyMustBeDirectedToUI(String pageTitle) {
        page.isAt(pageTitle);
    }

}