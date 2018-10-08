package services.helpers;

import com.google.common.collect.ImmutableList;
import lombok.val;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.MUST;
import static services.helpers.SearchQueryBuilder.QUERY_TYPE.SHOULD;
import static services.helpers.SearchQueryBuilder.simpleTerms;
import static services.helpers.SearchQueryBuilder.simpleTermsIncludingSingleLetters;

public class SearchQueryBuilderTest {

    @Test
    public void simpleTermsAreLowercase_and_doNotInclude_dates_termsWithSlashes() {
        String terms = simpleTerms("a SNA 28/02/2018 b foo SF69/ABC 2017-Jun-3 bar c");
        assertThat(terms).isEqualTo("a sna b foo bar c");
    }

    @Test
    public void simpleTermsAreLowercase_returnsEmptyString_whenThereAreNoSimpleTerms() {
        String terms = simpleTerms("28/02/2018 SF69/ABC");
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
    public void searchSourceBuilderHasCorrectQueries_forShouldTypeSearches() {
        SearchSourceBuilder builder =
            SearchQueryBuilder.searchSourceFor(
                "2013/0234567A 15-09-1970 a smith 1/2/1992",
                emptyList(),
                10,
                3,
                SHOULD);

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
        assertThat(queryBuilder2.value()).isEqualTo("a smith");
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

        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) query.mustNot().get(0);
        assertThat(termQueryBuilder.fieldName()).isEqualTo("softDeleted");
        assertThat(termQueryBuilder.value()).isEqualTo(true);

        assertThat(builder.suggest().getSuggestions().keySet()).containsOnly("firstName", "surname");
    }

    @Test
    public void searchSourceBuilderHasCorrectQueries_forMustTypeSearches() {
        SearchSourceBuilder builder =
            SearchQueryBuilder.searchSourceFor(
                "123456/15D 2013/0234567A 15-09-1970 smith 1/2/1992",
                emptyList(),
                10,
                3,
                MUST);

        val query = (BoolQueryBuilder) builder.query();
        val queryBuilder1 = (MultiMatchQueryBuilder)query.must().get(0);
        assertThat(queryBuilder1.value()).isEqualTo("smith");
        assertThat(queryBuilder1.fields()).containsOnlyKeys(
            "firstName",
            "surname",
            "middleNames",
            "offenderAliases.firstName",
            "offenderAliases.surname",
            "contactDetails.addresses.town",
            "gender",
            "contactDetails.addresses.streetName",
            "contactDetails.addresses.county",
            "contactDetails.addresses.postcode",
            "otherIds.crn",
            "otherIds.nomsNumber",
            "otherIds.niNumber");

        val queryBuilder2 = (MultiMatchQueryBuilder)query.must().get(1);
        assertThat(queryBuilder2.value()).isEqualTo("123456/15d");
        assertThat(queryBuilder2.fields()).containsOnlyKeys(
            "otherIds.croNumberLowercase");

        val queryBuilder4 = (MultiMatchQueryBuilder)query.must().get(2);
        assertThat(queryBuilder4.value()).isEqualTo("2013/234567a");
        assertThat(queryBuilder4.fields()).containsOnlyKeys(
            "otherIds.pncNumberLongYear",
            "otherIds.pncNumberShortYear");

        assertThat(((MultiMatchQueryBuilder)query.must().get(3)).value()).isEqualTo("1970-09-15");

        assertThat(((MultiMatchQueryBuilder)query.must().get(4)).value()).isEqualTo("1992-02-01");

        TermQueryBuilder termQueryBuilder = (TermQueryBuilder) query.mustNot().get(0);
        assertThat(termQueryBuilder.fieldName()).isEqualTo("softDeleted");
        assertThat(termQueryBuilder.value()).isEqualTo(true);

        assertThat(builder.suggest().getSuggestions().keySet()).containsOnly("firstName", "surname");
    }

    @Test
    public void emptyProbationAreaFilterWillNotAddAPostFilter() {
        val builder = SearchQueryBuilder.searchSourceFor(
            "smith",
            emptyList(),
            10,
            1,
            SHOULD);

        assertThat(builder.postFilter()).isNull();
    }

    @Test
    public void eachProbationAreaFilterCodeIsAddedToPostFilter() {
        val probationAreasFilter = ImmutableList.of("N01", "N02", "N03");
        val builder = SearchQueryBuilder.searchSourceFor(
            "smith",
            probationAreasFilter,
            10,
            1,
            SHOULD);

        assertThat(builder.postFilter()).isNotNull();
        val query = (BoolQueryBuilder) builder.postFilter();

        assertThat(query.should().size()).isEqualTo(3);

        probationAreasFilter.forEach(probationAreaCode -> {
            val index = probationAreasFilter.indexOf(probationAreaCode);
            val mustQuery = ((BoolQueryBuilder) ((NestedQueryBuilder) query.should().get(index)).query()).must();
            assertThat(mustQuery.size()).isEqualTo(2);
            assertThat(((TermQueryBuilder)mustQuery.get(0)).fieldName()).isEqualTo("offenderManagers.probationArea.code");
            assertThat(((TermQueryBuilder)mustQuery.get(0)).value()).isEqualTo(probationAreaCode);

            assertThat(((TermQueryBuilder)mustQuery.get(1)).fieldName()).isEqualTo("offenderManagers.active");
            assertThat(((TermQueryBuilder)mustQuery.get(1)).value()).isEqualTo(true);
        });
    }

    @Test
    public void dateOnlySearchDoesNotAddAPrefixSearch() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor(
            "15-09-1970",
            emptyList(),
            10,
            3,
            SHOULD);

        val query = (BoolQueryBuilder) builder.query();
        assertThat((query.should())).doesNotContain(prefixQuery("firstName", "").boost(11));
    }

    @Test
    public void unifiedHighlighterIsRequested() {
        SearchSourceBuilder builder = SearchQueryBuilder.searchSourceFor(
            "15-09-1970 a smith 1/2/1992",
            emptyList(),
            10,
            3,
            SHOULD);

        assertThat(builder.query()).isInstanceOfAny(BoolQueryBuilder.class);

        val highlighter = builder.highlighter();
        assertThat(highlighter.highlighterType()).isEqualTo("unified");
    }

}