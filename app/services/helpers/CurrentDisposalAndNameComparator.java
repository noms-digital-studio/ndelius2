package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import org.elasticsearch.search.SearchHit;

import java.util.Comparator;

import static play.libs.Json.parse;

public class CurrentDisposalAndNameComparator {

    public static Comparator<SearchHit> currentDisposalAndNameComparator = (o1, o2) -> {
        JsonNode offender1 = parse(o1.getSourceAsString());
        JsonNode offender2 = parse(o2.getSourceAsString());
        String s1 =
            safeGet("currentDisposal", offender1) +
            safeGet("surname", offender1);
        String s2 =
            safeGet("currentDisposal", offender2) +
            safeGet("surname", offender2);

        return s2.compareTo(s1);
    };


    private static String safeGet(String key, JsonNode offender1) {
        JsonNode jsonNode = offender1.get(key);
        if (jsonNode != null) {
            return jsonNode.asText().toLowerCase();
        }

        return "";
    }
}
