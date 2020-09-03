package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import play.Environment;
import play.Mode;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static scala.io.Source.fromInputStream;

public class ProbationSearchApiMock {
    @Inject
    @Named("probationSearchApiWireMock")
    private WireMockServer custodyApiWireMock;

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

    public ProbationSearchApiMock start() {
        custodyApiWireMock.start();
        return this;
    }

    public ProbationSearchApiMock stop() {
        custodyApiWireMock.stop();
        return this;
    }

    public ProbationSearchApiMock stubSearchWithResource(String resource) {
        custodyApiWireMock.stubFor(
                post(urlPathMatching("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource(String
                                        .format("/probationoffendersearch/%s", resource)))));


        return this;
    }


    public void stubDefaults() {
        custodyApiWireMock.stubFor(
                post(urlPathMatching("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));
    }
}
