package services.helpers;

import helpers.DateTimeHelper;
import lombok.val;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import play.Logger;

import java.util.List;
import java.util.stream.Stream;

import static helpers.DateTimeHelper.covertToCanonicalDate;
import static helpers.FluentHelper.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.suggest.SuggestBuilders.termSuggestion;

public class SearchQueryBuilder {
    public static SearchSourceBuilder searchSourceFor(String searchTerm, int pageSize, int pageNumber) {

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

        val searchSource = new SearchSourceBuilder()
            .query(boolQueryBuilder)
            .highlighter(highlight)
            .postFilter(termQuery("softDeleted", false))
            .explain(Logger.isDebugEnabled())
            .size(pageSize)
            .from(pageSize * aValidPageNumberFor(pageNumber))
            .suggest(suggestionsFor(searchTerm));

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
            .filter(DateTimeHelper::canBeConvertedToADate)
            .map(term -> covertToCanonicalDate(term).get())
            .collect(toList());
    }

    static String simpleTerms(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> term.length() > 1)
            .filter(not(term -> covertToCanonicalDate(term).isPresent()))
            .filter(not(term -> term.contains("/")))
            .map(String::toLowerCase)
            .collect(joining(" "));
    }

    static String simpleTermsIncludingSingleLetters(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(not(term -> covertToCanonicalDate(term).isPresent()))
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
