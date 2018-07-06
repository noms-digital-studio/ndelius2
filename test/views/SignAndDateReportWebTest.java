package views;

import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import views.pages.SignAndDateReportPage;
import views.pages.StartPage;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class SignAndDateReportWebTest extends WithIE8Browser {
    private SignAndDateReportPage signAndDateReportPage;
    private StartPage startPage;
    private DocumentStore documentStore;

    @Before
    public void before() {
        signAndDateReportPage = new SignAndDateReportPage(browser);
        startPage = new StartPage(browser);
    }

    @Test
    public void shouldBePresentedWithReportAuthorField() {
        assertThat(signAndDateReportPage.navigateHere().hasReportAuthorField()).isTrue();
    }

    @Test
    public void shouldBePresentedWithCounterSignatureField() {
        assertThat(signAndDateReportPage.navigateHere().hasCounterSignatureField()).isTrue();
    }

    @Test
    public void shouldBePresentedWithCourtOfficePhoneNumberField() {
        assertThat(signAndDateReportPage.navigateHere().hasCourtOfficePhoneNumberField()).isTrue();
    }

    @Test
    public void shouldBePresentedWithReadOnlyStartDateField() {
        val signAndDateReportPage = this.signAndDateReportPage.navigateHere();
        assertThat(signAndDateReportPage.hasStartDateField()).isTrue();
        assertThat(signAndDateReportPage.isStartDateFieldReadonly()).isTrue();
    }

    @Test
    public void shouldBePresentedWithReadOnlyStartDateFieldUsingTodaysDateForNewReport() {
        assertThat(signAndDateReportPage.navigateHere().getStartDate()).isEqualTo(todaysDate());
    }

    @Test
    public void startDateFieldIsPopulatedWhenEditingAnExistingReport() {
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData(reportDataWithStartDateOf("25/12/2017"), OffsetDateTime.now())));
        startPage.navigateWithExistingReport();
        startPage.navigateWithExistingReport().gotoNext();
        assertThat(signAndDateReportPage.getStartDate()).isEqualTo("25/12/2017");
    }

    @Test
    public void startDateFieldIsEmptyWhenEditingALegacyReport() {
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(this::legacyReportData));
        startPage.navigateHere();
        assertThat(signAndDateReportPage.getStartDate()).isEqualTo(null);
    }

    @Test
    public void nextButtonStatesSubmit() {
        assertThat(signAndDateReportPage.navigateHere().getNextButtonText()).contains("Submit");
    }

    private String reportDataWithStartDateOf(String startDate) {
        return String.format("{\"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"11\", \"name\": \"Smith,John\", \"address\": \"1234\", \"pnc\": \"Retrieved From Store\",  \"startDate\": \"%s\" } }", startDate);
    }

    private DocumentStore.OriginalData legacyReportData() {
        return new DocumentStore.OriginalData("{\"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"11\", \"name\": \"Smith,John\", \"address\": \"1234\", \"pnc\": \"Retrieved From Store\" } }", OffsetDateTime.now());
    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        documentStore = mock(DocumentStore.class);
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));

        OffenderApi offenderApi = mock(OffenderApi.class);
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(documentStore),
                bind(OffenderApi.class).toInstance(offenderApi),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)))
            .configure("params.user.token.valid.duration", "100000d")
            .build();
    }

    private String todaysDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }
}
