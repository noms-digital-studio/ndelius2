package interfaces;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PrisonerCategoryApi {

    @Value
    @Builder(toBuilder = true)
    class Category {
        private String code;
        private String description;

    }

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<Optional<Category>> getOffenderCategoryByNomsNumber(String nomsNumber);
}
