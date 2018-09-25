package bdd.paroleparom1report;

import cucumber.api.java.en.Given;
import views.pages.paroleparom1report.BehaviourInPrisonPage;

import javax.inject.Inject;

public class BehaviourInPrisonSteps {

    @Inject
    private BehaviourInPrisonPage page;

    @Given("^that the Delius user is on the \"Behaviour in prison\" page within the Parole Report$")
    public void thatTheDeliusUserIsOnThePageWithinTheParoleReport() throws Throwable {
        page.navigateHere();
    }

    @Given("^that the Delius user wants to enter details of the offender's behaviour in Prison in the offender parole report$")
    public void thatTheDeliusUserWantsToEnterDetailsOfTheOffenderSBehaviourInPrisonInTheOffenderParoleReport() throws Throwable {
        // no page action required
    }

    @Given("^that the Delius user has not completed any fields in the \"Behaviour in Prison\" UI$")
    public void thatTheDeliusUserHasNotCompletedAnyFieldsInTheUI() {
        page.doNotNavigateHere();
    }
}
