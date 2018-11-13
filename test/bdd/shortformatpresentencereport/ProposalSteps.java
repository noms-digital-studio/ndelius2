package bdd.shortformatpresentencereport;

import cucumber.api.java.en.Given;
import views.pages.shortformatpresentencereport.ProposalPage;

import javax.inject.Inject;

import static views.pages.shortformatpresentencereport.Page.PROPOSAL;

public class ProposalSteps {
    @Inject
    private ProposalPage page;

    @Given("^that the Delius user is on the \"Proposal\" page within the Short Format Pre-sentence Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheReport() throws Throwable {
        page.navigateHere();
        page.isAt(PROPOSAL.getPageHeader());
    }

    @Given("^Delius User completes the \"Proposal\" UI within the Short Format Pre-sentence Report$")
    public void deliusUserCompletesThePageWithinTheReport() throws Throwable {
        page.fillTextArea("Enter a proposed sentence", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum nec sem eget lacus euismod vulputate sit amet sed nulla.");
        page.clickButton("Continue");
    }
}
