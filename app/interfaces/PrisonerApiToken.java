package interfaces;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public interface PrisonerApiToken extends Supplier<String> {
    default CompletionStage<String> getAsync(){
        return CompletableFuture.completedFuture(get());
    }

    default CompletionStage<HealthCheckResult> isHealthy(){
        return CompletableFuture.completedFuture(HealthCheckResult.healthy());
    }

    default void clearToken(){
        // no-op by default
    }
}
