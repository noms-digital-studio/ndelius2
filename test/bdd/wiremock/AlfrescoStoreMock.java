package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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

    public boolean verifySavedDocumentContainsValues(Map<String, String> values) {
        val match = postRequestedFor(urlMatching("/noms-spg/updatemetadata/.*"));
        // build matcher adding each tuple as JSON body match
        values.forEach((key, text) -> match.withRequestBody(matching(String.format(".*\"%s\":.*%s.*.*", key, text))));
        val hasMatched = alfrescofWireMock.findAll(match).size() > 0;
        if (!hasMatched) {
            System.out.println(alfrescofWireMock.findAllNearMissesFor(match));
        }
        return hasMatched;
    }

    public AlfrescoStoreMock stubExistingDocument(String documentId, String document) {
        alfrescofWireMock.stubFor(
                get(urlEqualTo(String.format("/noms-spg/details/%s", documentId)))
                        .willReturn(
                                okForContentType("application/json",  document)));
        return this;
    }
}
