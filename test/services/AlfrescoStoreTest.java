package services;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.test.WSTestClient;

import java.util.AbstractMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static play.libs.Json.toJson;

public class AlfrescoStoreTest {

    private static final int PORT = 18080;

    private AlfrescoStore alfrescoStore;

    @Rule
    public WireMockRule wireMock = new WireMockRule(PORT);

    @Before
    public void setup() {
        val config = ConfigFactory.load()
            .withValue("store.alfresco.url",
                        ConfigValueFactory.fromAnyRef(String.format("http://localhost:%d/alfresco/service/", PORT)))
            .withValue("offender.api.url",
                        ConfigValueFactory.fromAnyRef(String.format("http://localhost:%d/", PORT)));
        alfrescoStore = new AlfrescoStore(config, WSTestClient.newClient(PORT));
    }

    @Test
    public void retrieveOriginalDataGetsUserDataFromAlfrescoAPI() {
        val response = toJson(
            ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636",
                            "userData", "some user data")).toString();
        wireMock.stubFor(
            get(urlEqualTo("/alfresco/service/noms-spg/details/309db0bf-f8bb-4ac0-b325-5dbc368e2636"))
                .willReturn(
                    okForContentType("application/json",  response)));

        val result = alfrescoStore.retrieveOriginalData("309db0bf-f8bb-4ac0-b325-5dbc368e2636",
                                                           "aUsername").toCompletableFuture().join();
        assertThat(result).isEqualTo("some user data");
    }

    @Test
    public void uploadNewPdfSendsDataToAlfrescoAPI() {
        val response = toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString();

        wireMock.stubFor(
            post(urlEqualTo("/alfresco/service/noms-spg/uploadnew"))
                .willReturn(okForContentType("application/json",  response)));

        wireMock.stubFor(
            post(urlEqualTo("/documentLink")).willReturn(created()));

        val document = Byte.parseByte("1");
        Byte[] documentBytes = { document };
        val result = alfrescoStore.uploadNewPdf(documentBytes, "filename.pdf",
            "johny userman",  "user data", "crn123", 12345L)
                         .toCompletableFuture().join();
        val entry = new AbstractMap.SimpleEntry<String, String>("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636");
        assertThat(result).containsExactly(entry);

        verify(postRequestedFor(urlEqualTo("/alfresco/service/noms-spg/uploadnew"))
            .withRequestBody(containing("crn123"))
            .withRequestBody(containing("johny userman"))
            .withRequestBody(containing("COURTREPORT"))
            .withRequestBody(containing("12345"))
            .withRequestBody(containing("DOCUMENT"))
            .withRequestBody(containing("user data"))
            .withRequestBody(containing("filename.pdf"))
        );

        verify(postRequestedFor(urlEqualTo("/documentLink"))
            .withRequestBody(equalToJson(toJson(ImmutableMap.builder()
                .put("alfrescoId", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")
                .put("alfrescoUser", "johny userman")
                .put("entityId", "12345")
                .put("documentName", "filename.pdf")
                .put("probationAreaCode", "alfrescoUser")
                .put("crn", "crn123")
                .put("tableName", "COURT_REPORT")
                .build()
            ).toString())));
    }

    @Test
    public void updateExistingPdfSendsDataToAlfrescoAPI() {
        val response = toJson(ImmutableMap.of("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")).toString();

        wireMock.stubFor(
            post(urlEqualTo("/alfresco/service/noms-spg/uploadandrelease/309db0bf-f8bb-4ac0-b325-5dbc368e2636"))
                .willReturn(
                    okForContentType("application/json",  response)));

        wireMock.stubFor(
            post(urlEqualTo("/alfresco/service/noms-spg/updatemetadata/309db0bf-f8bb-4ac0-b325-5dbc368e2636"))
                .willReturn(
                    okForContentType("application/json",  response)));

        val document = Byte.parseByte("1");
        Byte[] documentBytes = { document };

        val result = alfrescoStore.updateExistingPdf(documentBytes, "filename.pdf",
            "johny userman",  "user data", "309db0bf-f8bb-4ac0-b325-5dbc368e2636")
                         .toCompletableFuture().join();
        val entry = new AbstractMap.SimpleEntry<String, String>("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636");
        assertThat(result).containsExactly(entry);

        verify(postRequestedFor(urlEqualTo("/alfresco/service/noms-spg/uploadandrelease/309db0bf-f8bb-4ac0-b325-5dbc368e2636"))
            .withRequestBody(containing("johny userman"))
            .withRequestBody(containing("filename.pdf")));

        verify(postRequestedFor(urlEqualTo("/alfresco/service/noms-spg/updatemetadata/309db0bf-f8bb-4ac0-b325-5dbc368e2636"))
            .withRequestBody(containing("user data")));

    }

}