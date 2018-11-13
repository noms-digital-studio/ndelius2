package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.OffenderAssessmentPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.OFFENDER_ASSESSMENT;

public class OffenderAssessmentSteps {
    @Inject
    private OffenderAssessmentPage page;

    @Given("^that the Delius user is on the \"Offender assessment\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(OFFENDER_ASSESSMENT.getPageHeader());
    }

    @Given("^Delius User completes the \"Offender assessment\" UI within the Short Format Pre-sentence Report")
    public void deliusUserCompletesThePageWithinTheReport() throws Throwable {
        page.clickCheckboxWithLabel("Accommodation");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Is there evidence of the offender experiencing trauma?");
        page.clickRadioButtonWithLabelWithinLegend("Yes", "Does the offender have caring responsibilities for children or adults, or have they ever had caring responsibilities for children or adults?");
        page.fillTextArea("Experience of trauma", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.fillTextArea("Caring responsibilities", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
