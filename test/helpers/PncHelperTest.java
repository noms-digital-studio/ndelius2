package helpers;

import org.junit.Test;

import static helpers.PncHelper.canBeConvertedToAPnc;
import static helpers.PncHelper.covertToCanonicalPnc;
import static org.assertj.core.api.Assertions.assertThat;

public class PncHelperTest {

    @Test
    public void recognisesValidPncStrings() {
        assertThat(canBeConvertedToAPnc("2003/1234567A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/234567A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/34567A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/4567A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/567A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/67A")).isTrue();
        assertThat(canBeConvertedToAPnc("2003/7A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/1234567A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/234567A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/34567A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/4567A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/567A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/67A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/7A")).isTrue();
        assertThat(canBeConvertedToAPnc("03/7A")).isTrue();
    }

    @Test
    public void rejectsNonPncFormattedTerms() {
        assertThat(canBeConvertedToAPnc("2003/A")).isFalse();
        assertThat(canBeConvertedToAPnc("203/1234567A")).isFalse();
        assertThat(canBeConvertedToAPnc("1234567A")).isFalse();
        assertThat(canBeConvertedToAPnc("2013")).isFalse();
        assertThat(canBeConvertedToAPnc("john smith")).isFalse();
        assertThat(canBeConvertedToAPnc("16/11/2018")).isFalse();
        assertThat(canBeConvertedToAPnc("111111/11A")).isFalse();
        assertThat(canBeConvertedToAPnc("SF68/945674U")).isFalse();
    }

    @Test
    public void convertsPncsToStandardFormat() {
        assertThat(covertToCanonicalPnc("2003/1234567A")).isEqualTo("2003/1234567a");
        assertThat(covertToCanonicalPnc("2003/0234567A")).isEqualTo("2003/234567a");
        assertThat(covertToCanonicalPnc("2003/0034567A")).isEqualTo("2003/34567a");
        assertThat(covertToCanonicalPnc("2003/0004567A")).isEqualTo("2003/4567a");
        assertThat(covertToCanonicalPnc("2003/0000567A")).isEqualTo("2003/567a");
        assertThat(covertToCanonicalPnc("2003/0000067A")).isEqualTo("2003/67a");
        assertThat(covertToCanonicalPnc("2003/0000007A")).isEqualTo("2003/7a");
        assertThat(covertToCanonicalPnc("2003/0000000A")).isEqualTo("2003/0a");
        assertThat(covertToCanonicalPnc("03/1234567A")).isEqualTo("03/1234567a");
        assertThat(covertToCanonicalPnc("03/0234567A")).isEqualTo("03/234567a");
        assertThat(covertToCanonicalPnc("03/0034567A")).isEqualTo("03/34567a");
        assertThat(covertToCanonicalPnc("03/0004567A")).isEqualTo("03/4567a");
        assertThat(covertToCanonicalPnc("03/0000567A")).isEqualTo("03/567a");
        assertThat(covertToCanonicalPnc("03/0000067A")).isEqualTo("03/67a");
        assertThat(covertToCanonicalPnc("03/0000007A")).isEqualTo("03/7a");
        assertThat(covertToCanonicalPnc("03/0000000A")).isEqualTo("03/0a");
    }

}