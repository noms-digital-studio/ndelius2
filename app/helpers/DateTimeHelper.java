package helpers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class DateTimeHelper {
    public static long calculateAge(String dateString, Clock clock) {
        LocalDate dateOfBirth = parse(dateString, ISO_LOCAL_DATE);
        return ChronoUnit.YEARS.between(dateOfBirth, now(clock));
    }

}
