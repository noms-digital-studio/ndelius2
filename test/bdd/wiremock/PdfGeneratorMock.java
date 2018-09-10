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

    private boolean started;


    public PdfGeneratorMock start() {
        if (!started) {
            pdfWireMock.start();
            started = true;
        }
        return this;
    }

    public PdfGeneratorMock stop() {
        pdfWireMock.resetAll();
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
