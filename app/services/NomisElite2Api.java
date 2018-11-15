package services;

import com.typesafe.config.Config;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApiToken;
import interfaces.PrisonerCategoryApi;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static helpers.JsonHelper.readValue;
import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.OK;

public class NomisElite2Api implements PrisonerCategoryApi {
    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    @Value
    @Builder(toBuilder = true)
    static class OffenderEntity {
        private String category;
        private String categoryCode;
    }



    @Inject
    public NomisElite2Api(Config configuration, WSClient wsClient, PrisonerApiToken apiToken) {

        apiBaseUrl = configuration.getString("nomis.api.url");

        this.wsClient = wsClient;
        this.apiToken = apiToken;

        Logger.info("Running with NomisElite2Api");

    }



    @Override
    public CompletionStage<Optional<Category>> getOffenderCategoryByNomsNumber(String nomsNumber) {
        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%selite2api/api/bookings/offenderNo/%s?fullInfo=true", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(this::checkForMaybeResponse)
                        .thenApply(maybeResponse ->
                                maybeResponse.flatMap(this::transformOffenderResponse)));
    }

    private Optional<Category> transformOffenderResponse(WSResponse response) {
        return CategoryTransformer.categoryOf(readValue(response.getBody(), OffenderEntity.class));
    }


    private Optional<WSResponse> checkForMaybeResponse(WSResponse wsResponse) {
        return NomisReponseHelper.checkForMaybeResponse(wsResponse, apiToken);
    }



    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {

        return wsClient.url(apiBaseUrl + "elite2api/health").
                get().
                thenApply(wsResponse -> {

                    val healthy = wsResponse.getStatus() == OK;

                    if (!healthy) {
                        Logger.warn("Elite2 API Response Status: " + wsResponse.getStatus());
                        return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                    }

                    return healthy(wsResponse.asJson());
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking Elite2 API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }

    static class CategoryTransformer {
        static Optional<Category> categoryOf(OffenderEntity offenderEntity) {
            val mayebCategoryCode = Optional.ofNullable(offenderEntity.getCategoryCode());


            return mayebCategoryCode
                    .map(code ->
                            Category
                                    .builder()
                                    .code(code)
                                    .description(offenderEntity.getCategory()).build()
                    );
        }
    }
}
