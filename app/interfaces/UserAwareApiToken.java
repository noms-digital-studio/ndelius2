package interfaces;

import java.util.concurrent.CompletionStage;

public interface UserAwareApiToken {
    CompletionStage<String> get(String username);

    CompletionStage<HealthCheckResult> isHealthy();
}
