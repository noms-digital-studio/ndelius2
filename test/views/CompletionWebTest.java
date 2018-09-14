package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import interfaces.DocumentStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import views.pages.CheckYourReportPage;
import views.pages.CompletionPage;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class CompletionWebTest extends WithIE8Browser {
    private CompletionPage completionPage;
    private CheckYourReportPage checkYourReportPage;

    @Before
    public void before() {
        completionPage = new CompletionPage(browser);
        checkYourReportPage = new CheckYourReportPage(browser);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456")));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"456\", \"pnc\": \"Retrieved From Store\", \"startDate\": \"12/12/2017\", \"crn\": \"1234\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\" } }", OffsetDateTime.now())));

        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someCourtAppearances()));
        given(offenderApi.getOffencesByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(someOffences()));
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

}
