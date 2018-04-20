package services.helpers;

import lombok.val;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static services.helpers.SearchQueryBuilder.simpleTerms;
import static services.helpers.SearchQueryBuilder.simpleTermsIncludingSingleLetters;

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
    public void simpleTermsAreLowercase_and_doNotInclude_singleLetters_dates_termsWithSlashes() {
        String terms = simpleTerms("a SNA 28/02/2018 b foo SF69/ABC 2017-Jun-3 bar c");
        assertThat(terms).isEqualTo("sna foo bar");
    }

    @Test
    public void simpleTermsAreLowercase_returnsEmptyString_whenThereAreNoSimpleTerms() {
        String terms = simpleTerms("a b c 28/02/2018 SF69/ABC");
        assertThat(terms).isEqualTo("");
    }

    @Test
    public void simpleTermsIncludingSingleLettersAreLowercase_and_doNotInclude_singleLetters_dates_termsWithSlashes() {
        assertThat(simpleTermsIncludingSingleLetters("a SNA 28/02/2018 b foo SF69/ABC 2017-Jun-3 bar c"))
            .isEqualTo("a sna b foo bar c");
    }

    @Test
    public void simpleTermsIncludingSingleLetters_returnsEmptyString_whenThereAreNoSimpleTermsOrSingleLetters() {
        assertThat(simpleTermsIncludingSingleLetters("28/02/2018 SF69/ABC"))
            .isEqualTo("");
    }

    @Test
    public void aValidPageNumberFor() {
        assertThat(SearchQueryBuilder.aValidPageNumberFor(1)).isEqualTo(0);
        assertThat(SearchQueryBuilder.aValidPageNumberFor(3)).isEqualTo(2);
        assertThat(SearchQueryBuilder.aValidPageNumberFor(0)).isEqualTo(0);
    }

    @Test
    public void searchSourceBuilderHasCorrectQueries() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor("2013/0234567A 15-09-1970 a smith 1/2/1992", 10, 3);

        val query = (BoolQueryBuilder) builder.query();
        val queryBuilder1 = (MultiMatchQueryBuilder)query.should().get(0);
        assertThat(queryBuilder1.value()).isEqualTo("smith");
        assertThat(queryBuilder1.fields()).containsOnlyKeys(
            "firstName",
            "surname",
            "middleNames",
            "offenderAliases.firstName",
            "offenderAliases.surname",
            "contactDetails.addresses.town");

        val queryBuilder2 = (MultiMatchQueryBuilder)query.should().get(1);
        assertThat(queryBuilder2.value()).isEqualTo("smith");
        assertThat(queryBuilder2.fields()).containsOnlyKeys(
            "gender",
            "otherIds.crn",
            "otherIds.nomsNumber",
            "otherIds.niNumber",
            "contactDetails.addresses.streetName",
            "contactDetails.addresses.county",
            "contactDetails.addresses.postcode");

        val queryBuilder3 = (MultiMatchQueryBuilder)query.should().get(2);
        assertThat(queryBuilder3.value()).isEqualTo("2013/0234567a 15-09-1970 a smith 1/2/1992");
        assertThat(queryBuilder3.fields()).containsOnlyKeys(
            "otherIds.croNumberLowercase");

        val queryBuilder4 = (MultiMatchQueryBuilder)query.should().get(3);
        assertThat(queryBuilder4.value()).isEqualTo("2013/234567a");
        assertThat(queryBuilder4.fields()).containsOnlyKeys(
            "otherIds.pncNumberLongYear",
            "otherIds.pncNumberShortYear");

        assertThat(((MultiMatchQueryBuilder)query.should().get(4)).value()).isEqualTo("1970-09-15");

        assertThat(((MultiMatchQueryBuilder)query.should().get(5)).value()).isEqualTo("1992-02-01");

        assertThat(((PrefixQueryBuilder)query.should().get(6)).value()).isEqualTo("a");

        assertThat(((PrefixQueryBuilder)query.should().get(7)).value()).isEqualTo("smith");

        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) builder.postFilter();
        assertThat(termQueryBuilder.fieldName()).isEqualTo("softDeleted");
        assertThat(termQueryBuilder.value()).isEqualTo(false);

        assertThat(builder.suggest().getSuggestions().keySet()).containsOnly("firstName", "surname");
    }

    @Test
    public void dateOnlySearchDoesNotAddAPrefixSearch() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor("15-09-1970", 10, 3);

        val query = (BoolQueryBuilder) builder.query();
        assertThat((query.should())).doesNotContain(prefixQuery("firstName", "").boost(11));
    }

    @Test
    public void unifiedHighlighterIsRequested() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor("15-09-1970 a smith 1/2/1992", 10, 3);

        assertThat(builder.query()).isInstanceOfAny(BoolQueryBuilder.class);

        val highlighter = builder.highlighter();
        assertThat(highlighter.highlighterType()).isEqualTo("unified");
    }

}