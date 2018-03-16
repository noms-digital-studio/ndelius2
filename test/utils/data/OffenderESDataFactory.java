package utils.data;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import play.Environment;
import play.Mode;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static scala.io.Source.fromInputStream;

public class OffenderESDataFactory {
    private OffenderESDataFactory() {
        // factory not used
    }
    public static SearchHit[] getSearchHitArray() {
        return getSearchHitArray(ImmutableMap.of("offenderId", 123, "crn", "X1224", "currentRestriction", false, "currentExclusion", false));
    }


    @SafeVarargs
    public static SearchHit[] getSearchHitArrayWithHighlights(Map<String, HighlightField> highlightFields, Map<String, Object>... replacements) {
        return stream(replacements).map((replacement) -> toSearchHit(highlightFields, replacement))
                .collect(toList()).toArray(new SearchHit[replacements.length]);
    }

    @SafeVarargs
    public static SearchHit[] getSearchHitArray(Map<String, Object>... replacements) {
        return stream(replacements).map((replacement) -> toSearchHit(ImmutableMap.of(), replacement))
                .collect(toList()).toArray(new SearchHit[replacements.length]);
    }

    private static SearchHit toSearchHit(Map<String, HighlightField> highlightFields, Map<String, Object> replacementMap) {
        val searchHitMap = new HashMap<String, Object>();
        val environment = new Environment(Mode.TEST);

        val offenderSearchResultsTemplate =
                fromInputStream(environment.resourceAsStream("offender-search-result.json.template"), "UTF-8").mkString();

        val offenderSearchResults =
                withDefaults(replacementMap).
                        keySet().
                        stream().
                        reduce(offenderSearchResultsTemplate,
                                (template, key) -> template.replace(format("${%s}", key), withDefaults(replacementMap).get(key).toString()));


        val bytesReference = new BytesArray(offenderSearchResults);
        searchHitMap.put("_source", bytesReference);
        searchHitMap.put("highlight", highlightFields);
        return SearchHit.createFromMap(searchHitMap);
    }

    private static Map<String, Object> withDefaults(Map<String, Object> replacementMap) {
        val replacementsBuilder = ImmutableMap.<String, Object>builder().putAll(replacementMap);
        if (!replacementMap.containsKey("dateOfBirth")) {
            replacementsBuilder.put("dateOfBirth", "1978-01-16");
        }

        if (!replacementMap.containsKey("firstName")) {
            replacementsBuilder.put("firstName", "firstName");
        }

        if (!replacementMap.containsKey("surname")) {
            replacementsBuilder.put("surname", "surname");
        }

        if (!replacementMap.containsKey("currentDisposal")) {
            replacementsBuilder.put("currentDisposal", "0");
        }

        if (!replacementMap.containsKey("offenderId")) {
            replacementsBuilder.put("offenderId", 123);
        }

        if (!replacementMap.containsKey("crn")) {
            replacementsBuilder.put("crn", "X1224");
        }

        if (!replacementMap.containsKey("currentRestriction")) {
            replacementsBuilder.put("currentRestriction", false);
        }

        if (!replacementMap.containsKey("currentExclusion")) {
            replacementsBuilder.put("currentExclusion", false);
        }

        return replacementsBuilder.build();
    }

}
