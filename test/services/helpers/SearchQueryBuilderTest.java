package services.helpers;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchQueryBuilderTest {

    @Test
    public void termsThatLookLikeDatesAreExtractedAndNormalised() {
        List<String> dateTerms = SearchQueryBuilder.termsThatLookLikeDates("sna 28/02/2018 foo 2017-Jun-3 bar");
        assertThat(dateTerms).containsExactly("2018-02-28", "2017-06-03");
    }

    @Test
    public void termsThatLookLikeDatesReturnsEmptyArray() {
        List<String> dateTerms = SearchQueryBuilder.termsThatLookLikeDates("sna foo bar");
        assertThat(dateTerms).isEmpty();
    }

    @Test
    public void termsWithoutDatesAreExtracted() {
        String terms = SearchQueryBuilder.termsWithoutDates("sna 28/02/2018 foo 2017-Jun-3 bar");
        assertThat(terms).isEqualTo("sna foo bar");
    }

    @Test
    public void termsWithoutDatesReturnsEmptyString() {
        String terms = SearchQueryBuilder.termsWithoutDates("28/02/2018 2017-Jun-3");
        assertThat(terms).isEqualTo("");
    }

    @Test
    public void termsWithoutSingleLettersAreExtracted() {
        String terms = SearchQueryBuilder.termsWithoutSingleLetters("a sna 28/02/2018 b foo 2017-Jun-3 bar c");
        assertThat(terms).isEqualTo("sna 28/02/2018 foo 2017-Jun-3 bar");
    }

    @Test
    public void termsWithoutSingleLettersReturnsEmptyString() {
        String terms = SearchQueryBuilder.termsWithoutSingleLetters("a b c");
        assertThat(terms).isEqualTo("");
    }

    @Test
    public void aValidPageNumberFor() {
        assertThat(SearchQueryBuilder.aValidPageNumberFor(1)).isEqualTo(0);
        assertThat(SearchQueryBuilder.aValidPageNumberFor(3)).isEqualTo(2);
        assertThat(SearchQueryBuilder.aValidPageNumberFor(0)).isEqualTo(0);
    }
}