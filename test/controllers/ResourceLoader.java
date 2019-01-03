package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Environment;
import play.Mode;
import play.libs.Json;

import static scala.io.Source.fromInputStream;

public interface ResourceLoader {
    default JsonNode loadJsonResource(String resource) {
        return Json.parse(fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString());
    }

}
