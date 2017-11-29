package utils;

import com.google.common.collect.ImmutableMap;
import interfaces.DocumentStore;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface DocumentStoreMock extends DocumentStore {

    default CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        setPdfUploaded(true);

        return CompletableFuture.supplyAsync(() -> {

            if (onBehalfOfUser != null) {

                return ImmutableMap.of("message", "Upload blows up for this user");
            } else {

                return ImmutableMap.of("ID", "123");
            }
        });
    }

    default CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        return CompletableFuture.supplyAsync(() -> "{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"" + onBehalfOfUser + "\", \"address\": \"" + documentId + "\", \"pnc\": \"Retrieved From Store\" } }");
    }

    default CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {

        return CompletableFuture.supplyAsync(() -> 200);
    }

    default CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        setPdfUpdated(true);

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456"));
    }

    void setPdfUploaded(boolean flag);

    void setPdfUpdated(boolean flag);
}
