package services.helpers;

import lombok.val;
import org.elasticsearch.search.SearchHit;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import static play.libs.Json.parse;

public class SearchHitComparison {

    public static Comparator<SearchHit> searchHitGrouper = (o1, o2) -> comparisonTerm(o2).compareTo(comparisonTerm(o1));

    private static String comparisonTerm(SearchHit searchHit) {

        val node = parse(searchHit.getSourceAsString());

        final Function<String, String> get = key -> Optional.ofNullable(node.get(key)).map(value -> value.asText().toLowerCase()).orElse("");

        return get.apply("currentDisposal") + get.apply("surname");
    }
}
