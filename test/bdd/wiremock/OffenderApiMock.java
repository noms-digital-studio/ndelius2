package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import helpers.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

public class OffenderApiMock {
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    public OffenderApiMock start() {
        offenderApiWireMock.start();
        return this;
    }

    public OffenderApiMock stop() {
        offenderApiWireMock.resetAll();
        return this;
    }

    public OffenderApiMock stubDefaults() {
        offenderApiWireMock.stubFor(
                post(urlEqualTo("/documentLink")).willReturn(created()));

        offenderApiWireMock.stubFor(
                post(urlEqualTo("/logon")).willReturn(ok().withBody("aBearerToken")));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        return this;
    }

}
