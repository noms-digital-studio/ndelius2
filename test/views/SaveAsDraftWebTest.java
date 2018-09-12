package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import views.pages.CheckYourReportPage;
import views.pages.DraftSavedConfirmationPage;
import views.pages.OffenderAssessmentPage;
import views.pages.OffenderDetailsPage;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class SaveAsDraftWebTest extends WithIE8Browser {
    private OffenderAssessmentPage offenderAssessmentPage;
    private OffenderDetailsPage offenderDetailsPage;
    private DraftSavedConfirmationPage draftSavedConfirmationPage;
    private CheckYourReportPage checkYourReportPage;

    @Before
    public void before() {
        offenderAssessmentPage = new OffenderAssessmentPage(browser);
        offenderDetailsPage = new OffenderDetailsPage(browser);
        draftSavedConfirmationPage = new DraftSavedConfirmationPage(browser);
        checkYourReportPage = new CheckYourReportPage(browser);
        when(documentStore.updateExistingPdf(any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(ImmutableMap.of("ID", "123")));
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), any()))
                .willReturn(CompletableFuture.completedFuture(someCourtAppearances()));

    }

    @Test
    public void savingDraftWillStoreAllValues() {

        offenderAssessmentPage.navigateHere();

        whenSaveAsDraftIsClicked();

        verify(documentStore, atLeastOnce()).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void savingDraftDisplaysConfirmationPage() {
        offenderAssessmentPage.navigateHere();
        whenSaveAsDraftIsClicked();

        draftSavedConfirmationPage.isAt();
    }

    @Test
    public void editReportAfterSavingReportDisplaysCheckYourReportPage() {
        offenderAssessmentPage.navigateHere();
        whenSaveAsDraftIsClicked();

        draftSavedConfirmationPage.updateReport();

        checkYourReportPage.isAt();
    }

    @Test
    public void editReportAfterSavingAsDraftMaintainsEncryptedFieldsCorrectly() {
        offenderDetailsPage.navigateHere();
        offenderDetailsPage.populateAddress("22 Acacia Avenue");

        whenSaveAsDraftIsClicked();
        draftSavedConfirmationPage.updateReport();
        checkYourReportPage.clickOffenderDetailsLink();

        assertThat(offenderDetailsPage.address()).isEqualTo("22 Acacia Avenue");
    }

    private void whenSaveAsDraftIsClicked() {
        offenderAssessmentPage.saveAsDraft();
    }


}
