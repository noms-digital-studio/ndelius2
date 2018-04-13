package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class OffenderSorter {

    public static List<ObjectNode> groupByNameAndSortByCurrentDisposal(List<ObjectNode> offenders) {

        val groups = offenders.stream().collect(Collectors.groupingBy(
                OffenderSorter::nameAndDobClassifier,
                LinkedHashMap::new,
                collectingAndThen(toList(), OffenderSorter::currentDisposalSorter)
        ));

        return groups.values().stream().flatMap(List::stream).collect(toList());
    }

    private static String nameAndDobClassifier(ObjectNode node) {

        return ImmutableList.of("firstName", "surname", "dateOfBirth").
                stream().
                map(field -> nodeField(node, field)).
                collect(joining());
    }

    private static List<ObjectNode> currentDisposalSorter(List<ObjectNode> nodes) {

        nodes.sort((o1, o2) -> nodeField(o2, "currentDisposal").compareTo(nodeField(o1, "currentDisposal")));
        return nodes;
    }

    private static String nodeField(ObjectNode node, String field) {

        return Optional.ofNullable(node.get(field)).map(JsonNode::asText).orElse("").toLowerCase();
    }
}
