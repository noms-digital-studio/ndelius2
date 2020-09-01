package services;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import helpers.JwtHelperTest;
import interfaces.OffenderSearch;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.Application;
import play.Environment;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import services.helpers.SearchQueryBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static scala.io.Source.fromInputStream;

public class ProbationOffenderSearchIntegrationTest  extends WithApplication {
    private static final int SEARCH_API_PORT = 18081;
    private static final int HMPPS_AUTH_PORT = 18082;

    private OffenderSearch probationOffenderSearch;

    @Rule
    public WireMockRule searchApiMock = new WireMockRule(wireMockConfig().port(SEARCH_API_PORT).jettyStopTimeout(10000L));
    @Rule
    public WireMockRule hmppsAuthWireMock = new WireMockRule(wireMockConfig().port(HMPPS_AUTH_PORT).jettyStopTimeout(10000L));

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("probation.offender.search.url", String.format("http://localhost:%d/", SEARCH_API_PORT))
                .configure("nomis.api.url", String.format("http://localhost:%d/", HMPPS_AUTH_PORT))
                .configure("offender.search.provider", "probation-offender-search")
                .build();
    }

    @Before
    public void beforeEach() {
        probationOffenderSearch = instanceOf(OffenderSearch.class);
        hmppsAuthWireMock.stubFor(
                post(urlPathEqualTo("/auth/oauth/token"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/nomsoffender/token.json"))));
    }

    @Test
    public void willRequestATokenForTheLoggedOnUser() {
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithSubject("sandrablacknps"),
                ImmutableList.of(),
                "john smith",
                10,
                0,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        hmppsAuthWireMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/auth/oauth/token")).withQueryParam("username", equalTo("sandrablacknps")));
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }
}
