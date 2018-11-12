package services;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
import interfaces.PrisonerApiToken;
import lombok.val;
import org.apache.commons.lang3.NotImplementedException;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.OK;

@Deprecated
public class NomisPrisonerApi implements PrisonerApi {

    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    @Inject
    public NomisPrisonerApi(Config configuration, WSClient wsClient, PrisonerApiToken apiToken) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;

        Logger.info("Running with legacy NomisPrisonerAPI");
    }

    @Override
    public CompletionStage<byte[]> getImage(String nomisId) {

        final Function<WSResponse, WSResponse> reportNonOKResponse = wsResponse -> {

            if (wsResponse.getStatus() != OK) {
                Logger.warn("Nomis Image API Id: {} bad response Status: {}", nomisId, wsResponse.getStatus());
            }
            return wsResponse;
        };

        Logger.info("Nomis Image API request for Id: {}", nomisId);

        return wsClient.url(apiBaseUrl + "nomisapi/offenders/" + nomisId + "/image").
                addHeader(AUTHORIZATION, "Bearer " + apiToken.get()).
                get().
                thenApply(reportNonOKResponse).
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap).
                thenApply(Optional::ofNullable).
                thenApply(result -> result.map(strings -> strings.get("image")).orElse("")).
                thenApply(base64 -> {

                    if (Strings.isNullOrEmpty(base64)) {

                        Logger.warn("Empty Image Base64 text for Nomis Id: {}", nomisId);
                        throw new RuntimeException("No NOMIS image retrieved");
                    }
                    else {
                        return Base64.getDecoder().decode(base64);
                    }
                });
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {

        return wsClient.url(apiBaseUrl + "custodyapi/health").
                get().
                thenApply(wsResponse -> {

                    val healthy = wsResponse.getStatus() == OK;

                    if (!healthy) {
                        Logger.warn("Custody API Response Status: " + wsResponse.getStatus());
                        return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                    }

                    return healthy(wsResponse.asJson());
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking Custody API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
        }

    @Override
    public CompletionStage<Optional<Offender>> getOffenderByNomsNumber(String nomsNumber) {
        throw new NotImplementedException("This API is deprecated use NomisCustodyApi instead");
    }
}
