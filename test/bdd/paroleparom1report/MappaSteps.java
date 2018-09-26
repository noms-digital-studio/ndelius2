package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.MappaPage;

import javax.inject.Inject;

public class MappaSteps {
    @Inject
    private MappaPage page;

    @Given("^Delius User is on the \"MAPPA\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"MAPPA\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Is the prisoner eligible for MAPPA?");
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Day", "19");
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Month", "07");
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Year", "2018");
        page.clickRadioButtonWithLabelWithinLegend("1", "What is the prisoner`s current MAPPA category?");
        page.clickRadioButtonWithLabelWithinLegend("1", "What is the prisoner`s current MAPPA level?");
        page.clickButton("Continue");
    }
}
