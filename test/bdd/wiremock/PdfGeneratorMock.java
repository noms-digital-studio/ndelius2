package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import helpers.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class PdfGeneratorMock {
    @Inject
    @Named("pdfWireMock")
    private WireMockServer pdfWireMock;

    public PdfGeneratorMock start() {
            pdfWireMock.start();
        return this;
    }

    public PdfGeneratorMock stop() {
        pdfWireMock.stop();
        return this;
    }

    public PdfGeneratorMock stubDefaults() {
        pdfWireMock.stubFor(
                post(urlEqualTo("/generate"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withBody(JsonHelper.stringify(new Byte[]{}))));
        return this;
    }
}
