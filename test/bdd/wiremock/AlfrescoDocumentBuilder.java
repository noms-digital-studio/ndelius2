package bdd.wiremock;

import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static play.libs.Json.toJson;

public class AlfrescoDocumentBuilder {
    private final Map<String, String> values;
    private final Map<String, String> document;

    public AlfrescoDocumentBuilder() {
        this.values = new HashMap<>();
        this.document = new HashMap<>();
    }

    public static AlfrescoDocumentBuilder standardDocument() {
        val builder = new AlfrescoDocumentBuilder();
        builder.document.put("ID", "309db0bf-f8bb-4ac0-b325-5dbc368e2636");
        builder.document.put("lastModifiedDate", "2007-12-03T10:15:30+01:00");
        builder.values.put("pageNumber", "2");
        builder.values.put("crn", "X12345");
        return builder;
    }

    public AlfrescoDocumentBuilder withValuesItem(String key, String value) {
        values.put(key, value);
        return this;
    }
    public AlfrescoDocumentBuilder withItem(String key, String value) {
        document.put(key, value);
        return this;
    }
    public String build() {
        val userData = JsonHelper.stringify(ImmutableMap.of("values", values));
        document.put("userData", userData);
        return toJson(document).toString();
    }

    public String userData() {
        return JsonHelper.stringify(ImmutableMap.of("values", values));
    }
}
