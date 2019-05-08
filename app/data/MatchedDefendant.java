package data;

import lombok.Builder;

@Builder
public class MatchedDefendant {
    private String crn;
    private String pncNumber;
    private String surname;
    private String firstName;
    private String dateOfBirth;
    private String address;

    public String getCrn() {
        return crn;
    }

    public String getPncNumber() {
        return pncNumber;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }
}
