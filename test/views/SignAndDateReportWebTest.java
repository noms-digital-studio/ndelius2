package views;

import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.SignAndDateReportPage;

import static org.assertj.core.api.Assertions.assertThat;
import static play.inject.Bindings.bind;

public class SignAndDateReportWebTest extends WithBrowser {
    private SignAndDateReportPage signAndDateReportPage;

    @Before
    public void before() {
        signAndDateReportPage = new SignAndDateReportPage(browser);
    }

    @Test
    public void shouldNavigateToSignAndDateYourReportPage() {
        assertThat(signAndDateReportPage.navigateHere().getMainHeading()).isEqualTo("Sign and date your report");
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
