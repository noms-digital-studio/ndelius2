package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.SourcesPage;

import javax.inject.Inject;

public class SourcesSteps {
    @Inject
    private SourcesPage page;

    @Given("^Delius user is on the \"Sources\" UI within the Parole Report$")
    public void deliusUserIsOnTheUIWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"Sources\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickCheckboxWithLabel("Previous convictions");
        page.clickCheckboxWithLabel("Crown Prosecution Service (CPS) documents");
        page.clickCheckboxWithLabel("Judges comments");
        page.clickCheckboxWithLabel("Parole dossier");
        page.clickCheckboxWithLabel("Probation case records (nDelius)");
        page.clickCheckboxWithLabel("Previous parole reports");
        page.clickCheckboxWithLabel("Pre-sentence report");
        page.clickCheckboxWithLabel("Other");
        page.fillTextArea("Detail any other case documents you have used", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("List all of the reports, assessments and directions you have used for this PAROM 1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Has any information not been made available to you, or are there any limitations to the sources?");
        page.fillTextArea("Provide an explanation", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
