package utils;

import interfaces.PdfGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface PdfGeneratorMock extends PdfGenerator {

    default <T> CompletionStage<Byte[]> generate(String templateName, T values) {

        setPdfGenerated(true);

        return CompletableFuture.supplyAsync(() -> new Byte[0]);    // Mocked PdfGenerator returns empty Byte array
    }

    void setPdfGenerated(boolean flag);
}