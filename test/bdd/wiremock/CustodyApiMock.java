package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.io.ByteStreams;
import helpers.JsonHelper;
import lombok.val;
import play.Environment;
import play.Mode;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
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
                get(urlMatching("/elite2api/api/offenders/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG.json"))));

        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomselite2offender/offender_G8020GG.json"))));

        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*/image/data"))
                        .willReturn(
                                ok().withHeader(CONTENT_TYPE, "image/jpeg").withBody(loadResourceBytes("/nomsoffender/image_3.jpeg"))));





        return this;
    }

    public CustodyApiMock stubOffenderWithName(String fullName) {
        val offender = JsonHelper.jsonToObjectMap(loadResource("/nomselite2offender/offender_G8020GG.json"));
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offender.put("firstName", firstName);
        offender.put("lastName", surname);
        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/offenders/.*"))
                        .willReturn(ok().withBody(JsonHelper.stringify(offender))));

        return this;
    }

    public CustodyApiMock stubOffenderNotFound() {
        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/offenders/.*"))
                        .willReturn(notFound()));

        return this;
    }

    public CustodyApiMock stubOffenderUnavailable() {
        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/offenders/.*"))
                        .willReturn(serviceUnavailable()));

        return this;
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

    private static byte[] loadResourceBytes(String resource) {
        try {
            return ByteStreams.toByteArray(new Environment(Mode.TEST).resourceAsStream(resource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public CustodyApiMock stubPrisonCategory(String categoryCodeDescription) {
        val offender = JsonHelper.jsonToObjectMap(loadResource("/nomselite2offender/offender_G8020GG.json"));
        val code = categoryCodeDescription.split(",")[0];
        val description = categoryCodeDescription.split(",")[1];
        offender.put("categoryCode", code);
        offender.put("category", description);
        custodyApiWireMock.stubFor(
                get(urlMatching("/elite2api/api/bookings/offenderNo/.*"))
                        .willReturn(ok().withBody(JsonHelper.stringify(offender))));

        return this;
    }
}
