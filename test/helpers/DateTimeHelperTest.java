package helpers;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeHelperTest {

    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));

    @Test
    public void calculatesCorrectAge() {
        assertThat(DateTimeHelper.calculateAge("1969-01-01", fixedClock)).isEqualTo(1);
        assertThat(DateTimeHelper.calculateAge("1954-02-01", fixedClock)).isEqualTo(15);
    }

}