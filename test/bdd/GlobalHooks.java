package bdd;

import bdd.wiremock.AlfrescoStoreMock;
import bdd.wiremock.CustodyApiMock;
import bdd.wiremock.OffenderApiMock;
import bdd.wiremock.PdfGeneratorMock;
import com.mongodb.rx.client.MongoClient;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.elasticsearch.client.RestHighLevelClient;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.TestBrowser;
import views.WithChromeBrowser;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

public class GlobalHooks extends WithChromeBrowser {
    @Inject
    private TestBrowser theTestBrowser;

    @Inject
    private AlfrescoStoreMock alfrescoStoreMock;
    @Inject
    private PdfGeneratorMock pdfGeneratorMock;
    @Inject
    private OffenderApiMock offenderApiMock;
    @Inject
    private CustodyApiMock custodyApiMock;

    @Before
    public void before() {
        startServer();
        pdfGeneratorMock.start().stubDefaults();
        offenderApiMock.start().stubDefaults();
        alfrescoStoreMock.start().stubDefaults();
        custodyApiMock.start().stubDefaults();

        createBrowser();
    }

    @After
    public void after() {
        pdfGeneratorMock.stop();
        alfrescoStoreMock.stop();
        offenderApiMock.stop();
        custodyApiMock.stop();
        stopServer();
        quitBrowser();
    }

    @Override
    protected TestBrowser provideBrowser(int port) {
        return theTestBrowser;
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "100000d")
                .configure("pdf.generator.url", String.format("http://localhost:%d/", Ports.PDF.getPort()))
                .configure("store.alfresco.url", String.format("http://localhost:%d/", Ports.ALFRESCO.getPort()))
                .configure("offender.api.url", String.format("http://localhost:%d/", Ports.OFFENDER_API.getPort()))
                .configure("nomis.api.url", String.format("http://localhost:%d/", Ports.CUSTODY_API.getPort()))
                .configure("prisoner.api.provider", "elite")
                .configure("custody.api.auth.username", "username")
                .configure("custody.api.auth.password", "password")
                .build();
    }

}
