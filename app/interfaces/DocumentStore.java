package interfaces;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DocumentStore {

    CompletionStage<Map> uploadNewPdf(Byte[] document, String filename, String originalData, String onBehalfOfUser, String crn, Integer entityId);

    CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser);
}
