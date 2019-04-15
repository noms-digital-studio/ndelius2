package services;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import interfaces.HealthCheckResult;
import interfaces.PdfGenerator;
import lombok.val;
import org.apache.commons.lang3.reflect.FieldUtils;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static interfaces.HealthCheckResult.unhealthy;

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

        Logger.info("Generating PDF: {} {}", values, asLoggableLine(values));

        return wsClient.url(pdfGeneratorUrl + "generate").
                post(Json.toJson(request)).
                thenApply(wsResponse -> Json.fromJson(wsResponse.asJson(), Byte[].class));
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return wsClient.url(pdfGeneratorUrl + "healthcheck")
            .get()
            .thenApply(WSResponse::asJson)
            .thenApply(json -> new HealthCheckResult("OK".equals(json.get("status").asText()), json))
            .exceptionally(ex -> {
                Logger.warn("Error calling PDF Generator's healthcheck", ex);
                return unhealthy(ex.getLocalizedMessage());
            });
    }

    private <T> String asLoggableLine(T values) {
        return FieldUtils.getAllFieldsList(values.getClass()).
                stream().
                map(field -> String.format("%s=%s", field.getName(), asStringValue(field, values))).collect(Collectors.joining(", "));
    }

    private <T> String asStringValue(Field field, T values) {
        return Optional.ofNullable(valueOf(field, values))
                .map(value -> field.getType().equals(String.class) ? String.format("[len=%s]", value.toString().length()) : value.toString())
                .orElse("");
    }

    private <T> Object valueOf(Field field, T values) {
        try {
            return FieldUtils.readField(field, values, true);
        } catch (IllegalAccessException e) {
            return "*";
        }
    }
}
