package interfaces;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DocumentStore {

    CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId);

    CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser);

    CompletionStage<byte[]> retrieveDocument(String documentId, String onBehalfOfUser);

    CompletionStage<String> getDocumentName(String documentId, String onBehalfOfUser);

    CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId);

    CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId);

    CompletionStage<Boolean> isHealthy();
}
