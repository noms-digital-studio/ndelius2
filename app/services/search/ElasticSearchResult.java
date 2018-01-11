package services.search;

import data.offendersearch.Address;
import data.offendersearch.Alias;
import data.offendersearch.OffenderSummary;
import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
public class ElasticSearchResult {
    private Long offenderId;
    private String firstName;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private Ids ids;
    private ElasticSearchContactDetails contactDetails;
    private List<ElasticSearchAlias> offenderAliases = emptyList();


    public OffenderSummary toOffenderSummary() {
        return OffenderSummary.builder()
            .offenderId(offenderId.toString())
            .firstName(firstName)
            .surname(surname)
            .dateOfBirth(dateOfBirth)
            .gender(gender)
            .crn(ids.getCrn())
            .currentOffender(true)
            .previousSurname("Not Yet Implemented")
            .risk("Not Yet Implemented")
            .aliases(convertAliases())
            .addresses(convertAddresses())
            .build();
    }

    private List<Address> convertAddresses() {
        return contactDetails.getAddresses().stream()
            .map(ElasticSearchAddress::toAddress)
            .collect(toList());
    }

    private List<Alias> convertAliases() {
        return offenderAliases.stream()
            .map(ElasticSearchAlias::toAlias)
            .collect(toList());
    }
}
