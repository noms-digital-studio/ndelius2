package helpers;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static helpers.DateTimeHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeHelperTest {

    private static final String EXPECTED_CANONICAL_DATE = "1992-02-01";
    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));

    @Test
    public void calculatesCorrectAge() {
        assertThat(calculateAge("1969-01-01", fixedClock)).isEqualTo(1);
        assertThat(calculateAge("1954-02-01", fixedClock)).isEqualTo(15);
    }

    @Test
    public void returnsTrueForStringsThatAreValidDates() {
        assertThat(covertToCanonicalDate("1992-02-01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-02-1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-2-01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-2-1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01-02-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01-2-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1-02-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1-2-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);

        assertThat(covertToCanonicalDate("01-Feb-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01-feb-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01-february-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1-Feb-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1-feb-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1-february-1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-Feb-01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-feb-01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-february-01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-Feb-1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-feb-1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992-february-1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);

        assertThat(covertToCanonicalDate("1992/02/01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/02/1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/2/01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/2/1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01/02/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1/02/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01/2/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1/2/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);

        assertThat(covertToCanonicalDate("1992/Feb/01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/feb/01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/february/01").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/Feb/1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/feb/1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1992/february/1").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01/Feb/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01/feb/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("01/february/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1/Feb/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1/feb/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
        assertThat(covertToCanonicalDate("1/february/1992").get()).isEqualTo(EXPECTED_CANONICAL_DATE);
    }

    @Test
    public void returnsFalseForStringsThatAreNotValidDates() {
        assertThat(covertToCanonicalDate("1992 02 01").isPresent()).isEqualTo(false);
        assertThat(covertToCanonicalDate("1992:02:01").isPresent()).isEqualTo(false);
        assertThat(covertToCanonicalDate("1992:Feb:01").isPresent()).isEqualTo(false);
        assertThat(covertToCanonicalDate("foo bar").isPresent()).isEqualTo(false);
    }

    @Test
    public void returnsTrueIfInputCanBeConvertedToADate() {
        assertThat(canBeConvertedToADate("5/9/1977")).isTrue();
    }

    @Test
    public void returnsFalseIfInputCanNotBeConvertedToADate() {
        assertThat(canBeConvertedToADate("john")).isFalse();
    }

    @Test
    public void termsThatLookLikeDatesAreExtractedAndNormalised() {
        assertThat(termsThatLookLikeDates("sna 28/02/2018 foo 2017-Jun-3 bar"))
            .containsExactly("2018-02-28", "2017-06-03");
    }

    @Test
    public void termsThatLookLikeDatesReturnsEmptyArray() {
        assertThat(termsThatLookLikeDates("sna foo bar")).isEmpty();
    }

    @Test
    public void formatsADateCorrectly() {
        assertThat(format("2018-06-29")).isEqualTo("29/06/2018");
    }

    @Test
    public void convertsADateCorrectly() {
        assertThat(convert("2018-06-29")).isEqualTo(LocalDate.of(2018, 6,29));
    }


}