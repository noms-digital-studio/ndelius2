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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.MOST_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.suggest.SuggestBuilders.termSuggestion;

public class SearchQueryBuilder {
    public static SearchSourceBuilder searchSourceFor(String searchTerm, int pageSize, int pageNumber) {
        val boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutDates(searchTerm))
            .field("firstName", 10)
            .field("surname", 10)
            .field("middleNames", 8)
            .field("offenderAliases.firstName", 8)
            .field("offenderAliases.surname", 8)
            .field("contactDetails.addresses.town")
            // CROSS_FIELDS Analyzes the query string into individual terms, then looks
            // for each term in any of the fields, as though they were one big field
            .type(CROSS_FIELDS));

        boolQueryBuilder.should().add(multiMatchQuery(termsWithoutSingleLetters(termsWithoutDates(searchTerm)))
            .field("gender")
            .field("otherIds.crn", 10)
            .field("otherIds.nomsNumber", 10)
            .field("otherIds.niNumber", 10)
            .field("otherIds.pncNumber", 10)
            .field("otherIds.croNumber", 10)
            .field("contactDetails.addresses.streetName")
            .field("contactDetails.addresses.county")
            .field("contactDetails.addresses.postcode", 10)
            // MOST_FIELDS Finds documents which match any field and combines the _score from each field
            .type(MOST_FIELDS));

        termsThatLookLikeDates(searchTerm).forEach(dateTerm ->
            boolQueryBuilder.should().add(multiMatchQuery(dateTerm)
                .field("dateOfBirth", 11)
                .lenient(true)));

        Stream.of(termsWithoutDates(searchTerm).split(" "))
            .filter(term -> !term.isEmpty())
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

    public static List<String> termsThatLookLikeDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(DateTimeHelper::canBeConvertedToADate)
            .map(term -> DateTimeHelper.covertToCanonicalDate(term).get())
            .collect(toList());
    }

    public static String termsWithoutDates(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> !DateTimeHelper.covertToCanonicalDate(term).isPresent())
            .collect(joining(" "));
    }

    public static String termsWithoutSingleLetters(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(term -> term.length() > 1)
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
