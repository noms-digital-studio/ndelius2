package services.helpers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static services.helpers.CroHelper.canBeConvertedToACro;
import static services.helpers.CroHelper.covertToCanonicalCro;
import static services.helpers.CroHelper.termsThatLookLikeCroNumbers;

public class CroHelperTest {

    @Test
    public void recognisesValidFullCroStrings() {
        assertThat(canBeConvertedToACro("123456/99Z")).isTrue();
        assertThat(canBeConvertedToACro("123456/99z")).isTrue();
        assertThat(canBeConvertedToACro("123456/08Z")).isTrue();
        assertThat(canBeConvertedToACro("000000/00A")).isTrue();
        assertThat(canBeConvertedToACro("123456/00Z")).isTrue();
        assertThat(canBeConvertedToACro("12345/99Z")).isTrue();
        assertThat(canBeConvertedToACro("1234/99Z")).isTrue();
        assertThat(canBeConvertedToACro("123/99Z")).isTrue();
        assertThat(canBeConvertedToACro("12/99Z")).isTrue();
        assertThat(canBeConvertedToACro("1/99Z")).isTrue();
    }

    @Test
    public void recognisesValidSearchFileCroStrings() {
        assertThat(canBeConvertedToACro("SF94/123456A")).isTrue();
        assertThat(canBeConvertedToACro("sf94/123456a")).isTrue();
        assertThat(canBeConvertedToACro("SF00/000000A")).isTrue();
        assertThat(canBeConvertedToACro("SF94/12345A")).isTrue();
        assertThat(canBeConvertedToACro("SF94/1234A")).isTrue();
        assertThat(canBeConvertedToACro("SF94/123A")).isTrue();
        assertThat(canBeConvertedToACro("SF94/12A")).isTrue();
        assertThat(canBeConvertedToACro("SF94/1A")).isTrue();
    }

    @Test
    public void rejectsNonCroFormattedTerms() {
        assertThat(canBeConvertedToACro("1234567/98A")).isFalse();
        assertThat(canBeConvertedToACro("12345A/98A")).isFalse();
        assertThat(canBeConvertedToACro("123456/8A")).isFalse();
        assertThat(canBeConvertedToACro("123456/zxA")).isFalse();
        assertThat(canBeConvertedToACro("TG98/123456A")).isFalse();
        assertThat(canBeConvertedToACro("SF8/123456A")).isFalse();
        assertThat(canBeConvertedToACro("SFab/123456A")).isFalse();
        assertThat(canBeConvertedToACro("123456A")).isFalse();
        assertThat(canBeConvertedToACro("2013")).isFalse();
        assertThat(canBeConvertedToACro("john smith")).isFalse();
        assertThat(canBeConvertedToACro("john/smith")).isFalse();
        assertThat(canBeConvertedToACro("16/11/2018")).isFalse();
        assertThat(canBeConvertedToACro("16-11-2018")).isFalse();
        assertThat(canBeConvertedToACro("2003/1234567A")).isFalse();
        assertThat(canBeConvertedToACro("03/1234567A")).isFalse();
        assertThat(canBeConvertedToACro("")).isFalse();
        assertThat(canBeConvertedToACro("SF95/BBBBBBA")).isFalse();
        assertThat(canBeConvertedToACro("BBBBBBA/17D")).isFalse();
    }

    @Test
    public void convertsCroToStandardFormat() {
        assertThat(covertToCanonicalCro("123456/99A")).isEqualTo("123456/99a");
        assertThat(covertToCanonicalCro("SF99/123456A")).isEqualTo("sf99/123456a");
    }

    @Test
    public void findsTermsThatLookLikeCroNumbers() {
        assertThat(termsThatLookLikeCroNumbers("john smith 123456/99Z SF94/123456A")).
            containsExactly("123456/99z", "sf94/123456a");
    }
}