package interfaces;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletionStage;

public interface PrisonerApi {

    @Data
    @Builder(toBuilder = true)
    class Institution {
        private String description;

    }

    @Data
    @Builder(toBuilder = true)
    class Offender {
        private Institution institution;

    }

    CompletionStage<byte[]> getImage(String nomsNumber);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<Offender> getOffenderByNomsNumber(String nomsNumber);
}
