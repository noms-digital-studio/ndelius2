package services.helpers;

import helpers.DateTimeHelper;
import lombok.val;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import play.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static helpers.FluentHelper.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.suggest.SuggestBuilders.termSuggestion;

public class SearchQueryBuilder {

    private static final int MAX_PROVIDERS_COUNT = 1000;

    public static SearchSourceBuilder searchSourceFor(String searchTerm, List<String> probationAreasCodes, int pageSize, int pageNumber) {

        val simpleTerms = simpleTerms(searchTerm);
        val boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().add(multiMatchQuery(simpleTerms)
            .field("firstName", 10)
            .field("surname", 10)
            .field("middleNames", 8)
            .field("offenderAliases.firstName", 8)
            .field("offenderAliases.surname", 8)
            .field("contactDetails.addresses.town")
            .type(CROSS_FIELDS));

        boolQueryBuilder.should().add(multiMatchQuery(simpleTerms)
            .field("gender")
            .field("otherIds.crn", 10)
            .field("otherIds.nomsNumber", 10)
            .field("otherIds.niNumber", 10)
            .field("contactDetails.addresses.streetName")
            .field("contactDetails.addresses.county")
            .field("contactDetails.addresses.postcode", 10)
            .type(MOST_FIELDS));

        boolQueryBuilder.should().add(multiMatchQuery(searchTerm.toLowerCase())
            .field("otherIds.croNumberLowercase", 10)
            .analyzer("whitespace"));

        termsThatLookLikePncNumbers(searchTerm).forEach(pnc ->
            boolQueryBuilder.should().add(multiMatchQuery(pnc)
                .field("otherIds.pncNumberLongYear", 10)
                .field("otherIds.pncNumberShortYear", 10)
                .analyzer("whitespace")));

        termsThatLookLikeDates(searchTerm).forEach(dateTerm ->
            boolQueryBuilder.should().add(multiMatchQuery(dateTerm)
                .field("dateOfBirth", 11)
                .lenient(true)));

        Stream.of(simpleTermsIncludingSingleLetters(searchTerm).split(" "))
            .filter(not(String::isEmpty))
            .forEach(term -> boolQueryBuilder.should().add(prefixQuery("firstName", term.toLowerCase()).boost(11)));

        val highlight = new HighlightBuilder().
            highlighterType("unified").
            field("*").
            preTags("").
            postTags("");

        boolQueryBuilder.mustNot(termQuery("softDeleted", true));

        val searchSource = new SearchSourceBuilder()
            .query(boolQueryBuilder)
            .highlighter(highlight)
            .explain(Logger.isDebugEnabled())
            .size(pageSize)
            .from(pageSize * aValidPageNumberFor(pageNumber))
            .suggest(suggestionsFor(searchTerm));

        val activeAreaFilter = QueryBuilders.boolQuery();
        activeAreaFilter.must().add(termQuery("offenderManagers.active", true));

        searchSource.aggregation(
                AggregationBuilders
                        .nested("offenderManagers", "offenderManagers")
                        .subAggregation(AggregationBuilders
                                .terms("active")
                                .field("offenderManagers.active").subAggregation(
                                    AggregationBuilders
                                            .terms("byProbationAreaCode").size(MAX_PROVIDERS_COUNT)
                                            .field("offenderManagers.probationArea.code")
                                )
                        )
        );

        if (!probationAreasCodes.isEmpty()) {

            val probationAreaFilter = QueryBuilders.boolQuery();

            probationAreasCodes.stream().map(probationAreaCode -> {
                val probationAreaCodeFilter = QueryBuilders.boolQuery();
                probationAreaCodeFilter.must().add(termQuery("offenderManagers.probationArea.code", probationAreaCode));
                probationAreaCodeFilter.must().add(termQuery("offenderManagers.active", true));
                return nestedQuery("offenderManagers", probationAreaCodeFilter, ScoreMode.None).ignoreUnmapped(true);
            }).forEach(probationAreaFilter::should);

            searchSource.postFilter(probationAreaFilter);
        }

        Logger.debug(searchSource.toString());
        return searchSource;
    }

    private static List<String> termsThatLookLikePncNumbers(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(PncHelper::canBeConvertedToAPnc)
            .map(PncHelper::covertToCanonicalPnc)
            .collect(toList());
    }

    public static List<String> termsThatLookLikeDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .map(DateTimeHelper::covertToCanonicalDate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    }

    static String simpleTerms(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> term.length() > 1)
            .filter(not(DateTimeHelper::canBeConvertedToADate))
            .filter(not(term -> term.contains("/")))
            .map(String::toLowerCase)
            .collect(joining(" "));
    }

    static String simpleTermsIncludingSingleLetters(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(not(DateTimeHelper::canBeConvertedToADate))
            .filter(not(term -> term.contains("/")))
            .map(String::toLowerCase)
            .collect(joining(" "));
    }

    public static int aValidPageNumberFor(int pageNumber) {
        return pageNumber >= 1 ? pageNumber - 1 : 0;
    }

    private static SuggestBuilder suggestionsFor(String searchTerm) {
        return new SuggestBuilder()
            .addSuggestion("surname", termSuggestion("surname").text(searchTerm))
            .addSuggestion("firstName", termSuggestion("firstName").text(searchTerm));
    }
}
