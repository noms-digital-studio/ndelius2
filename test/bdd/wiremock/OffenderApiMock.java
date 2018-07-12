package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class OffenderApiMock {
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    public OffenderApiMock start() {
        offenderApiWireMock.start();
        return this;
    }

    public OffenderApiMock stop() {
        offenderApiWireMock.stop();
        return this;
    }

    public OffenderApiMock stubDefaults() {
        offenderApiWireMock.stubFor(
                post(urlEqualTo("/documentLink")).willReturn(created()));
        return this;
    }

}
