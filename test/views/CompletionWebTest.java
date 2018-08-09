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
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import views.pages.CheckYourReportPage;
import views.pages.CompletionPage;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class CompletionWebTest extends WithIE8Browser {
    private CompletionPage completionPage;
    private CheckYourReportPage checkYourReportPage;

    @Before
    public void before() {
        completionPage = new CompletionPage(browser);
        checkYourReportPage = new CheckYourReportPage(browser);
    }

    @Test
    public void signingReportNavigatesToCompletionPage() {
        completionPage.navigateHere().isAt();
    }

    @Test
    public void editReportAfterSavingReportDisplaysCheckYourReportPage() {
        completionPage.navigateHere();

        completionPage.updateReport();

        checkYourReportPage.isAt();
    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        DocumentStore documentStore = mock(DocumentStore.class);
        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456")));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"456\", \"pnc\": \"Retrieved From Store\", \"startDate\": \"12/12/2017\", \"crn\": \"1234\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\" } }", OffsetDateTime.now())));

        OffenderApi offenderApi = mock(OffenderApi.class);
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(documentStore),
                bind(OffenderApi.class).toInstance(offenderApi),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)))
            .configure("params.user.token.valid.duration", "100000d")
            .build();
    }

}
