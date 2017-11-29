package utils;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class SimpleDocumentStoreMock implements DocumentStoreMock {
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        setPdfUploaded(true);

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123"));
    }

    @Override
    public void setPdfUploaded(boolean flag) {

    }

    @Override
    public void setPdfUpdated(boolean flag) {

    }
}
