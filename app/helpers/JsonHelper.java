package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Map;
import lombok.val;
import play.libs.Json;
import play.mvc.Result;

import static play.mvc.Results.ok;

public interface JsonHelper {

    static Map<String, String> jsonToMap(String json) {

        return jsonToMap(Json.parse(json));
    }

    static Map<String, String> jsonToMap(JsonNode json) {

        val mapper = Json.mapper();

        try {
            return mapper.readValue(
                    mapper.treeAsTokens(json),
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
            );
        } catch (IOException ex) {

            return null;
        }
    }

    static <T> Result okJson(T data) {

        return ok(Json.toJson(data));
    }
}
