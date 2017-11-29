package utils;

import interfaces.PdfGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 *
 */
public class SimplePdfGeneratorMock implements PdfGenerator {
    @Override
    public <T> CompletionStage<Byte[]> generate(String templateName, T values) {
        return CompletableFuture.supplyAsync(() -> new Byte[0]);    // Mocked PdfGenerator returns empty Byte array
    }
}
