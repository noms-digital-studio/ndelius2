package interfaces;

import data.offendersearch.OffenderSearchResult;

import java.util.concurrent.CompletionStage;

public interface Search {
    CompletionStage<OffenderSearchResult> search(String searchTerm, int pageSize, int pageNumber);

    CompletionStage<Boolean> isHealthy();
}
