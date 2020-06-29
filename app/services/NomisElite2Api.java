package services;

import akka.util.ByteString;
import com.typesafe.config.Config;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
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
import java.util.function.Function;
import java.util.function.Supplier;

import static helpers.JsonHelper.readValue;
import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.HeaderNames.AUTHORIZATION;
import static play.mvc.Http.Status.OK;

public class NomisElite2Api implements PrisonerCategoryApi, PrisonerApi {
    private final String apiBaseUrl;
    private final WSClient wsClient;
    private final PrisonerApiToken apiToken;

    @Value
    @Builder(toBuilder = true)
    static class OffenderEntity {
        String category;
        String categoryCode;
        String bookingNo;
        String firstName;
        String lastName;
        LivingUnit assignedLivingUnit;
    }

    @Value
    @Builder(toBuilder = true)
    static class LivingUnit {
        String agencyName;
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
                                maybeResponse.flatMap(this::transformOffenderResponseToCategory)));
    }

    @Override
    public CompletionStage<Optional<Offender>> getOffenderByNomsNumber(String nomsNumber) {
        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%selite2api/api/offenders/%s", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(this::checkForMaybeResponse)
                        .thenApply(maybeResponse ->
                                maybeResponse.map(this::transformOffenderResponse)));
    }


    @Override
    public CompletionStage<byte[]> getImage(String nomsNumber) {
        Function<WSResponse, WSResponse> checkForValidImageResponse = (wsResponse) -> checkForValidResponse(wsResponse, () -> String.format("No images found for offender %s", nomsNumber));
        return apiToken
                .getAsync()
                .thenCompose(token -> wsClient
                        .url(String.format("%selite2api/api/bookings/offenderNo/%s/image/data", apiBaseUrl, nomsNumber))
                        .addHeader(AUTHORIZATION, "Bearer " + token)
                        .get()
                        .thenApply(checkForValidImageResponse)
                        .thenApply(WSResponse::getBodyAsBytes)
                        .thenApply(ByteString::toArray));
    }


    private Optional<Category> transformOffenderResponseToCategory(WSResponse response) {
        return CategoryTransformer.categoryOf(readValue(response.getBody(), OffenderEntity.class));
    }


    private Optional<WSResponse> checkForMaybeResponse(WSResponse wsResponse) {
        return NomisReponseHelper.checkForMaybeResponse(wsResponse, apiToken);
    }

    private WSResponse checkForValidResponse(WSResponse wsResponse, Supplier<String> notFoundMessage) {
        return NomisReponseHelper.checkForValidResponse(wsResponse, apiToken, notFoundMessage);
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

    private Offender transformOffenderResponse(WSResponse response) {
        return OffenderTransformer.offenderOf(readValue(response.getBody(), OffenderEntity.class));
    }
    static class OffenderTransformer {
        static Offender offenderOf(OffenderEntity offenderEntity) {
            return Offender
                    .builder()
                    .firstName(offenderEntity.getFirstName())
                    .surname(offenderEntity.getLastName())
                    .mostRecentPrisonerNumber(offenderEntity.getBookingNo())
                    .institution(
                            Institution
                                    .builder()
                                    .description(Optional.ofNullable(offenderEntity.getAssignedLivingUnit()).map(LivingUnit::getAgencyName).orElse("Unknown"))
                                    .build())
                    .build();

        }
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
