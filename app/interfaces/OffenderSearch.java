package interfaces;

import services.helpers.SearchQueryBuilder.QUERY_TYPE;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface OffenderSearch {

    CompletionStage<Map<String, Object>> search(String bearerToken, List<String> probationAreasFilter, String searchTerm, int pageSize, int pageNumber, QUERY_TYPE queryType);

    CompletionStage<Boolean> isHealthy();
}
