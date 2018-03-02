package helpers;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static helpers.DateTimeHelper.calculateAge;
import static helpers.DateTimeHelper.canBeConvertedToADate;
import static helpers.DateTimeHelper.covertToCanonicalDate;
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
}