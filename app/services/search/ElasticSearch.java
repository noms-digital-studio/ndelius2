package services.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import data.offendersearch.OffenderSearchResult;
import helpers.FutureListener;
import interfaces.Search;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static play.libs.Json.parse;

public class ElasticSearch implements Search {

    private final RestHighLevelClient elasticSearchClient;

    @Inject
    public ElasticSearch(RestHighLevelClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    @Override
    public CompletionStage<OffenderSearchResult> search(String searchTerm, int pageSize, int pageNumber) {

        val listener = new FutureListener<SearchResponse>();
        val searchSource = new SearchSourceBuilder().query(multiMatchQuery(searchTerm, "surname", "firstName", "gender"));
        searchSource.size(pageSize);
        searchSource.from(pageSize * aValidPageNumberFor(pageNumber));

        elasticSearchClient.searchAsync(new SearchRequest("offender").source(searchSource), listener);

        return listener.stage().thenApply(response -> {

            val offenderSummaries =
                stream(response.getHits().getHits())
                    .map(documentFields -> {
                        JsonNode node = parse(documentFields.getSourceAsString());
                        return embellishNode(node);
                    }).collect(toList());

            return OffenderSearchResult.builder()
                .offenders(offenderSummaries)
                .total(response.getHits().getTotalHits())
                .build();
        });
    }

    private int aValidPageNumberFor(int pageNumber) {
        return pageNumber >= 1 ? pageNumber - 1 : 0;
    }

    private JsonNode embellishNode(JsonNode node) {
        JsonNode dateOfBirth =  node.get("dateOfBirth");
        if (dateOfBirth != null) {
            return ((ObjectNode) node).put("age", calculateAge(dateOfBirth.asText(), systemUTC()));
        }

        return node;
    }

}
