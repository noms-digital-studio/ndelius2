package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.typesafe.config.ConfigFactory;
import data.DefendantMatchConfidence;
import data.CourtDefendant;
import helpers.JwtHelperTest;
import interfaces.OffenderApi;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import play.Environment;
import play.Mode;
import play.libs.Json;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static scala.io.Source.fromInputStream;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearch_findMatch_IntegrationTest {

    private static final int PORT = 18080;

    private ElasticOffenderSearch elasticOffenderSearch;

    @Rule
    public WireMockRule wireMock = new WireMockRule(wireMockConfig().port(PORT).jettyStopTimeout(10000L));

    @Mock
    private OffenderApi offenderApi;


    @Before
    public void setup() {
        val config = ConfigFactory.load();
        elasticOffenderSearch = new ElasticOffenderSearch(config, new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", PORT, "http"))), offenderApi);
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.any())).thenReturn(CompletableFuture.completedFuture(Json.parse("[]")));
    }

    @Test
    public void pncPresentWillSearchByPNCAndSurnameOnlyWhenMatched() {

        wireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));

        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search")));

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("2018/123456x")));

    }


    @Test
    public void pncPresentWillSearchByPNCAndSurnameFollowedByNameWithDateOfBirthWhenNotMatched() {

        wireMock.stubFor(
            get(anyUrl())
                    .inScenario("Search")
                    .whenScenarioStateIs(STARTED)
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/noMatches.json")))
                    .willSetStateTo("Name search"));


        wireMock.stubFor(
            get(anyUrl())
                    .inScenario("Search")
                    .whenScenarioStateIs("Name search")
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));

        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "SMITH", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                2,
                getRequestedFor(urlPathEqualTo("/offender/_search")));


        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("2018/123456x"))
                        .withRequestBody(containing("SMITH")));

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("JOHN"))
                        .withRequestBody(containing("1978-01-06"))
                        .withRequestBody(containing("SMITH")));

    }

    @Test
    public void pncPresentWillSearchByPNCAndSurnameFollowedByNameWithDateOfBirthFollowedByDateOfBirthVariationsWhenNotMatched() {

        wireMock.stubFor(
            get(anyUrl())
                    .inScenario("Search")
                    .whenScenarioStateIs(STARTED)
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/noMatches.json")))
                    .willSetStateTo("Name search"));


        wireMock.stubFor(
            get(anyUrl())
                    .inScenario("Search")
                    .whenScenarioStateIs("Name search")
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/noMatches.json")))
                    .willSetStateTo("Date of Birth variations search"));

        wireMock.stubFor(
                get(anyUrl())
                        .inScenario("Search")
                        .whenScenarioStateIs("Date of Birth variations search")
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));


        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "SMITH", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                3,
                getRequestedFor(urlPathEqualTo("/offender/_search")));


        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("2018/123456x"))
                        .withRequestBody(containing("SMITH")));

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("JOHN"))
                        .withRequestBody(containing("1978-01-06"))
                        .withRequestBody(containing("SMITH")));

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search"))
                        .withRequestBody(containing("JOHN"))
                        .withRequestBody(containing("1978-06-01"))
                        .withRequestBody(containing("SMITH")));

    }

    @Test
    public void willDoThreeSearchesWhenPNCPresentButNoMatches() {

        wireMock.stubFor(
            get(anyUrl())
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/noMatches.json"))));



        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "SMITH", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                3,
                getRequestedFor(urlPathEqualTo("/offender/_search")));


    }

    @Test
    public void willDoTwoSearchesWhenNoPNCPresentAndNoMatches() {

        wireMock.stubFor(
            get(anyUrl())
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/noMatches.json"))));



        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "SMITH", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                2,
                getRequestedFor(urlPathEqualTo("/offender/_search")));


    }

    @Test
    public void convictionsRetrievedWhenDuplicatesFound() {

        wireMock.stubFor(
            get(anyUrl())
                    .willReturn(
                        okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));



        elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "SMITH", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        wireMock.verify(
                1,
                getRequestedFor(urlPathEqualTo("/offender/_search")));

        verify(offenderApi).getOffenderConvictionsByOffenderId(anyString(), eq("1"));
        verify(offenderApi).getOffenderConvictionsByOffenderId(anyString(), eq("2"));

    }

    @Test
    public void singlePNCMatchHasVeryHighConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));

        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.VERY_HIGH);

    }

    @Test
    public void singleNameMatchHasHighConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));

        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.HIGH);

    }

    @Test
    public void singleDateOfBirthVariationsWithNameMatchHasMediumConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .inScenario("Search")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/noMatches.json")))
                        .willSetStateTo("Date of Birth variations search"));

        wireMock.stubFor(
                get(anyUrl())
                        .inScenario("Search")
                        .whenScenarioStateIs("Date of Birth variations search")
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/singleMatch.json"))));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.MEDIUM);

    }

    @Test
    public void multiplePNCMatchHasVeryHighConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.VERY_HIGH);
        assertThat(result.getDuplicates()).isNotNull();

    }

    @Test
    public void multipleNameMatchHasHighConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.HIGH);
        assertThat(result.getDuplicates()).isNotNull();

    }

    @Test
    public void multipleNameWithDateOfBirthVariationMatchHasLowConfidence() {
        wireMock.stubFor(
                get(anyUrl())
                        .inScenario("Search")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/noMatches.json")))
                        .willSetStateTo("Date of Birth variations search"));

        wireMock.stubFor(
                get(anyUrl())
                        .inScenario("Search")
                        .whenScenarioStateIs("Date of Birth variations search")
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));



        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.LOW);
        assertThat(result.getDuplicates()).isNotNull();

    }

    @Test
    public void multiplePNCMatchWillResolveToSingleMatchWhenOnlyOneOfDuplicatesHasMoreThanOneEvent() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("1"))).thenReturn(CompletableFuture.completedFuture(Json.parse("[]")));
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("2"))).thenReturn(CompletableFuture.completedFuture(Json.parse(response("/deliusoffender/offenderConvictions.json"))));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.HIGH);
        assertThat(result.getMatch()).isNotNull();
        assertThat(result.getMatch().get("offenderId").asText()).isEqualTo("2");

    }

    @Test
    public void multiplePNCMatchWillResolveToSingleMatchWhenOnlyOneOfDuplicatesHasASingleActiveEvent() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("1"))).thenReturn(CompletableFuture.completedFuture(singleConviction(false)));
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("2"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.HIGH);
        assertThat(result.getMatch()).isNotNull();
        assertThat(result.getMatch().get("offenderId").asText()).isEqualTo("2");

    }

    @Test
    public void multipleNameMatchWillResolveToSingleMatchWhenOnlyOneOfDuplicatesHasASingleActiveEvent() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("1"))).thenReturn(CompletableFuture.completedFuture(singleConviction(false)));
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("2"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.MEDIUM);
        assertThat(result.getMatch()).isNotNull();
        assertThat(result.getMatch().get("offenderId").asText()).isEqualTo("2");

    }

    @Test
    public void multiplePNCMatchWillNotResolveToSingleMatchWhenBothHaveActiveEvent() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("1"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("2"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant("2018/0123456X", "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.VERY_HIGH);
        assertThat(result.getDuplicates()).isNotNull();

    }

    @Test
    public void multipleNameMatchWillNotResolveToSingleMatchWhenBothHaveActiveEvent() {
        wireMock.stubFor(
                get(anyUrl())
                        .willReturn(
                                okForContentType("application/json", response("/elasticsearchdata/duplicateMatches.json"))));

        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("1"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));
        when(offenderApi.getOffenderConvictionsByOffenderId(Mockito.any(), Mockito.eq("2"))).thenReturn(CompletableFuture.completedFuture(singleConviction(true)));


        val result = elasticOffenderSearch.findMatch(JwtHelperTest.generateToken(), new CourtDefendant(null, "Smith", "JOHN", LocalDate.of(1978, 1, 6))).toCompletableFuture().join();

        assertThat(result.getConfidence()).isEqualTo(DefendantMatchConfidence.HIGH);
        assertThat(result.getDuplicates()).isNotNull();

    }



    private ArrayNode singleConviction(boolean active) {
        val conviction = (ObjectNode)Json.parse(response("/deliusoffender/offenderConviction.json"));
        conviction.set("active", active ? BooleanNode.TRUE :BooleanNode.FALSE);
        val convictions = JsonNodeFactory.instance.arrayNode();
        convictions.add(conviction);
        return convictions;

    }



    private String response(String file) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(file), "UTF-8").mkString();
    }


}