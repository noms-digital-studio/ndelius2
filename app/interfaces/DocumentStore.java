package interfaces;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface DocumentStore {

    CompletionStage<Map> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String crn, String author, Integer entityId);
}
