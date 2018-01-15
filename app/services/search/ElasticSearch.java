package services.search;

import data.offendersearch.OffenderSearchResult;
import helpers.FutureListener;
import helpers.JsonHelper;
import interfaces.Search;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

public class ElasticSearch implements Search {

    private final RestHighLevelClient elasticSearchClient;

    @Inject
    public ElasticSearch(RestHighLevelClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    @Override
    public CompletionStage<OffenderSearchResult> search(String searchTerm) {

        val listener = new FutureListener<SearchResponse>();
        val searchSource = new SearchSourceBuilder().query(multiMatchQuery(searchTerm, "surname", "firstName", "gender"));
        val searchRequest = new SearchRequest("offender").source(searchSource);

        elasticSearchClient.searchAsync(searchRequest, listener);

        return listener.stage().thenApply(response -> {

            val offenderSummaries =
                Arrays.stream(response.getHits().getHits())
                    .map(offenderSearchHit -> JsonHelper.readValue(offenderSearchHit.getSourceAsString(),
                                                                   ElasticSearchResult.class).toOffenderSummary()
                    ).collect(toList());

            val offenderSearchResult = new OffenderSearchResult();
            offenderSearchResult.setOffenders(offenderSummaries);
            return offenderSearchResult;
        });
    }

}
