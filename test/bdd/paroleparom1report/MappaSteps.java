package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.MappaPage;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MappaSteps {
    @Inject
    private MappaPage page;

    @Given("^Delius User is on the \"MAPPA\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"MAPPA\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();

        page.clickRadioButtonWithLabelWithinLegend("Yes", "Is the prisoner eligible for MAPPA?");
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Day", new SimpleDateFormat("dd").format(date));
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Month", new SimpleDateFormat("MM").format(date));
        page.fillInputInSectionWithLegend("When was the prisoner screened for MAPPA (MAPPA Q completed)?", "Year", new SimpleDateFormat("yyyy").format(date));
        page.clickRadioButtonWithLabelWithinLegend("1", "What is the prisoner`s current MAPPA category?");
        page.clickRadioButtonWithLabelWithinLegend("1", "What is the prisoner`s current MAPPA level?");
        page.clickButton("Continue");
    }
}
