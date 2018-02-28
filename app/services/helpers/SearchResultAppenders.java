package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import helpers.JwtHelper;
import lombok.val;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;
import static services.helpers.SearchQueryBuilder.termsThatLookLikeDates;

public class SearchResultAppenders {

    public static ObjectNode appendOneTimeNomisRef(String bearerToken, ObjectNode rootNode, Function<String, String> encrypter) {
        Optional<JsonNode> nomsNode =
            Optional.ofNullable(rootNode.get("otherIds")).flatMap(otherIds -> Optional.ofNullable(otherIds.get("nomsNumber")));

        if (nomsNode.isPresent()) {
            return rootNode.put("oneTimeNomisRef", oneTimeNomisRef(bearerToken, nomsNode.get().asText(), encrypter));
        }

        return rootNode;
    }

    public static ObjectNode appendOffendersAge(ObjectNode rootNode) {
        val dateOfBirth = dateOfBirth(rootNode);

        return Optional.ofNullable(dateOfBirth)
            .map(dob -> rootNode.put("age", calculateAge(dob, systemUTC())))
            .orElse(rootNode);
    }

    public static String dateOfBirth(ObjectNode rootNode) {
        val dateOfBirth = rootNode.get("dateOfBirth");
        return Optional.ofNullable(dateOfBirth).map(JsonNode::asText).orElse(null);
    }

    public static ObjectNode appendHighlightFields(ObjectNode rootNode, String searchTerm, Map<String, HighlightField> highlightFields) {
        val highlightNode = JsonNodeFactory.instance.objectNode();
        highlightFields.forEach((key, value) -> {
            val arrayNode = JsonNodeFactory.instance.arrayNode();
            Stream.of(value.fragments()).forEach(text -> arrayNode.add(text.string()));
            highlightNode.set(key, arrayNode);
        });

        if (shouldHighlightDateOfBirth(rootNode, searchTerm)) {
            val arrayNode = JsonNodeFactory.instance.arrayNode();
            arrayNode.add(dateOfBirth(rootNode));
            highlightNode.set("dateOfBirth", arrayNode);
        }

        rootNode.set("highlight", highlightNode);
        return rootNode;
    }

    /*
      Creates a limited-time reference to the nomisId number as a string that can only be used by the same user within
      a limited time frame. This allows safe access to a NomisId only by a User that has already had Offender
      canAccess() checked
    */
    private static String oneTimeNomisRef(String bearerToken, String nomisId, Function<String, String> encrypter) {

        val reference = ImmutableMap.of(
            "user", JwtHelper.principal(bearerToken),
            "noms", nomisId,
            "tick", Instant.now().toEpochMilli()
        );

        return encrypter.apply(JsonHelper.stringify(reference));
    }

    private static boolean shouldHighlightDateOfBirth(ObjectNode rootNode, String searchTerm) {
        val dateOfBirth = dateOfBirth(rootNode);

        return Optional.ofNullable(dateOfBirth).map(dob -> doAnySearchTermsMatchDateOfBirth(searchTerm, dob)).orElse(false);
    }

    private static boolean doAnySearchTermsMatchDateOfBirth(String searchTerm, String dateOfBirth) {
        return termsThatLookLikeDates(searchTerm).stream().anyMatch(currentDate -> currentDate.equals(dateOfBirth));
    }

}
