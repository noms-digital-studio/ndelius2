package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import helpers.JwtHelper;
import lombok.val;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import static helpers.DateTimeHelper.calculateAge;
import static java.time.Clock.systemUTC;

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
}
