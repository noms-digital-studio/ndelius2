package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.OPDPathwayPage;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class OPDPathwaySteps {
    @Inject
    private OPDPathwayPage page;

    @Given("^Delius user is on the \"OPD Pathway\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }



    @Given("^Delius User completes the \"OPD Pathway\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("No", "Has the prisoner met the OPD screening criteria and been considered for OPD pathway services?");
        page.fillTextArea("Detail the reasons why the prisoner has not been screened including when it will happen", "Some reason for not screening the prisoner text");
        page.clickButton("Continue");
    }
}