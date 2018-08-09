package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import views.pages.OffenderAssessmentPage;
import views.pages.OffenderDetailsPage;
import views.pages.StartPage;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;
import static views.helpers.AlfrescoDataHelper.legacyReportWith;

@RunWith(MockitoJUnitRunner.class)
public class InterstitialWebTest extends WithBrowser {
    @Mock
    private DocumentStore alfrescoDocumentStore;

    private OffenderAssessmentPage offenderAssessmentPage;
    private OffenderDetailsPage offenderDetailsPage;
    private StartPage startPage;

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        offenderDetailsPage = new OffenderDetailsPage(browser);
        startPage = new StartPage(browser);
        when(alfrescoDocumentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(alfrescoDocumentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
    }

    @Test
    public void reportSaveOnPage1WillTakeYouToPage2WhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "1")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasInvalidZeroPageNumberWillTakeYouToPage2WhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "0")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderAssessmentPage.isAt();

    }

    @Test
    public void reportThatIsLessThanMinuteOldShowsChangedSecondsAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(2, ChronoUnit.SECONDS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).containsPattern("[0-9] seconds ago");
    }

    @Test
    public void reportThatIsLessThanHourOldShowsChangedMinutesAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(58, ChronoUnit.MINUTES)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("58 minutes ago");
    }

    @Test
    public void reportThatIsLessThanTwoMinutesOldShowsChangedMinuteAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(1, ChronoUnit.MINUTES)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("1 minute ago");
    }

    @Test
    public void reportThatIsLessThanDayOldShowsChangedHoursAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(23, ChronoUnit.HOURS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("23 hours ago");
    }

    @Test
    public void reportThatIsMoreThanDayOldShowsChangedDaysAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(25, ChronoUnit.HOURS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("1 day ago");
    }

    @Test
    public void reportThatIsReallyOldShowsChangedDaysAgo() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(10, ChronoUnit.YEARS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).containsPattern("365[0-9] days ago"); // not exactly 3650 due to leap years
    }


    @Test
    public void continuingAReportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(alfrescoDocumentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();
        offenderAssessmentPage.isAt();

        startPage.switchToWindow().gotoNext();
        offenderAssessmentPage.isAt();

    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        OffenderApi offenderApi = mock(OffenderApi.class);
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(alfrescoDocumentStore),
                bind(OffenderApi.class).toInstance(offenderApi),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)))
            .configure("params.user.token.valid.duration", "100000d")
            .build();
    }

}
