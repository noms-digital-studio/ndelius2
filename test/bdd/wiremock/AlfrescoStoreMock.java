package bdd.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
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

    /*
        getBodyAsString() call returns a string within which is the nested JSON that we want to assert on.

        e.g.

        --MXimuNKTEgoTtC3OT4
        Content-Disposition: form-data; name="userData"
        Content-Type: text/plain

        {
        "templateName":"paroleParom1Report","values":
            {
                "prisonerDetailsPrisonersFullName":"Jimmy Jammy Fizz",
                "prisonerDetailsOffence":"Sub cat desc (code123) - 08/11/2018",
                ...
            }
        }
        --MXimuNKTEgoTtC3OT4--
     */
    public boolean verifySavedDocumentContainsValues(Map<String, String> values) {
        List<LoggedRequest> requests = alfrescofWireMock.findAll(postRequestedFor(urlMatching("/noms-spg/updatemetadata/.*")));
        // We need to look at the last request that Wiremock received
        val body = requests.get(requests.size()-1).getBodyAsString();
        val jsonFromBody = body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1);
        val documentMetaData = Json.parse(jsonFromBody).get("values");

        Map<String, Boolean> results = values.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> documentMetaData.get(e.getKey()).asText().contains(e.getValue())
            ));

        boolean result = results.values().stream()
            .allMatch(passed -> passed);

        if (!result) {
            results.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .forEach(entry -> System.err.println(String.format("\nField: [%s] \nExpected [%s] \nto contain [%s]",
                    entry.getKey(),
                    documentMetaData.get(entry.getKey()).asText(),
                    values.get(entry.getKey()))));
        }

        return result;
    }

    public AlfrescoStoreMock stubExistingDocument(String documentId, String document) {
        alfrescofWireMock.stubFor(
                get(urlEqualTo(String.format("/noms-spg/details/%s", documentId)))
                        .willReturn(
                                okForContentType("application/json",  document)));
        return this;
    }
}
