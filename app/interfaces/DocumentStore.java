package interfaces;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DocumentStore {

    @Data
    class OriginalData {
        private final String userData;
        private final OffsetDateTime lastModifiedDate;
    }

    @Data
    class DocumentEntity {
        private final String filename;
        private final String tableName;
        private final String entityName;
    }

    CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, DocumentEntity documentEntity, String onBehalfOfUser, String originalData, String crn, Long entityId);

    CompletionStage<OriginalData> retrieveOriginalData(String documentId, String onBehalfOfUser);

    CompletionStage<byte[]> retrieveDocument(String documentId, String onBehalfOfUser);

    CompletionStage<String> getDocumentName(String documentId, String onBehalfOfUser);

    CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId);

    CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId);

    CompletionStage<HealthCheckResult> isHealthy();
}
