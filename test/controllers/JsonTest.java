package controllers;

import com.google.common.collect.ImmutableMap;
import data.ShortFormatPreSentenceReportData;
import interfaces.DocumentStore;
import interfaces.HealthCheckResult;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import utils.TestableWizardController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertTrue;

public class JsonTest implements PdfGenerator, DocumentStore {

    private String updatedData;

    @Test
    public void testJsonSerialisationOfEscapedCharacters() {

        val controller = new TestableWizardController(this, this);

        val data = new ShortFormatPreSentenceReportData();

        data.setPageNumber(1);
        data.setDocumentId("12345");
        data.setName("This has \"quotes\" and 'apostrophes' and \\ backslashes");

        controller.testStoreReport(data);

        assertTrue(updatedData.contains("This has \\\"quotes\\\" and 'apostrophes' and \\\\ backslashes"));
    }

    @Override
    public <T> CompletionStage<Byte[]> generate(String templateName, T values) {

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        this.updatedData = updatedData;
        return CompletableFuture.completedFuture(ImmutableMap.of());
    }

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {
        return null;
    }

    @Override
    public CompletionStage<OriginalData> retrieveOriginalData(String documentId, String onBehalfOfUser) {
        return null;
    }

    @Override
    public CompletionStage<byte[]> retrieveDocument(String documentId, String onBehalfOfUser) {
        return null;
    }

    @Override
    public CompletionStage<String> getDocumentName(String documentId, String onBehalfOfUser) {
        return null;
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {
        return null;
    }
}
