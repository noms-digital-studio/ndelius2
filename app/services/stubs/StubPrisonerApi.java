package services.stubs;

import interfaces.HealthCheckResult;
import interfaces.PrisonerApi;
import interfaces.PrisonerCategoryApi;
import play.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StubPrisonerApi implements PrisonerApi, PrisonerCategoryApi {

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
                .mostRecentPrisonerNumber("4815")
                .institution(Institution.builder().description("HMP Leeds").build())
                .build()));
    }

    @Override
    public CompletionStage<Optional<Category>> getOffenderCategoryByNomsNumber(String nomsNumber) {
        return CompletableFuture.completedFuture(Optional.of(Category.builder().code("A").description("Cat A").build()));
    }
}
