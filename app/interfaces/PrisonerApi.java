package interfaces;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PrisonerApi {

    @Value
    @Builder(toBuilder = true)
    class Institution {
        private String description;

    }

    @Value
    @Builder(toBuilder = true)
    class Offender {
        private String firstName;
        private String surname;
        private Institution institution;
        private String mostRecentPrisonerNumber;

        public String displayName() {
            return String.format("%s %s", firstName, surname);
        }
    }

    CompletionStage<byte[]> getImage(String nomsNumber);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<Optional<Offender>> getOffenderByNomsNumber(String nomsNumber);
}
