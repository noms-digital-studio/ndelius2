package views;

import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.CheckYourReportPage;
import views.pages.CompletionPage;

import static play.inject.Bindings.bind;

@RunWith(MockitoJUnitRunner.class)
public class CompletionWebTest extends WithBrowser {
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
        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(new SimpleDocumentStoreMock()),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock())
            )
            .build();
    }

}
