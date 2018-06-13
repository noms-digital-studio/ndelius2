package helpers;

import lombok.val;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class DateTimeHelper {
    public static long calculateAge(String dateString, Clock clock) {
        val dateOfBirth = parse(dateString, ISO_LOCAL_DATE);
        return ChronoUnit.YEARS.between(dateOfBirth, now(clock));
    }

    public static boolean canBeConvertedToADate(String dateString) {
        return covertToCanonicalDate(dateString).isPresent();
    }

    public static Optional<String> covertToCanonicalDate(String dateString) {
        List<String> datePatterns = asList(
            "yyyy/MMMM/dd", "yyyy-MMMM-dd",
            "yyyy/MMMM/d", "yyyy-MMMM-d",
            "yyyy/MMM/dd", "yyyy-MMM-dd",
            "yyyy/MMM/d", "yyyy-MMM-d",
            "yyyy/MM/dd", "yyyy-MM-dd",
            "yyyy/M/dd", "yyyy-M-dd",
            "yyyy/MM/d", "yyyy-MM-d",
            "yyyy/M/d", "yyyy-M-d",
            "dd/MMMM/yyyy", "dd-MMMM-yyyy",
            "d/MMMM/yyyy", "d-MMMM-yyyy",
            "dd/MMM/yyyy", "dd-MMM-yyyy",
            "d/MMM/yyyy", "d-MMM-yyyy",
            "dd/MM/yyyy", "dd-MM-yyyy",
            "dd/M/yyyy", "dd-M-yyyy",
            "d/MM/yyyy", "d-MM-yyyy",
            "d/M/yyyy", "d-M-yyyy"
            );

        return datePatterns.stream()
                .filter(datePattern -> canParse(dateString, datePattern))
                .map(datePattern -> LocalDate.parse(dateString, dateTimeBuilderFor(datePattern).toFormatter()))
                .map(date -> date.format(ISO_LOCAL_DATE))
                .findFirst();
    }

    private static boolean canParse(String dateString, String pattern) {
        try {
            parse(dateString, dateTimeBuilderFor(pattern).toFormatter());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static DateTimeFormatterBuilder dateTimeBuilderFor(String datePattern) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(datePattern);
    }

    public static List<String> termsThatLookLikeDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .map(DateTimeHelper::covertToCanonicalDate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }
}
