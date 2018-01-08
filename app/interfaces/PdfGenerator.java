package interfaces;

import java.util.concurrent.CompletionStage;

public interface PdfGenerator {

    <T> CompletionStage<Byte[]> generate(String templateName, T values);

    CompletionStage<Boolean> isHealthy();
}
