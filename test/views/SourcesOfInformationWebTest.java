package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import interfaces.DocumentStore;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;
import views.pages.SourcesOfInformationPage;
import views.pages.StartPage;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static helpers.JsonHelper.jsonToMap;
import static helpers.JsonHelper.stringify;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.libs.Json.toJson;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class SourcesOfInformationWebTest extends WithIE8Browser {
    @Captor
    private ArgumentCaptor<String> metaDataCaptor;

    private SourcesOfInformationPage sourcesOfInformationPage;
    private StartPage startPage;
    final private static String[] sources = {
            "Interview",
            "Service records",
            "CPS summary",
            "Previous OASys assessments",
            "Previous convictions",
            "Victim statement",
            "Safeguarding checks",
            "Police information",
            "Sentencing guidelines",
            "Other (please specify below)"
    };

    @Before
    public void before() {
        sourcesOfInformationPage = new SourcesOfInformationPage(browser);
        startPage = new StartPage(browser);
        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .willReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .willReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));
        given(offenderApi.getOffencesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someOffences()));
    }

    @Test
    public void issuesWillContainAllCommonOptions() {
        final String[] labels = {
                "Interview",
                "Service records",
                "CPS summary",
                "Previous OASys assessments",
                "Previous convictions",
                "Victim statement",
                "Safeguarding checks",
                "Police information",
                "Sentencing guidelines",
                "Other (please specify below)"
        };

        assertThat(sourcesOfInformationPage.navigateHere().sourcesOfInformation()).contains(labels);
    }

    @Test
    public void savingDraftWillStoreAllValues() {
        sourcesOfInformationPage.navigateHere();
        givenAllSourcesAreTicked();

        whenReportIsSaved();

        assertThat(storedData()).
                contains(entry("interviewInformationSource", "true")).
                contains(entry("serviceRecordsInformationSource", "true")).
                contains(entry("cpsSummaryInformationSource", "true")).
                contains(entry("oasysAssessmentsInformationSource", "true")).
                contains(entry("previousConvictionsInformationSource", "true")).
                contains(entry("victimStatementInformationSource", "true")).
                contains(entry("childrenServicesInformationSource", "true")).
                contains(entry("policeInformationSource", "true")).
                contains(entry("sentencingGuidelinesInformationSource", "true")).
                contains(entry("otherInformationSource", "true")).
                contains(entry("otherInformationDetails", "Other details"));

    }

    @Test
    public void existingReportWithNoSourcesHasNothingTicked() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(existingReportWith(
                        ImmutableMap.<String, Object>builder().
                                put("pageNumber", "4").
                                put("interviewInformationSource", "false").
                                put("serviceRecordsInformationSource", "false").
                                put("cpsSummaryInformationSource", "false").
                                put("oasysAssessmentsInformationSource", "false").
                                put("previousConvictionsInformationSource", "false").
                                put("victimStatementInformationSource", "false").
                                put("childrenServicesInformationSource", "false").
                                put("policeInformationSource", "false").
                                put("sentencingGuidelinesInformationSource", "false").
                                put("otherInformationSource", "false").
                                put("otherInformationDetails", "").
                                build()));

        startPage.navigateWithExistingReport().gotoNext();

        stream(sources).forEach(informationSource -> assertThat(sourcesOfInformationPage.isTicked(informationSource)).isFalse().describedAs(informationSource));
        assertThat(sourcesOfInformationPage.otherInformationDetails()).isEqualTo("");
    }

    @Test
    public void existingReportWithAllSourcesHasEverythingTicked() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(existingReportWith(
                        ImmutableMap.<String, Object>builder().
                                put("pageNumber", "4").
                                put("interviewInformationSource", "true").
                                put("serviceRecordsInformationSource", "true").
                                put("cpsSummaryInformationSource", "true").
                                put("oasysAssessmentsInformationSource", "true").
                                put("previousConvictionsInformationSource", "true").
                                put("victimStatementInformationSource", "true").
                                put("childrenServicesInformationSource", "true").
                                put("policeInformationSource", "true").
                                put("sentencingGuidelinesInformationSource", "true").
                                put("otherInformationSource", "true").
                                put("otherInformationDetails", "Other details").
                                build()));

        startPage.navigateWithExistingReport().gotoNext();

        stream(sources).forEach(informationSource -> assertThat(sourcesOfInformationPage.isTicked(informationSource)).isTrue().describedAs(informationSource));
        assertThat(sourcesOfInformationPage.otherInformationDetails()).isEqualTo("Other details");
    }

    private CompletionStage<DocumentStore.OriginalData> existingReportWith(ImmutableMap<String, Object> values) {
        val originalReport = Json.parse(getClass().getResourceAsStream("/alfrescodata/existingReport.json"));

        val reportJson = stringify(toJson(merge(
                ImmutableMap.of("templateName", originalReport.get("templateName").asText()),
                ImmutableMap.of("values", merge(
                        jsonToMap(originalReport.get("values")),
                        values)))));
        return CompletableFuture.completedFuture(new DocumentStore.OriginalData(reportJson, OffsetDateTime.now()));
    }

    private Map<String, Object> merge(Map<String, String> original, Map<String, Object> additions) {
        Map<String, Object> mergedValues = new HashMap<>();
        mergedValues.putAll(original);
        mergedValues.putAll(additions);
        return ImmutableMap.copyOf(mergedValues);
    }

    private Map<String, String> storedData() {
        verify(documentStore, atLeastOnce()).updateExistingPdf(any(), any(), any(), metaDataCaptor.capture(), any());
        return jsonToMap(Json.parse(metaDataCaptor.getValue()).get("values"));
    }

    private void whenReportIsSaved() {
        sourcesOfInformationPage.attemptNext();
    }


    private void givenAllSourcesAreTicked() {
        stream(sources).forEach(informationSource -> sourcesOfInformationPage.tick(informationSource));
        sourcesOfInformationPage.fillOtherDetailsWith("Other details");
    }


}
