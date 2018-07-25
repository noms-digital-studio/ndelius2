package bdd.paroleparom1report;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.paroleparom1report.ParoleParom1PopupReportPage;

import javax.inject.Inject;

public class GlobalSteps {
    @Inject
    private ParoleParom1PopupReportPage page;

    @When("^they select the \"([^\"]*)\" button$")
    public void theySelectTheButton(String button) {
        page.clickButton(button);
    }

    @Then("^the user should be directed to the \"([^\"]*)\" UI$")
    public void the_user_should_be_directed_to_UI(String header) {
        page.isAt(header);
    }


}