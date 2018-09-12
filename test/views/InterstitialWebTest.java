package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import views.pages.OffenderAssessmentPage;
import views.pages.OffenderDetailsPage;
import views.pages.StartPage;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;
import static views.helpers.AlfrescoDataHelper.legacyReportWith;

@RunWith(MockitoJUnitRunner.class)
public class InterstitialWebTest extends WithPartialMockedApplicationBrowser {
    private OffenderAssessmentPage offenderAssessmentPage;
    private OffenderDetailsPage offenderDetailsPage;
    private StartPage startPage;

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        offenderDetailsPage = new OffenderDetailsPage(browser);
        startPage = new StartPage(browser);
        when(documentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));

    }

    @Test
    public void reportSaveOnPage1WillTakeYouToPage2WhenReturning() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "1")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasInvalidZeroPageNumberWillTakeYouToPage2WhenReturning() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "0")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderDetailsPage.isAt();

    }

    @Test
    public void reportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();

        offenderAssessmentPage.isAt();

    }

    @Test
    public void reportThatIsLessThanMinuteOldShowsChangedSecondsAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(2, ChronoUnit.SECONDS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).containsPattern("[0-9] seconds ago");
    }

    @Test
    public void reportThatIsLessThanHourOldShowsChangedMinutesAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(58, ChronoUnit.MINUTES)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("58 minutes ago");
    }

    @Test
    public void reportThatIsLessThanTwoMinutesOldShowsChangedMinuteAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(1, ChronoUnit.MINUTES)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("1 minute ago");
    }

    @Test
    public void reportThatIsLessThanDayOldShowsChangedHoursAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(23, ChronoUnit.HOURS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("23 hours ago");
    }

    @Test
    public void reportThatIsMoreThanDayOldShowsChangedDaysAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(25, ChronoUnit.HOURS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).contains("1 day ago");
    }

    @Test
    public void reportThatIsReallyOldShowsChangedDaysAgo() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of(), OffsetDateTime.now().minus(10, ChronoUnit.YEARS)));

        assertThat(startPage.navigateWithExistingReport().lastUpdatedText()).containsPattern("365[0-9] days ago"); // not exactly 3650 due to leap years
    }


    @Test
    public void continuingAReportThatHasValidPageNumberWillTakeYouToPageWhenReturning() {
        when(documentStore.retrieveOriginalData(any(), any())).
                thenReturn(legacyReportWith(
                        ImmutableMap.of( "pageNumber", "3")));

        startPage.navigateWithExistingReport().gotoNext();
        offenderAssessmentPage.isAt();

        startPage.switchToWindow().gotoNext();
        offenderAssessmentPage.isAt();

    }

}
