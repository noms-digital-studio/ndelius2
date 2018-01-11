package views;

import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import interfaces.Search;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.CheckYourReportPage;
import views.pages.OffenderAssessmentPage;
import views.pages.StartPage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static helpers.JsonHelper.jsonToMap;
import static helpers.JsonHelper.stringify;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.libs.Json.toJson;

@RunWith(MockitoJUnitRunner.class)
public class OffenderAssessmentWebTest extends WithBrowser {
    @Mock
    private DocumentStore alfrescoDocumentStore;
    @Captor
    private ArgumentCaptor<String> metaDataCaptor;
    @Mock
    private Search search;

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
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(alfrescoDocumentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
    }

    @Test
    public void issuesWillContainAllCommonOptions() {
        assertThat(offenderAssessmentPage.navigateHere().issues()).contains(issueOptions);
    }

    @Test
    public void savingDraftWillStoreAllValues() {
        offenderAssessmentPage.navigateHere();
        givenAllIssuesAreTicked();

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
                contains(entry("issueOtherDetails", "Other (Please specify below) details"));

    }

    @Test
    public void noneTickedResultsInSingleErrorMessage() {
        offenderAssessmentPage.navigateHere();

        whenFormIsSubmitted();

        assertThat(offenderAssessmentPage.countErrors("Select underlying issues from the options below")).isEqualTo(1);
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
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("issueDrugs", "true", "pageNumber", "7")));

        startPage.navigateWithExistingReport();


        assertThat(offenderAssessmentPage.isTicked("Substance misuse")).isTrue();
    }

    @Test
    public void legacyReportWithAlcoholIssueDetailsBecomesOtherIssueWithDetails() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("issueAlcohol", "true", "pageNumber", "7")));

        startPage.navigateWithExistingReport();


        assertThat(offenderAssessmentPage.isTicked("Substance misuse")).isTrue();
    }

    @Test
    public void legacyReportWithAlcoholIssueBecomesSubstanceMisuse() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of("offenderAssessment", "some offender assessment", "pageNumber", "7")));

        startPage.navigateWithExistingReport();


        assertThat(offenderAssessmentPage.isTicked("Other (Please specify below)")).isTrue();
        assertThat(offenderAssessmentPage.associatedDetailsFor("Other (Please specify below)")).isEqualTo("some offender assessment");
    }
    
    @Test
    public void existingReportWithNoIssuesHasNothingTickedAndNoDetails() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
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

        startPage.navigateWithExistingReport();

        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.isTicked(issue)).isFalse().describedAs(issue));
        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.associatedDetailsFor(issue)).isEqualTo("").describedAs(issue));
    }
    
    @Test
    public void existingReportWithAllIssuesHasEverythingTickedWithDetails() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
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

        startPage.navigateWithExistingReport();

        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.isTicked(issue)).isTrue().describedAs(issue));
        stream(issueOptions).forEach(issue -> assertThat(offenderAssessmentPage.associatedDetailsFor(issue)).isEqualTo(issue + " details").describedAs(issue));
    }

    private CompletionStage<String> legacyReportWith(ImmutableMap<String, Object> values) {
        val originalReport = Json.parse(getClass().getResourceAsStream("/alfrescodata/legacyOffenderAssessment.json"));

        val reportJson = stringify(toJson(merge(
                ImmutableMap.of("templateName", originalReport.get("templateName").asText()),
                ImmutableMap.of("values", merge(
                        jsonToMap(originalReport.get("values")),
                        values)))));
        return CompletableFuture.completedFuture(reportJson);
    }

    private Map<String, Object> merge(Map<String, String> original, Map<String, Object> additions) {
        Map<String, Object> mergedValues = new HashMap<>();
        mergedValues.putAll(original);
        mergedValues.putAll(additions);
        return ImmutableMap.copyOf(mergedValues);
    }

    private Map<String, String> storedData() {
        verify(alfrescoDocumentStore, atLeastOnce()).updateExistingPdf(any(), any(), any(), metaDataCaptor.capture(), any());
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

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(alfrescoDocumentStore),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock()),
                bind(Search.class).toInstance(search)
            )
            .build();
    }

}
