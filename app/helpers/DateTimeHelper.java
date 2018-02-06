package helpers;

import lombok.val;

import java.time.Clock;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Arrays.asList;

public class DateTimeHelper {
    public static long calculateAge(String dateString, Clock clock) {
        val dateOfBirth = parse(dateString, ISO_LOCAL_DATE);
        return ChronoUnit.YEARS.between(dateOfBirth, now(clock));
    }

    public static boolean doesNotlookLikeADate(String dateString) {
        return !looksLikeADate(dateString);
    }

    public static boolean looksLikeADate(String dateString) {
        val datePatterns = asList(
            "yyyy-MM-dd", "yyyy-M-dd", "yyyy-M-d",
            "dd-MM-yyyy", "d-MM-yyyy", "d-M-yyyy",
            "yyyy-MMM-dd", "yyyy-MMM-d",
            "dd-MMM-yyyy", "d-MMM-yyyy",
            "yyyy/MM/dd", "yyyy/M/dd", "yyyy/M/d",
            "dd/MM/yyyy", "d/MM/yyyy", "d/M/yyyy",
            "yyyy/MMM/dd", "yyyy/MMM/d",
            "dd/MMM/yyyy", "d/MMM/yyyy"
            );

        return datePatterns.stream()
                .anyMatch((datePattern) -> canParse(dateString, datePattern));
    }

    private static boolean canParse(String dateString, String pattern) {
        try {
            parse(dateString, new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern).toFormatter());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
