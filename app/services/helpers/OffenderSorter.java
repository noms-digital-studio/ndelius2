package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class OffenderSorter {

    public static List<ObjectNode> groupByNameAndSortByCurrentDisposal(List<ObjectNode> offenders) {

        val groups = offenders.stream().collect(Collectors.groupingBy(
                OffenderSorter::nameClassifier,
                LinkedHashMap::new,
                collectingAndThen(toList(), OffenderSorter::currentDisposalSorter)
        ));

        return groups.values().stream().flatMap(List::stream).collect(toList());
    }

    private static String nameClassifier(ObjectNode node) {

        return (nodeField(node, "firstName") + nodeField(node, "surname")).toLowerCase();
    }

    private static List<ObjectNode> currentDisposalSorter(List<ObjectNode> nodes) {

        nodes.sort((o1, o2) -> nodeField(o2, "currentDisposal").compareTo(nodeField(o1, "currentDisposal")));
        return nodes;
    }

    private static String nodeField(ObjectNode node, String field) {

        return Optional.ofNullable(node.get(field)).map(JsonNode::asText).orElse("");
    }
}
