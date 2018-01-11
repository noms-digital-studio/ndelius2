package services.search;

import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
public class ElasticSearchContactDetails {
    private List<ElasticSearchAddress> addresses = emptyList();
}
