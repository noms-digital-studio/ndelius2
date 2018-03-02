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

    @Test
    public void searchSourceBuilderHasCorrectQueries() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor("15-09-1970 a smith 1/2/1992", 10, 3);

        val query = (BoolQueryBuilder) builder.query();
        val queryBuilder1 = (MultiMatchQueryBuilder)query.should().get(0);
        assertThat(queryBuilder1.value()).isEqualTo("a smith");
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
            "otherIds.pncNumber",
            "otherIds.croNumber",
            "contactDetails.addresses.streetName",
            "contactDetails.addresses.county",
            "contactDetails.addresses.postcode");

        assertThat(((MultiMatchQueryBuilder)query.should().get(2)).value()).isEqualTo("1970-09-15");

        assertThat(((MultiMatchQueryBuilder)query.should().get(3)).value()).isEqualTo("1992-02-01");

        assertThat(((PrefixQueryBuilder)query.should().get(4)).value()).isEqualTo("a");

        assertThat(((PrefixQueryBuilder)query.should().get(5)).value()).isEqualTo("smith");

        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) builder.postFilter();
        assertThat(termQueryBuilder.fieldName()).isEqualTo("softDeleted");
        assertThat(termQueryBuilder.value()).isEqualTo(false);
    }

    @Test
    public void dateOnlySearchDoesNotAddAPrefixSearch() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor("15-09-1970", 10, 3);

        val query = (BoolQueryBuilder) builder.query();
        System.out.println(query.should());
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