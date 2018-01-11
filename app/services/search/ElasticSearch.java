package services.search;

import data.offendersearch.OffenderSearchResult;
import interfaces.Search;
import lombok.val;
import play.Environment;
import play.Logger;
import play.libs.Json;
import scala.io.Source;

import javax.inject.Inject;
import java.io.IOException;

import static java.util.stream.Collectors.toList;

public class ElasticSearch implements Search {

    private final Environment environment;

    @Inject
    public ElasticSearch(Environment environment) {
        this.environment = environment;
    }

    @Override
    public OffenderSearchResult search(String searchTerm) {
        try {
            val offenderSearchResults = Source.fromInputStream(environment.resourceAsStream("offender-search-results.json"), "UTF-8").mkString();
            val elasticSearchResults = Json.mapper().readValue(offenderSearchResults, ElasticSearchResults.class);

            val offenderSummaries =
                elasticSearchResults.getHits().stream()
                    .map(ElasticSearchResult::toOffenderSummary)
                    .collect(toList());

            val offenderSearchResult = new OffenderSearchResult();
            offenderSearchResult.setOffenders(offenderSummaries);
            return offenderSearchResult;
        } catch (IOException e) {
            Logger.error("Failed to read offender search results", e);
            throw new RuntimeException(e);
        }
    }

}
