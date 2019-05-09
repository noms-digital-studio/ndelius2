package data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

@AllArgsConstructor
public class MatchedDefendants {
    private DefendantMatchConfidence confidence;
    private List<MatchedDefendant> defendants;

    public static MatchedDefendants of(MatchedOffenders matchedOffenders) {
        val offenders = Optional.ofNullable(matchedOffenders.getMatch())
                .map(offenderNode -> ImmutableList.of(toMatchedDefendant(offenderNode)))
                .orElseGet(() -> Optional.ofNullable(matchedOffenders.getDuplicates())
                        .map(offenderNodes -> offenderNodes
                                .stream()
                                .map(MatchedDefendants::toMatchedDefendant)
                                .collect(collectingAndThen(toList(), ImmutableList::copyOf)))
                        .orElse(ImmutableList.of()));

        return new MatchedDefendants(matchedOffenders.getConfidence(), offenders);
    }

    private static MatchedDefendant toMatchedDefendant(ObjectNode offenderNode) {
        if (offenderNode.hasNonNull("accessDenied") && offenderNode.get("accessDenied").asBoolean()) {
            return MatchedDefendant
                    .builder()
                    .crn(offenderNode.get("otherIds").get("crn").asText())
                    .surname("*** restricted ***")
                    .firstName("*** restricted ***")
                    .build();

        }
        return MatchedDefendant
                .builder()
                .crn(offenderNode.get("otherIds").get("crn").asText())
                .pncNumber(offenderNode.get("otherIds").hasNonNull("pncNumber")
                        ? offenderNode.get("otherIds").get("pncNumber").asText()
                        : "")
                .firstName(offenderNode.get("firstName").asText())
                .surname(offenderNode.get("surname").asText())
                .dateOfBirth(offenderNode.get("dateOfBirth").asText())
                .address(mainAddress(offenderNode).orElse("No main address"))
                .build();
    }

    private static Optional<String> mainAddress(ObjectNode offenderNode) {
        if (offenderNode.hasNonNull("contactDetails")) {
            if (offenderNode.get("contactDetails").hasNonNull("addresses")) {
                val addresses = (ArrayNode)offenderNode.get("contactDetails").get("addresses");
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(addresses.elements(), Spliterator.ORDERED),false)
                        .filter(address -> {
                            if (address.hasNonNull("status")) {
                                return address.get("status").hasNonNull("code") &&
                                        address.get("status").get("code").asText().equals("M");
                            }
                            return true;
                        })
                        .findAny()
                        .map(node -> ImmutableList
                                .<String>builder()
                                .add(Optional.ofNullable(node.get("buildingName")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("addressNumber")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("streetName")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("district")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("town")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("county")).map(JsonNode::asText).orElse(""))
                                .add(Optional.ofNullable(node.get("postcode")).map(JsonNode::asText).orElse(""))
                                .build()
                                .stream()
                                .filter(StringUtils::isNotBlank)
                                .collect(joining(", "))
                        );
            }
        }
        return Optional.empty();
    }

    public DefendantMatchConfidence getConfidence() {
        return confidence;
    }

    public List<MatchedDefendant> getDefendants() {
        return defendants;
    }
}
