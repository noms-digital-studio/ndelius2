package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static play.libs.Json.toJson;

public class AlfrescoStoreMock {
    @Inject
    @Named("alfrescofWireMock")
    private WireMockServer alfrescofWireMock;


    public AlfrescoStoreMock start() {
        alfrescofWireMock.start();
        return this;
    }

    public AlfrescoStoreMock stop() {
        alfrescofWireMock.stop();
        return this;
    }

    public AlfrescoStoreMock stubDefaults() {
        alfrescofWireMock.stubFor(
                post(urlEqualTo("/noms-spg/uploadnew"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));

        alfrescofWireMock.stubFor(
                post(urlMatching("/noms-spg/uploadandrelease/.*"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));

        alfrescofWireMock.stubFor(
                post(urlMatching("/noms-spg/updatemetadata/.*"))
                        .willReturn(okForContentType("application/json", toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString())));


        return this;
    }
}
