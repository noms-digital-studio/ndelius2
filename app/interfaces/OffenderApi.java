package interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;


public interface OffenderApi {

    @Value
    @Builder(toBuilder = true)
    class Offender {
        private String firstName;
        private String surname;
        private List<String> middleNames;
        private String dateOfBirth;
        private Map<String, String> otherIds;
        private ContactDetails contactDetails;

        public String displayName() {

            String middleName = ofNullable(middleNames).flatMap(names -> names.stream().findFirst()).orElse(null);
            return joinList(" ", asList(firstName, middleName, surname));
        }
    }

    @Value
    @Builder(toBuilder = true)
    class ContactDetails {
        private List<OffenderAddress> addresses;

        public Optional<OffenderAddress> mainAddress() {
            return ofNullable(addresses)
                .flatMap(offenderAddresses -> offenderAddresses.stream()
                    .filter(address -> ofNullable(address.getStatus()).isPresent())
                    .filter(address -> "M".equals(address.getStatus().getCode().toUpperCase()))
                    .findFirst());
        }
    }

    @Value
    @Builder(toBuilder = true)
    class OffenderAddress {
        private String buildingName;
        private String addressNumber;
        private String streetName;
        private String district;
        private String town;
        private String county;
        private String postcode;
        private String from;
        private String to;
        private AddressStatus status;

        public String render() {

            val address = asList(
                    buildingName,
                    joinList(" ", asList(addressNumber, streetName)),
                    district,
                    town,
                    county,
                    postcode);

            return joinList("\n", address);
        }
    }

    @Value
    @Builder(toBuilder = true)
    class AddressStatus {
        private String code;
        private String description;
    }

    static String joinList(String delimiter, List<String> list) {
        return String.join(delimiter,
            list.stream()
                .map(Optional::ofNullable)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList()));
    }

    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);

    CompletionStage<HealthCheckResult> isHealthy();

    CompletionStage<JsonNode> searchDb(Map<String, String> queryParams);

    CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams);

    CompletionStage<Map<String, String>> probationAreaDescriptions(String bearerToken, List<String> probationAreaCodes);

    CompletionStage<Offender> getOffenderByCrn(String bearerToken, String crn);
}
