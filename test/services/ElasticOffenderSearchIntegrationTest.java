package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
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

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static scala.io.Source.fromInputStream;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.SHOULD;

@RunWith(MockitoJUnitRunner.class)
public class ElasticOffenderSearchIntegrationTest {

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
        when(offenderApi.probationAreaDescriptions(Mockito.any(), Mockito.any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                "N01", "N01 Area",
                "N02", "N02 Area",
                "N03", "N03 Area",
                "N40", "N40 Area"
        )));
    }

    @Test
    public void probationAreaAggregationsReturnedWithResults() {

        /*
        /elasticsearchdata/multipleMatches.json has aggregation as:
        "buckets": [
                {
                  "key": "N02",
                  "doc_count": 2
                },
                {
                  "key": "N01",
                  "doc_count": 1
                },
                {
                  "key": "N03",
                  "doc_count": 1
                }
              ]
         */
        val response = fromInputStream(new Environment(Mode.TEST).resourceAsStream("/elasticsearchdata/multipleMatches.json"), "UTF-8").mkString();
        wireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    okForContentType("application/json",  response)));

        val result = elasticOffenderSearch.search(JwtHelperTest.generateToken(), emptyList(), "john smith", 10, 0, SHOULD).toCompletableFuture().join();
        val byProbationAreas = byProbationAreasAggregationNodes(result);

        assertThat(byProbationAreas.size()).isEqualTo(3);

        assertThat(byProbationAreas.get(0).get("code").asText()).isEqualTo("N02");
        assertThat(byProbationAreas.get(0).get("description").asText()).isEqualTo("N02 Area");
        assertThat(byProbationAreas.get(0).get("count").asInt()).isEqualTo(2);

        assertThat(byProbationAreas.get(1).get("code").asText()).isEqualTo("N01");
        assertThat(byProbationAreas.get(1).get("description").asText()).isEqualTo("N01 Area");
        assertThat(byProbationAreas.get(1).get("count").asInt()).isEqualTo(1);

        assertThat(byProbationAreas.get(2).get("code").asText()).isEqualTo("N03");
        assertThat(byProbationAreas.get(2).get("description").asText()).isEqualTo("N03 Area");
        assertThat(byProbationAreas.get(2).get("count").asInt()).isEqualTo(1);
    }

    @Test
    public void centralTeamIsFilteredFromProbationAreaAggregations() {

        /*
        /elasticsearchdata/multipleMatches.json has aggregation as:
        "buckets": [
                {
                  "key": "N02",
                  "doc_count": 2
                },
                {
                  "key": "N01",
                  "doc_count": 1
                },
                {
                  "key": "N40",
                  "doc_count": 1
                }
              ]
         */
        val response = fromInputStream(new Environment(Mode.TEST).resourceAsStream("/elasticsearchdata/multipleMatchesWithN40.json"), "UTF-8").mkString();
        wireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    okForContentType("application/json",  response)));

        val result = elasticOffenderSearch.search(JwtHelperTest.generateToken(), emptyList(), "john smith", 10, 0, SHOULD).toCompletableFuture().join();
        val byProbationAreas = byProbationAreasAggregationNodes(result);

        assertThat(byProbationAreas.size()).isEqualTo(2);

        assertThat(byProbationAreas.get(0).get("code").asText()).isEqualTo("N02");
        assertThat(byProbationAreas.get(0).get("description").asText()).isEqualTo("N02 Area");
        assertThat(byProbationAreas.get(0).get("count").asInt()).isEqualTo(2);

        assertThat(byProbationAreas.get(1).get("code").asText()).isEqualTo("N01");
        assertThat(byProbationAreas.get(1).get("description").asText()).isEqualTo("N01 Area");
        assertThat(byProbationAreas.get(1).get("count").asInt()).isEqualTo(1);
    }
    @Test
    public void centralTeamIsNotFilteredFromProbationAreaAggregationsWhenInProfile() {

        /*
        /elasticsearchdata/multipleMatches.json has aggregation as:
        "buckets": [
                {
                  "key": "N02",
                  "doc_count": 2
                },
                {
                  "key": "N01",
                  "doc_count": 1
                },
                {
                  "key": "N40",
                  "doc_count": 1
                }
              ]
         */
        val response = fromInputStream(new Environment(Mode.TEST).resourceAsStream("/elasticsearchdata/multipleMatchesWithN40.json"), "UTF-8").mkString();
        wireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    okForContentType("application/json",  response)));

        val result = elasticOffenderSearch.search(JwtHelperTest.generateTokenWithProbationAreaCodes(Arrays.asList("N01", "N40")), emptyList(), "john smith", 10, 0, SHOULD).toCompletableFuture().join();
        val byProbationAreas = byProbationAreasAggregationNodes(result);

        assertThat(byProbationAreas.size()).isEqualTo(3);

        assertThat(byProbationAreas.get(0).get("code").asText()).isEqualTo("N02");
        assertThat(byProbationAreas.get(0).get("description").asText()).isEqualTo("N02 Area");
        assertThat(byProbationAreas.get(0).get("count").asInt()).isEqualTo(2);

        assertThat(byProbationAreas.get(1).get("code").asText()).isEqualTo("N01");
        assertThat(byProbationAreas.get(1).get("description").asText()).isEqualTo("N01 Area");
        assertThat(byProbationAreas.get(1).get("count").asInt()).isEqualTo(1);

        assertThat(byProbationAreas.get(2).get("code").asText()).isEqualTo("N40");
        assertThat(byProbationAreas.get(2).get("description").asText()).isEqualTo("N40 Area");
        assertThat(byProbationAreas.get(2).get("count").asInt()).isEqualTo(1);
    }

    private ArrayNode byProbationAreasAggregationNodes(Map<String, Object> result) {
        return (ArrayNode) ((Map)result.get("aggregations")).get("byProbationArea");
    }

    @Test
    public void noResultsReturnsEmptyProbationAreaAggregations() {
        val response = fromInputStream(new Environment(Mode.TEST).resourceAsStream("/elasticsearchdata/noMatches.json"), "UTF-8").mkString();
        wireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    okForContentType("application/json",  response)));

        val result = elasticOffenderSearch.search(JwtHelperTest.generateToken(), emptyList(), "john smith", 10, 0, SHOULD).toCompletableFuture().join();
        val byProbationAreas = byProbationAreasAggregationNodes(result);

        assertThat(byProbationAreas.size()).isEqualTo(0);
    }


}