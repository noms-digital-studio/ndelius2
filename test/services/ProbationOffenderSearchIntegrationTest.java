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
import play.libs.Json;
import play.test.WithApplication;
import services.helpers.SearchQueryBuilder;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static scala.io.Source.fromInputStream;

public class ProbationOffenderSearchIntegrationTest extends WithApplication {
    private static final int SEARCH_API_PORT = 18081;
    private static final int HMPPS_AUTH_PORT = 18082;
    private static final int COMMUNITY_API_PORT = 18083;
    @Rule
    public WireMockRule searchApiMock = new WireMockRule(wireMockConfig().port(SEARCH_API_PORT)
            .jettyStopTimeout(10000L));
    @Rule
    public WireMockRule hmppsAuthWireMock = new WireMockRule(wireMockConfig().port(HMPPS_AUTH_PORT)
            .jettyStopTimeout(10000L));

    @Rule
    public WireMockRule communityApiWireMock = new WireMockRule(wireMockConfig().port(COMMUNITY_API_PORT)
            .jettyStopTimeout(10000L));

    private OffenderSearch probationOffenderSearch;

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("probation.offender.search.url", String.format("http://localhost:%d/", SEARCH_API_PORT))
                .configure("hmpps.auth.url", String.format("http://localhost:%d/", HMPPS_AUTH_PORT))
                .configure("offender.api.url", String.format("http://localhost:%d/api/", COMMUNITY_API_PORT))
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
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/singleResult.json"))));
        communityApiWireMock.stubFor(
                get(urlPathMatching("/api/probationAreas/code/.*"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/probationAreaByCode_Other.json"))));
        communityApiWireMock.stubFor(
                get(urlPathEqualTo("/api/probationAreas/code/N01"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/probationAreaByCode_N01.json"))));
        communityApiWireMock.stubFor(
                get(urlPathEqualTo("/api/probationAreas/code/N02"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/probationAreaByCode_N02.json"))));
    }

    @Test
    public void willRequestATokenForTheLoggedOnUser() {
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                ImmutableList.of(),
                "john smith",
                10,
                0,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        hmppsAuthWireMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/auth/oauth/token"))
                        .withQueryParam("username", equalTo("sandrablacknps")));
    }

    @Test
    public void willCallProbationSearchWithToken() {
        String expectedToken = Json.parse(loadResource("/nomsoffender/token.json")).get("access_token").asText();
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                ImmutableList.of(),
                "john smith",
                10,
                1,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        searchApiMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/phrase"))
                        .withHeader("Authorization", equalTo("Bearer " + expectedToken)));
    }

    @Test
    public void pageNumberIsMappedToZeroIndexedNumber() {
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                ImmutableList.of(),
                "john smith",
                10,
                1,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        searchApiMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/phrase")).withQueryParam("page", equalTo("0")));
    }

    @Test
    public void pageSizeIsPassedAsRequestParameter() {
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                ImmutableList.of(),
                "john smith",
                20,
                1,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        searchApiMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/phrase")).withQueryParam("size", equalTo("20")));
    }

    @Test
    public void phraseAndSearchTypeAndFilterPassedInBody() {
        probationOffenderSearch.search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                ImmutableList.of("N01", "N02"),
                "john smith",
                20,
                1,
                SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        searchApiMock.verify(
                1,
                postRequestedFor(urlPathEqualTo("/phrase")).withRequestBody(equalToJson("" +
                        "{" +
                        " \"phrase\": \"john smith\"," +
                        " \"matchAllTerms\": true," +
                        " \"probationAreasFilter\": [\"N01\", \"N02\"]" +
                        "}"
                )));
    }

    @Test
    public void willPassThroughAllOffenders() {
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));

        Map<String, Object> results = probationOffenderSearch
                .search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                        ImmutableList.of("N01", "N02"),
                        "john smith",
                        10,
                        1,
                        SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> offenders = (List<Map<String, Object>>) results.get("offenders");

        assertThat(offenders.size()).isEqualTo(10);
        assertThat(offenders.get(0).get("offenderId")).isEqualTo(2500195236L);
        assertThat(offenders.get(9).get("offenderId")).isEqualTo(2500196465L);
    }
    @Test
    public void willSetTotalFromTotalElements() {
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));

        Map<String, Object> results = probationOffenderSearch
                .search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                        ImmutableList.of("N01", "N02"),
                        "john smith",
                        10,
                        1,
                        SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        assertThat(results.get("total")).isEqualTo(37);
    }
    @Test
    public void willPassThroughAggregations() {
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));

        Map<String, Object> results = probationOffenderSearch
                .search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                        ImmutableList.of("N01", "N02"),
                        "john smith",
                        10,
                        1,
                        SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        @SuppressWarnings("unchecked")
        Map<String, Object> aggregationsWrapper = (Map<String, Object>)results.get("aggregations");

        assertThat(aggregationsWrapper.get("byProbationArea")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aggregations = (List<Map<String, Object>>) aggregationsWrapper.get("byProbationArea");

        assertThat(aggregations.size()).isEqualTo(7);
        assertThat(aggregations.get(0).get("code")).isEqualTo("N03");
        assertThat(aggregations.get(0).get("count")).isEqualTo(21);
        assertThat(aggregations.get(6).get("code")).isEqualTo("N05");
        assertThat(aggregations.get(6).get("count")).isEqualTo(1);
    }
    @Test
    public void willAddDescriptionsToProbationArea() {
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));

        Map<String, Object> results = probationOffenderSearch
                .search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                        ImmutableList.of("N01", "N02"),
                        "john smith",
                        10,
                        1,
                        SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        @SuppressWarnings("unchecked")
        Map<String, Object> aggregationsWrapper = (Map<String, Object>)results.get("aggregations");

        assertThat(aggregationsWrapper.get("byProbationArea")).isNotNull();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aggregations = (List<Map<String, Object>>) aggregationsWrapper.get("byProbationArea");

        assertThat(aggregations.get(2).get("code")).isEqualTo("N02");
        assertThat(aggregations.get(2).get("description")).isEqualTo("NPS North East");
    }
    @Test
    public void willPassThroughSuggestions() {
        searchApiMock.stubFor(
                post(urlPathEqualTo("/phrase"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/probationoffendersearch/multipleResults.json"))));

        Map<String, Object> results = probationOffenderSearch
                .search(JwtHelperTest.generateTokenWithUsername("sandrablacknps"),
                        ImmutableList.of("N01", "N02"),
                        "john smith",
                        10,
                        1,
                        SearchQueryBuilder.QUERY_TYPE.MUST).toCompletableFuture().join();

        @SuppressWarnings("unchecked")
        Map<String, Object> suggestionsWrapper = (Map<String, Object>) results.get("suggestions");

        assertThat(suggestionsWrapper.get("suggest")).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, Object> suggestions = (Map<String, Object>) suggestionsWrapper.get("suggest");

        assertThat(suggestions.size()).isEqualTo(2);
        assertThat(suggestions.get("firstName")).isNotNull();
        assertThat(suggestions.get("surname")).isNotNull();
    }

}
