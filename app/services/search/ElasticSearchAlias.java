package services.search;

import data.offendersearch.Alias;
import lombok.Data;

@Data
public class ElasticSearchAlias {
    private String dateOfBirth;
    private String firstName;
    private String surname;

    public Alias toAlias() {
        return Alias.builder()
            .firstName(firstName)
            .surname(surname)
            .dateOfBirth(dateOfBirth)
            .build();
    }
}
