package interfaces;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DocumentStore {

    CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String originalData, String onBehalfOfUser, String crn, Long entityId);

    CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser);
}
