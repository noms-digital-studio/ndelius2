package data;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.DateTimeException;
import java.time.LocalDate;

@Data
public class Defendant {
    private String pncNumber;
    private String surname;
    private String firstName;
    private String dateOfBirth_day;
    private String dateOfBirth_month;
    private String dateOfBirth_year;

    public LocalDate getDateOfBirth() {
        if (NumberUtils.isParsable(dateOfBirth_day) && NumberUtils.isParsable(dateOfBirth_month) && NumberUtils.isParsable(dateOfBirth_year)) {
            try {
                return LocalDate.of(NumberUtils.toInt(dateOfBirth_year), NumberUtils.toInt(dateOfBirth_month), NumberUtils.toInt(dateOfBirth_day));
            } catch (DateTimeException e) {
                //
            }
        }
        return null;
    }
}
