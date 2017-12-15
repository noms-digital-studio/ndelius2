package views;

import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.SignAndDateReportPage;
import views.pages.StartPage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;

@RunWith(MockitoJUnitRunner.class)
public class SignAndDateReportWebTest extends WithBrowser {
    private SignAndDateReportPage signAndDateReportPage;
    private StartPage startPage;
    @Mock
    private Supplier<String> mockOriginalReportData;

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
        assertThat(signAndDateReportPage.navigateHere().hasStartDateField()).isTrue();
        assertThat(signAndDateReportPage.navigateHere().isStartDateFieldReadonly()).isTrue();
    }

    @Test
    public void shouldBePresentedWithReadOnlyStartDateFieldUsingTodaysDateForNewReport() {
        assertThat(signAndDateReportPage.navigateHere().getStartDate()).isEqualTo(todaysDate());
    }

    @Test
    public void startDateFieldIsPopulatedWhenEditingAnExistingReport() {
        when(mockOriginalReportData.get()).thenReturn(reportDataWithStartDateOf("25/12/2017"));
        startPage.navigateWithExistingReport();
        assertThat(signAndDateReportPage.getStartDate()).isEqualTo("25/12/2017");
    }

    @Test
    public void startDateFieldIsEmptyWhenEditingALegacyReport() {
        when(mockOriginalReportData.get()).thenReturn(legacyReportData());
        startPage.navigateHere();
        assertThat(signAndDateReportPage.getStartDate()).isEqualTo(null);
    }

    @Test
    public void nextButtonStatesViewDocumentList() {
        assertThat(signAndDateReportPage.navigateHere().getNextButtonText()).contains("Submit and view your document list");
    }

    private String reportDataWithStartDateOf(String startDate) {
        return String.format("{\"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"11\", \"name\": \"Smith,John\", \"address\": \"1234\", \"pnc\": \"Retrieved From Store\",  \"startDate\": \"%s\" } }", startDate);
    }

    private String legacyReportData() {
        return "{\"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"11\", \"name\": \"Smith,John\", \"address\": \"1234\", \"pnc\": \"Retrieved From Store\" } }";
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(new DocumentStoreMock()),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock())
            )
            .build();
    }

    private String todaysDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    class DocumentStoreMock extends SimpleDocumentStoreMock {
        public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {
            return CompletableFuture.supplyAsync(mockOriginalReportData);
        }
    }
}
