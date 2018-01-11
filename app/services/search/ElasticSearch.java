package services.search;

import data.offendersearch.OffenderSearchResult;
import helpers.JsonHelper;
import interfaces.Search;
import lombok.val;
import play.Environment;
import scala.io.Source;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

public class ElasticSearch implements Search {

    private final Environment environment;

    @Inject
    public ElasticSearch(Environment environment) {
        this.environment = environment;
    }

    @Override
    public CompletableFuture<OffenderSearchResult> search(String searchTerm) {
            val offenderSearchResults = Source.fromInputStream(environment.resourceAsStream("offender-search-results.json"), "UTF-8").mkString();
            val elasticSearchResults = JsonHelper.readValue(offenderSearchResults, ElasticSearchResults.class);

            val offenderSummaries =
                elasticSearchResults.getHits().stream()
                    .map(ElasticSearchResult::toOffenderSummary)
                    .collect(toList());

            val offenderSearchResult = new OffenderSearchResult();
            offenderSearchResult.setOffenders(offenderSummaries);
            return CompletableFuture.supplyAsync(() -> offenderSearchResult);
    }

}
