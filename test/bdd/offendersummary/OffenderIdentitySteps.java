package bdd.offendersummary;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class OffenderIdentitySteps {
    @Inject
    private OffenderSummaryPage page;

    @When("^they navigate to the offender summary page$")
    public void theyNavigateToTheOffenderSummaryPage() {
        page.navigateHere();
    }

    @Then("^they see the offender name as \"([^\"]*)\"$")
    public void theySeeTheOffenderNameAs(String name) {
        assertThat(page.getOffenderName()).isEqualTo(name);
    }

    @And("^they see the offender CRN as \"([^\"]*)\"$")
    public void theySeeTheOffenderCRNAs(String crn)  {
        assertThat(page.getOffenderCRN()).isEqualTo(crn);
    }

    @And("^they see the offender date of birth as \"([^\"]*)\"$")
    public void theySeeTheOffenderDateOfBirthAs(String dateOfBirth) {
        assertThat(page.getOffenderDateOfBirth()).isEqualTo(dateOfBirth);
    }

    @And("^they see the offender mugshot$")
    public void theySeeTheOffenderMugshot() {
        assertThat(page.getOffenderImageUrl()).matches("^.*/offender/oneTimeNomisRef/.*/image$");
    }

    @And("^they see the offender placeholder mugshot$")
    public void theySeeTheOffenderPlaceholderMugshot() {
        assertThat(page.getOffenderImageUrl()).matches("^.*/assets/images/NoPhoto@2x.png$");
    }
}