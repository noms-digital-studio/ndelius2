package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import play.Environment;
import play.Mode;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static scala.io.Source.fromInputStream;

public class CustodyApiMock {
    @Inject
    @Named("custodyApiWireMock")
    private WireMockServer custodyApiWireMock;

    public CustodyApiMock start() {
        custodyApiWireMock.start();
        return this;
    }

    public CustodyApiMock stop() {
        custodyApiWireMock.stop();
        return this;
    }

    public CustodyApiMock stubDefaults() {
        custodyApiWireMock.stubFor(
                post(urlEqualTo("/auth/oauth/token"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/token.json"))));

        custodyApiWireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/offender_G8020GG.json"))));

        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG.json"))));


        return this;
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }


}
