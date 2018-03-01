package services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchResultPipelineTest {

    private ObjectMapper mapper;
    private ObjectNode rootNode;
    private Map<String, Map.Entry<Function<ObjectNode, Optional<JsonNode>>, BiFunction<ObjectNode, JsonNode, ObjectNode>>> pipeline;

    @Before
    public void setup() {
        mapper = Json.mapper();
        rootNode = mapper.createObjectNode();
        pipeline = SearchResultPipeline.create(Function.identity(), "", "", ImmutableMap.of());
    }

    @Test
    public void appendsAgeIfDateOfBirthPresent() {

        rootNode.put("dateOfBirth", "2018-02-28");
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addAgeAndHighlightDob")));
        assertThat(newNode.get("age")).isNotNull();
    }

    @Test
    public void doesNotAppendAgeIfDateOfBirthNotPresent() {
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addAgeAndHighlightDob")));
        assertThat(newNode.get("age")).isNull();
    }

    @Test
    public void appendsOneTimeNomisRefIfNomsNumberPresent() {
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("nomsNumber", "A123");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addOneTimeNomisRef")));
        assertThat(newNode.get("oneTimeNomisRef")).isNotNull();
        System.out.println(newNode.get("oneTimeNomisRef"));
        assertThat(newNode.get("oneTimeNomisRef").asText()
            .startsWith("{\"user\":\"unknown\",\"noms\":\"A123\",\"tick\"")).isTrue();
    }

    @Test
    public void doesNotAppendsOneTimeNomisRefIfnomsNumberNotPresent() {
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("pncNumber", "2018/12345");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addOneTimeNomisRef")));
        assertThat(newNode.get("oneTimeNomisRef")).isNull();
    }

    @Test
    public void addsHighlightNode() {
        HighlightField highlightField = mock(HighlightField.class);
        when(highlightField.fragments()).thenReturn(Text.convertFromStringArray(new String[]{"foo", "bar"}));
        Map<String, HighlightField> highlightFieldMap = new HashMap<>();
        highlightFieldMap.put("someField", highlightField);

        pipeline = SearchResultPipeline.create(Function.identity(), "", "dont care", highlightFieldMap);

        ObjectNode sna = SearchResultPipeline.process(rootNode, ImmutableList.of(pipeline.get("addHighlightFields")));
        assertThat(sna.get("highlight")).isNotNull();
        assertThat(sna.get("dateOfBirth")).isNull();
        assertThat(sna.get("highlight").get("someField")).isNotNull();
        assertThat(sna.get("highlight").get("someField").get(0).asText()).isEqualTo("foo");
        assertThat(sna.get("highlight").get("someField").get(1).asText()).isEqualTo("bar");
    }

    @Test
    public void addsHighlightNodeWithDateOfBirth() {
        HighlightField highlightField = mock(HighlightField.class);
        when(highlightField.fragments()).thenReturn(Text.convertFromStringArray(new String[]{"foo", "bar"}));
        Map<String, HighlightField> highlightFieldMap = new HashMap<>();
        highlightFieldMap.put("someField", highlightField);
        rootNode.put("dateOfBirth", "2018-03-20");

        pipeline = SearchResultPipeline.create(Function.identity(), "", "2018-03-20", highlightFieldMap);

        ObjectNode sna = SearchResultPipeline.process(rootNode, pipeline.values());
        assertThat(sna.get("highlight")).isNotNull();
        assertThat(sna.get("dateOfBirth").asText()).isEqualTo("2018-03-20");
        assertThat(sna.get("highlight").get("someField")).isNotNull();
        assertThat(sna.get("highlight").get("someField").get(0).asText()).isEqualTo("foo");
        assertThat(sna.get("highlight").get("someField").get(1).asText()).isEqualTo("bar");
    }
}