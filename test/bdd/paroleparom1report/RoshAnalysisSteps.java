package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.RoshAnalysisPage;

import javax.inject.Inject;

public class RoshAnalysisSteps {
    @Inject
    private RoshAnalysisPage page;

    @Given("^Delius user is on the \"RoSH analysis\" UI on the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^Delius User completes the \"RoSH analysis\" UI within the Parole Report$")
    public void deliusUserCompletesThePageWithinTheParoleReport() throws Throwable {
        page.fillTextArea("Detail the nature of the risk of serious harm to all relevant groups", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("What factors might increase the risk of serious harm to the relevant groups?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("What factors might decrease the risk of serious harm to the relevant groups?", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("Analyse the likelihood of further offending", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Does the prisoner pose a risk of absconding?");
        page.fillTextArea("Provide details of the absconding risk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
