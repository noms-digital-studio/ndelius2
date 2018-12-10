package bdd.offendersummary;

import bdd.wiremock.OffenderApiMock;
import cucumber.api.java.en.Given;
import views.pages.offendersummary.OffenderSummaryPage;

import javax.inject.Inject;

public class OffenderDetailsSteps {

    @Inject
    private OffenderSummaryPage page;

    @Inject
    private OffenderApiMock offenderApiMock;

    @Given("^offender record has a main address registered$")
    public void offenderRecordHasMainAddress() {

    }
}
