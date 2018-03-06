package services.fakes;

import com.google.common.collect.ImmutableMap;
import interfaces.DocumentStore;
import lombok.val;
import play.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDocumentStore implements DocumentStore {

    private Map<String, HashMap<String, Object>> documentStore = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser,
                                                             String originalData, String crn, Long entityId) {
        val parameters = new HashMap<String, Object>() {
            {
                put("document", document);
                put("filename", filename);
                put("onBehalfOfUser", onBehalfOfUser);
                put("originalData", originalData);
                put("entityId", entityId != null ? entityId.toString() : "");
                put("crn", crn);
            }
        };

        String key = UUID.randomUUID().toString();
        documentStore.put(key, parameters);
        Logger.debug(String.format("uploadNewPdf: storing %s against key %s", parameters, key));
        return CompletableFuture.completedFuture(ImmutableMap.of("ID", key));
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {
        HashMap<String, Object> parameters = documentStore.get(documentId);

        Logger.debug(String.format("retrieveOriginalData: for key %s", documentId));
        return CompletableFuture.completedFuture((String) parameters.get("originalData"));
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {
        return CompletableFuture.completedFuture(Integer.MIN_VALUE);
    }

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {
        HashMap<String, Object> parameters = documentStore.get(documentId);
        String entityId = (String) parameters.get("entityId");

        val newParameters = new HashMap<String, Object>() {
            {
                put("document", document);
                put("filename", filename);
                put("onBehalfOfUser", onBehalfOfUser);
                put("originalData", updatedData);
                put("entityId", entityId != null ? entityId : "");
            }
        };

        documentStore.put(documentId, newParameters);
        Logger.debug(String.format("updateExistingPdf: storing %s against key %s", newParameters, documentId));
        return CompletableFuture.completedFuture(ImmutableMap.of("ID", documentId));
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return CompletableFuture.completedFuture(true);
    }
}
