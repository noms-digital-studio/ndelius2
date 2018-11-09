package services.stubs;

import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
import play.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StubPrisonerApi implements PrisonerApi {

    public StubPrisonerApi() {
        Logger.info("Running with stub NomisPrisonerAPI");
    }
    @Override
    public CompletionStage<byte[]> getImage(String nomsNumber) {
        return CompletableFuture.completedFuture(new byte[]{});
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return CompletableFuture.completedFuture(HealthCheckResult.healthy());
    }

    @Override
    public CompletionStage<Optional<Offender>> getOffenderByNomsNumber(String nomsNumber) {
        return CompletableFuture.completedFuture(Optional.ofNullable(Offender
                .builder()
                .institution(Institution.builder().description("HMP Leeds").build())
                .build()));
    }
}
