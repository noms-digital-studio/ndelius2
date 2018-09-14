package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;
import views.pages.CheckYourReportPage;
import views.pages.OffenderAssessmentPage;
import views.pages.StartPage;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static helpers.JsonHelper.jsonToMap;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;
import static views.helpers.AlfrescoDataHelper.legacyReportWith;

@RunWith(MockitoJUnitRunner.class)
public class OffenderAssessmentWebTest extends WithIE8Browser {
    @Captor
    private ArgumentCaptor<String> metaDataCaptor;

    private OffenderAssessmentPage offenderAssessmentPage;
    private StartPage startPage;
    private CheckYourReportPage checkYourReportPage;
    final private static String[] issueOptions = {
            "Accommodation",
            "Employment, training and education",
            "Finance",
            "Relationships",
            "Substance misuse",
            "Physical & mental health",
            "Thinking & behaviour",
            "Other (Please specify below)"
    };

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        startPage = new StartPage(browser);
        checkYourReportPage = new CheckYourReportPage(browser);
        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .willReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .willReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));
        given(offenderApi.getOffencesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someOffences()));
    }

    @Test
    public void issuesWillContainAllCommonOptions() {
        assertThat(offenderAssessmentPage.navigateHere().issues()).contains(issueOptions);
    }

    @Test
    public void savingDraftWillStoreAllValues() {
        offenderAssessmentPage.navigateHere();
        givenAllIssuesAreTicked();
        offenderAssessmentPage.yesWithDetailsFor("experienceTrauma", "Some trauma");
        offenderAssessmentPage.yesWithDetailsFor("caringResponsibilities", "Some caring responsibilities");

        whenReportIsSaved();

        assertThat(storedData()).
                contains(entry("issueAccommodation", "true")).
                contains(entry("issueAccommodationDetails", "Accommodation details")).
                contains(entry("issueEmployment", "true")).
                contains(entry("issueEmploymentDetails", "Employment, training and education details")).
                contains(entry("issueFinance", "true")).
                contains(entry("issueFinanceDetails", "Finance details")).
                contains(entry("issueRelationships", "true")).
                contains(entry("issueRelationshipsDetails", "Relationships details")).
                contains(entry("issueSubstanceMisuse", "true")).
                contains(entry("issueSubstanceMisuseDetails", "Substance misuse details")).
                contains(entry("issueHealth", "true")).
                contains(entry("issueHealthDetails", "Physical & mental health details")).
                contains(entry("issueBehaviour", "true")).
                contains(entry("issueBehaviourDetails", "Thinking & behaviour details")).
                contains(entry("issueOther", "true")).
                contains(entry("issueOtherDetails", "Other (Please specify below) details")).
                contains(entry("experienceTrauma", "yes")).
                contains(entry("experienceTraumaDetails", "Some trauma")).
                contains(entry("caringResponsibilities", "yes")).
                contains(entry("caringResponsibilitiesDetails", "Some caring responsibilities"));

    }

    @Test
    public void nothingEnteredResultsInSingleErrorMessageForIssuesAndOneForEachQuestion() {
        offenderAssessmentPage.navigateHere();

        whenFormIsSubmitted();

        assertThat(offenderAssessmentPage.countErrors("Select underlying issues from the options below")).isEqualTo(1);
        assertThat(offenderAssessmentPage.countErrors("This field is required")).isEqualTo(2);
    }

    @Test
    public void jumpingToCheckYourReportIsAllowedAndWillShowThisPageAsIncompleteWhenNoneTicked() {
        offenderAssessmentPage.navigateHere();

        whenJumpingToPage(CheckYourReportPage.PAGE_NUMBER);

        checkYourReportPage.isAt();
        assertThat(checkYourReportPage.statusForOffenderAssessment()).isEqualTo("INCOMPLETE");
    }

    @Test
    public void legacyReportWithDrugsIssueBecomesSubstanceMisuse() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("issueDrugs", "true", "pageNumber", "7")));

        startPage.navigateWithExistingReport().gotoNext();


        assertThat(offenderAssessmentPage.isTicked("Substance misuse")).isTrue();
    }

    @Test
    public void legacyReportWithAlcoholIssueDetailsBecomesOtherIssueWithDetails() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("issueAlcohol", "true", "pageNumber", "7")));

        startPage.navigateWithExistingReport().gotoNext();


        assertThat(offenderAssessmentPage.isTicked("Substance misuse")).isTrue();
    }

    @Test
    public void legacyReportWithAlcoholIssueBecomesSubstanceMisuse() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("offenderAssessment", "some offender assessment", "pageNumber", "7")));

        startPage.navigateWithExistingReport().gotoNext();


        assertThat(offenderAssessmentPage.isTicked("Other (Please specify below)")).isTrue();
        assertThat(offenderAssessmentPage.associatedDetailsFor("Other (Please specify below)")).isEqualTo("some offender assessment");
    }

    @Test
    public void existingReportWithNoIssuesHasNothingTickedAndNoDetails() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.<String, Object>builder().
                                put("pageNumber", "7").
                                put("issueAccommodation", "false").
                                put("issueAccommodationDetails", "").
                                put("issueEmployment", "false").
                                put("issueEmploymentDetails", "").
                                put("issueFinance", "false").
                                put("issueFinanceDetails", "").
                                put("issueRelationships", "false").
                                put("issueRelationshipsDetails", "").
                                put("issueSubstanceMisuse", "false").
                                put("issueSubstanceMisuseDetails", "").
                                put("issueHealth", "false").
                                put("issueHealthDetails", "").
                                put("issueBehaviour", "false").
                                put("issueBehaviourDetails", "").
                                put("issueOther", "false").
                                put("issueOtherDetails", "").
                                build()));

        startPage.navigateWithExistingReport().gotoNext();

        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.isTicked(issue)).isFalse().describedAs(issue));
        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.associatedDetailsFor(issue)).isEqualTo("").describedAs(issue));
    }

    @Test
    public void existingReportWithAllIssuesHasEverythingTickedWithDetails() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.<String, Object>builder().
                                put("pageNumber", "7").
                                put("issueAccommodation", "true").
                                put("issueAccommodationDetails", "Accommodation details").
                                put("issueEmployment", "true").
                                put("issueEmploymentDetails", "Employment, training and education details").
                                put("issueFinance", "true").
                                put("issueFinanceDetails", "Finance details").
                                put("issueRelationships", "true").
                                put("issueRelationshipsDetails", "Relationships details").
                                put("issueSubstanceMisuse", "true").
                                put("issueSubstanceMisuseDetails", "Substance misuse details").
                                put("issueHealth", "true").
                                put("issueHealthDetails", "Physical & mental health details").
                                put("issueBehaviour", "true").
                                put("issueBehaviourDetails", "Thinking & behaviour details").
                                put("issueOther", "true").
                                put("issueOtherDetails", "Other (Please specify below) details").
                                build()));

        startPage.navigateWithExistingReport().gotoNext();

        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.isTicked(issue)).isTrue().describedAs(issue));
        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.associatedDetailsFor(issue)).isEqualTo(issue + " details").describedAs(issue));
    }

    private Map<String, String> storedData() {
        verify(documentStore, atLeastOnce()).updateExistingPdf(any(), any(), any(), metaDataCaptor.capture(), any());
        return jsonToMap(Json.parse(metaDataCaptor.getValue()).get("values"));
    }

    private void whenReportIsSaved() {
        offenderAssessmentPage.attemptNext();
    }

    private void whenFormIsSubmitted() {
        offenderAssessmentPage.attemptNext();
    }

    private void whenJumpingToPage(String pageNumber) {
        offenderAssessmentPage.jumpToPage(pageNumber);
    }



    private void givenAllIssuesAreTicked() {
        stream(issueOptions).forEach(issue -> offenderAssessmentPage.tick(issue));
        stream(issueOptions).forEach(issue -> offenderAssessmentPage.fillDetailsWith(issue, issue + " details"));
    }

}
