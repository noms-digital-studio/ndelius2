package services;

import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.ByteStreams;
import helpers.JsonHelper;
import interfaces.PrisonerApi;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.Application;
import play.Environment;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scala.io.Source.fromInputStream;

public class NomisCustodyApiIntegrationTest  extends WithApplication {

    private PrisonerApi prisonerApi;
    private static final int PORT = 18080;

    @Rule
    public WireMockRule wireMock = new WireMockRule(wireMockConfig().port(PORT).jettyStopTimeout(10000L));

    @Before
    public void beforeEach() {

        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/offender_G8020GG.json"))));

        wireMock.stubFor(
                post(urlEqualTo("/auth/oauth/token"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/token.json"))));

        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*/images"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/images_123.json"))));

        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*/images/.*/thumbnail"))
                        .willReturn(
                                ok().withHeader(CONTENT_TYPE, "image/jpeg").withBody(loadResourceBytes("/nomsoffender/image_3.jpeg"))));



        prisonerApi = instanceOf(PrisonerApi.class);
    }

    @Test
    public void credentialsSuppliedToRetrieveToken() {
        prisonerApi.getImage("123").toCompletableFuture().join();

        wireMock.verify(
                1,
                postRequestedFor(urlEqualTo("/auth/oauth/token"))
                        .withRequestBody(containing("grant_type=client_credentials"))
                        .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                        .withBasicAuth(new BasicCredentials("my_username", "my_password")));
    }

    @Test
    public void retrievesNewTokenPriorToRetrievingImageWhichIsThenCached() {
        prisonerApi.getImage("123").toCompletableFuture().join();
        prisonerApi.getImage("123").toCompletableFuture().join();
        prisonerApi.getImage("123").toCompletableFuture().join();

        wireMock.verify(
                1,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));

        wireMock.verify(
                3,
                getRequestedFor(urlMatching("/custodyapi/api/offenders/nomsId/.*/images/.*/thumbnail")));

    }

    @Test
    public void retrievesNewTokenAfterCachedExpires() throws InterruptedException {
        prisonerApi.getImage("123").toCompletableFuture().join();
        Thread.sleep(1000);
        prisonerApi.getImage("123").toCompletableFuture().join();

        wireMock.verify(
                2,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));

        wireMock.verify(
                2,
                getRequestedFor(urlMatching("/custodyapi/api/offenders/nomsId/.*/images/.*/thumbnail")));

    }

    @Test
    public void usesLatestFrontFacingThumbnail() {
        prisonerApi.getImage("123").toCompletableFuture().join();

        // 3 if the latest front facing image id
        wireMock.verify( getRequestedFor(urlMatching("/custodyapi/api/offenders/nomsId/123/images/3/thumbnail")));
    }

    @Test
    public void returnsBytesFromMatchingImage() {
        val imageBytes = prisonerApi.getImage("123").toCompletableFuture().join();

        assertThat(imageBytes).isEqualTo(loadResourceBytes("/nomsoffender/image_3.jpeg"));

    }

    @Test
    public void completesExceptionallyIfNotActiveThumbnailFound() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/images_no_current_thumbnail.json"))));

        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("No current image found for offender");

    }

    @Test
    public void completesExceptionallyIfOffenderNotFoundInNomis() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                aResponse().withStatus(404)));

        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("No offender found in NOMIS - check the noms number 123 is correct");

    }

    @Test
    public void completesExceptionallyIfOffenderImageRowNotFoundInNomis() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/.*/images/.*/thumbnail"))
                        .willReturn(
                                aResponse().withStatus(404)));

        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("No images found for offender 123");

    }

    @Test
    public void completesExceptionallyIfFailedToRetrieveToken() {
        wireMock.stubFor(
                post(urlEqualTo("/auth/oauth/token"))
                        .willReturn(
                                aResponse().withStatus(401)));

        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("Unable to call http://localhost:18080/auth/oauth/token Status = 401");

    }

    @Test
    public void unauthorisedResponseCompletesExceptionally() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                aResponse().withStatus(401)));


        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("NOMIS authentication token has expired or is invalid");
    }

    @Test
    public void forbiddenResponseCompletesExceptionally() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                aResponse().withStatus(403)));


        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("NOMIS authentication token has expired or is invalid");
    }

    @Test
    public void unauthorisedResponseClearsCachedToken() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                aResponse().withStatus(401)));


        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join()).hasCauseInstanceOf(RuntimeException.class);

        wireMock.verify(
                3,
                postRequestedFor(urlEqualTo("/auth/oauth/token")));
    }

    @Test
    public void completesExceptionallyIfOffenderServiceErrors() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/123/images"))
                        .willReturn(
                                aResponse().withStatus(500)));

        assertThatThrownBy(() -> prisonerApi.getImage("123").toCompletableFuture().join())
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("Failed to retrieve offender record from NOMIS. Status code 500");

    }


    @Test
    public void tokenFromAuthCallUsedInImageCalls() {
        val tokenFromFakeAuthResponse = JsonHelper.jsonToMap(loadResource("/nomsoffender/token.json")).get("access_token");

        prisonerApi.getImage("123").toCompletableFuture().join();

        wireMock.verify( getRequestedFor(urlMatching("/custodyapi/api/offenders/nomsId/.*/images")).withHeader("Authorization", equalTo(String.format("Bearer %s", tokenFromFakeAuthResponse))));
        wireMock.verify( getRequestedFor(urlMatching("/custodyapi/api/offenders/nomsId/.*/images/.*/thumbnail")).withHeader("Authorization", equalTo(String.format("Bearer %s", tokenFromFakeAuthResponse))));

    }

    @Test
    public void tokenIsRetrievePriorToRetrievingAnOffender() {
        prisonerApi.getOffenderByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                postRequestedFor(urlEqualTo("/auth/oauth/token")));

    }

    @Test
    public void nomsNumberIsUsedWhenRetrievingAnOffender() {
        prisonerApi.getOffenderByNomsNumber("G8020GG").toCompletableFuture().join();

        wireMock.verify(
                getRequestedFor(urlEqualTo("/custodyapi/api/offenders/nomsId/G8020GG")));

    }

    @Test
    public void prisonerOffenderIsReturnedWhenFound() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/G8020GG"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/offender_G8020GG.json"))));



        val maybeOffender = prisonerApi.getOffenderByNomsNumber("G8020GG").toCompletableFuture().join();

        assertThat(maybeOffender.isPresent()).isTrue();
    }

    @Test
    public void prisonerOffenderIsNotReturnedWhenNotFound() {
        wireMock.stubFor(
                get(urlMatching("/custodyapi/api/offenders/nomsId/G8020GG"))
                        .willReturn(
                                notFound()));



        val maybeOffender = prisonerApi.getOffenderByNomsNumber("G8020GG").toCompletableFuture().join();

        assertThat(maybeOffender.isPresent()).isFalse();
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("nomis.api.url", String.format("http://localhost:%d/", PORT))
                .configure("prisoner.api.provider", "custody")
                .configure("custody.api.auth.username", "my_username")
                .configure("custody.api.auth.password", "my_password")
                .configure("custody.api.token.cache.time.seconds", "1")
                .build();
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

}