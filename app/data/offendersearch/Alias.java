package data.offendersearch;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Alias {
    private String dateOfBirth;
    private String firstName;
    private String surname;
}
