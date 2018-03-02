package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class OffenderSorterTest {

    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = Json.mapper();
    }

    @Test
    public void offenderListWithoutCurrentOffendersRemainsUnchanged() {
        val offenders = ImmutableList.of(
                anOffender(1, "john", "Smith", "0"),
                anOffender(2, "john", "smith", "0"),
                anOffender(3, "John", "SMITH", "0")
        );

        val sortedOffenders = OffenderSorter.groupByNameAndSortByCurrentDisposal(offenders);

        val ids = sortedOffenders.stream()
                .map(node -> node.get("offenderId").asInt())
                .collect(toList());

        assertThat(ids).containsExactly(1, 2, 3);
    }

    @Test
    public void sortsCurrentOffendersToTopInAListOfDuplicateNames() {
        val offenders = ImmutableList.of(
                anOffender(1, "john", "Smith", "0"),
                anOffender(2, "john", "smith", "0"),
                anOffender(3, "John", "SMITH", "1")
        );

        val sortedOffenders = OffenderSorter.groupByNameAndSortByCurrentDisposal(offenders);

        List<Integer> ids = sortedOffenders.stream()
                .map(node -> node.get("offenderId").asInt())
                .collect(toList());

        assertThat(ids).containsExactly(3, 1, 2);
    }

    @Test
    public void groupsSimilarNamesAndSortCurrentOffendersToTopOfThoseGroups() {
        val offenders = ImmutableList.of(
                anOffender(1, "James", "Smith", "0"),
                anOffender(2, "john", "Smith", "0"),
                anOffender(3, "john", "smith", "0"),
                anOffender(4, "bob", "samuels", "1"),
                anOffender(5, "John", "SMITH", "1"),
                anOffender(6, "bob", "samuels", "0")
        );

        val sortedOffenders = OffenderSorter.groupByNameAndSortByCurrentDisposal(offenders);

        val ids = sortedOffenders.stream()
                .map(node -> node.get("offenderId").asInt())
                .collect(toList());

        assertThat(ids).containsExactly(1, 5, 2, 3, 4, 6);
    }

    private ObjectNode anOffender(int id, String firstName, String surname, String currentDisposal) {
        val node = mapper.createObjectNode();
        node.put("offenderId", id);
        node.put("firstName", firstName);
        node.put("surname", surname);
        node.put("currentDisposal", currentDisposal);
        return node;
    }
}
