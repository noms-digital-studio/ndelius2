package views.helpers;

import com.google.common.collect.ImmutableMap;
import interfaces.DocumentStore;
import lombok.val;
import play.libs.Json;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static helpers.JsonHelper.jsonToMap;
import static helpers.JsonHelper.stringify;
import static play.libs.Json.toJson;

public class AlfrescoDataHelper {
    public static CompletionStage<DocumentStore.OriginalData> legacyReportWith(ImmutableMap<String, Object> values) {
        return  legacyReportWith(values, OffsetDateTime.now());
    }

    public static CompletionStage<DocumentStore.OriginalData> legacyReportWith(ImmutableMap<String, Object> values, OffsetDateTime lastUpdated) {
        val originalReport = Json.parse(AlfrescoDataHelper.class.getResourceAsStream("/alfrescodata/legacyOffenderAssessment.json"));

        val reportJson = stringify(toJson(merge(
                ImmutableMap.of("templateName", originalReport.get("templateName").asText()),
                ImmutableMap.of("values", merge(
                        jsonToMap(originalReport.get("values")),
                        values)))));
        return CompletableFuture.completedFuture(new DocumentStore.OriginalData(reportJson, lastUpdated));
    }

    private static Map<String, Object> merge(Map<String, String> original, Map<String, Object> additions) {
        Map<String, Object> mergedValues = new HashMap<>();
        mergedValues.putAll(original);
        mergedValues.putAll(additions);
        return ImmutableMap.copyOf(mergedValues);
    }


}
