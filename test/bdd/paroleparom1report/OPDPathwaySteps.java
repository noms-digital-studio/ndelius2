package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import views.pages.paroleparom1report.OPDPathwayPage;

import javax.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();

        page.fillInputInSectionWithLegend("Date of OPD screening", "Day", new SimpleDateFormat("dd").format(date));
        page.fillInputInSectionWithLegend("Date of OPD screening", "Month", new SimpleDateFormat("MM").format(date));
        page.fillInputInSectionWithLegend("Date of OPD screening", "Year", new SimpleDateFormat("yyyy").format(date));
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has the prisoner been screened into the OPD pathway (OPD criteria met)?");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Have you received consultation or a formulation?");
        page.clickButton("Continue");
    }
}