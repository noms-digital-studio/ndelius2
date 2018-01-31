package interfaces;

import data.offendersearch.OffenderSearchResult;

import java.util.concurrent.CompletionStage;

public interface OffenderSearch {
    CompletionStage<OffenderSearchResult> search(String bearerToken, String searchTerm, int pageSize, int pageNumber);

    CompletionStage<Boolean> isHealthy();
}
