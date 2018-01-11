package interfaces;

import data.offendersearch.OffenderSearchResult;

public interface Search {
    OffenderSearchResult search(String searchTerm);
}
