package interfaces;

import data.offendersearch.OffenderSearchResult;

import java.util.concurrent.CompletableFuture;

public interface Search {
    CompletableFuture<OffenderSearchResult> search(String searchTerm);
}
