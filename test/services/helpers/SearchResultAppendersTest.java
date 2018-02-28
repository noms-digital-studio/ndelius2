package services.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchResultAppendersTest {

    private ObjectMapper mapper;
    private ObjectNode rootNode;

    @Before
    public void setup() {
        mapper = Json.mapper();
        rootNode = mapper.createObjectNode();
    }

    @Test
    public void appendsAgeIfDateOfBirthPresent() {
        rootNode.put("dateOfBirth", "2018-02-28");
        ObjectNode newNode = SearchResultAppenders.appendOffendersAge(rootNode);
        assertThat(newNode.get("age")).isNotNull();
    }

    @Test
    public void doesNotAppendAgeIfDateOfBirthNotPresent() {
        ObjectNode newNode = SearchResultAppenders.appendOffendersAge(rootNode);
        assertThat(newNode.get("age")).isNull();
    }

    @Test
    public void appendsOneTimeNomisRefIfNomsNumberPresent() {
        Function<String, String> encrypter = string -> string;
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("nomsNumber", "A123");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultAppenders.appendOneTimeNomisRef("token", rootNode, encrypter);
        assertThat(newNode.get("oneTimeNomisRef")).isNotNull();
        System.out.println(newNode.get("oneTimeNomisRef"));
        assertThat(newNode.get("oneTimeNomisRef").asText()
            .startsWith("{\"user\":\"unknown\",\"noms\":\"A123\",\"tick\"")).isTrue();
    }

    @Test
    public void doesNotAppendsOneTimeNomisRefIfnomsNumberNotPresent() {
        Function<String, String> encrypter = string -> string;
        ObjectNode nomsNode = mapper.createObjectNode();
        nomsNode.put("pncNumber", "2018/12345");
        rootNode.set("otherIds", nomsNode);
        ObjectNode newNode = SearchResultAppenders.appendOneTimeNomisRef("token", rootNode, encrypter);
        assertThat(newNode.get("oneTimeNomisRef")).isNull();
    }
}