package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtDefendant {
    private String pncNumber;
    private String surname;
    private String firstName;
    private LocalDate dateOfBirth;
}
