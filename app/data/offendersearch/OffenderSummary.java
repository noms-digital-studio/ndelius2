package data.offendersearch;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
@Builder
public class OffenderSummary {
    private String offenderId;
    private String firstName;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private String crn;
    private int age;
    private String previousSurname;

    private String risk;
    private boolean currentOffender;

    private List<Alias> aliases = emptyList();
    private List<Address> addresses = emptyList();
}