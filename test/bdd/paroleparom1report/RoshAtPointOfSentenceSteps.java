package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshAtPointOfSentencePage;

import javax.inject.Inject;

public class RoshAtPointOfSentenceSteps {
    @Inject
    private RoshAtPointOfSentencePage page;

    @Given("^Delius user is on the \"RoSH at point of sentence\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"RoSH at point of sentence\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Was a RoSH assessment completed at the point of sentence?");
        page.fillInputInSectionWithLegend("When was the RoSH assessment completed?", "Month", "07");
        page.fillInputInSectionWithLegend("When was the RoSH assessment completed?", "Year", "2018");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Public");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Known adult");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Children");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Prisoners");
        page.clickRadioButtonWithLabelWithinLegend("Low", "Staff");
        page.fillTextArea("What is the prisoner`s attitude to the index offence?", "Prisoner's attitude to the index offence text");
        page.fillTextArea("What is the prisoner`s attitude to their previous offending?", "Prisoner's attitude to their previous offending text");
        page.clickButton("Continue");
    }

}
