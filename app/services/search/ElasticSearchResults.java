package services.search;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ElasticSearchResults {
    private List<ElasticSearchResult> hits = Collections.emptyList();
}

