package interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;


public interface OffenderApi {

    @Value
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
    class ContactDetails {
        private List<OffenderAddress> addresses;

        public Optional<OffenderAddress> currentAddress() {
            return ofNullable(addresses)
                .flatMap(offenderAddresses ->
                    offenderAddresses.stream()
                        .filter(address -> ofNullable(address.getFrom()).isPresent())
                        .max(comparing(OffenderAddress::getFrom)));
        }

    }

    @Value
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
