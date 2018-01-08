package services;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import interfaces.PdfGenerator;
import lombok.val;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class RestPdfGenerator implements PdfGenerator {

    private final String pdfGeneratorUrl;
    private final WSClient wsClient;

    @Inject
    public RestPdfGenerator(Config configuration, WSClient wsClient) {

        pdfGeneratorUrl = configuration.getString("pdf.generator.url");
        this.wsClient = wsClient;
    }

    @Override
    public <T> CompletionStage<Byte[]> generate(String templateName, T values) {

        val request = ImmutableMap.of(
                "templateName", templateName,
                "values", values
        );

        Logger.info("Generating PDF: " + request);

        return wsClient.url(pdfGeneratorUrl + "generate").
                post(Json.toJson(request)).
                thenApply(wsResponse -> Json.fromJson(wsResponse.asJson(), Byte[].class));
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return wsClient.url(pdfGeneratorUrl + "healthcheck")
            .get()
            .thenApply(WSResponse::asJson)
            .thenApply(json -> json.get("status").asText())
            .thenApply("OK"::equals)
            .exceptionally(ex -> {
                Logger.warn("Error calling PDF Generator's healthcheck", ex);
                return false;
            });
    }
}
