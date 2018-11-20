package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import helpers.JsonHelper;
import lombok.val;
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

    public CustodyApiMock stubOffenderWithName(String fullName) {
        val offender = JsonHelper.jsonToObjectMap(loadResource("/nomsoffender/offender_G8020GG.json"));
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offender.put("firstName", firstName);
        offender.put("surname", surname);
        custodyApiWireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*"))
                        .willReturn(ok().withBody(JsonHelper.stringify(offender))));

        return this;
    }

    public CustodyApiMock stubOffenderNotFound() {
        custodyApiWireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*"))
                        .willReturn(notFound()));

        return this;
    }

    public CustodyApiMock stubOffenderUnavailable() {
        custodyApiWireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*"))
                        .willReturn(serviceUnavailable()));

        return this;
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }


}
