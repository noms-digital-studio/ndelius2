package data.offendersearch;

import lombok.Data;

import java.util.List;

@Data
public class OffenderDetails {
    private String offenderId;
    private String firstName;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private String crn;
    private String age;
    private String previousSurname;

    private String risk;
    private boolean currentOffender;

    private List<String> aliases;
    private List<String> addresses;
}