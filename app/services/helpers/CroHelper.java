package services.helpers;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * CRO: Criminal Record Office reference
 */
public interface CroHelper {

    static boolean canBeConvertedToACro(String term) {
        return canBeConvertedToAFullCro(term) ||
               canBeConvertedToASearchFileCro(term);
    }

    static String covertToCanonicalCro(String croNumber) {
        return croNumber.toLowerCase();
    }

    // e.g. 123456/08A
    static boolean canBeConvertedToAFullCro(String term) {
        return term.matches("^[0-9]{1,6}/[0-9]{2}[a-zA-Z]");
    }

    // e.g. SF93/123456A
    static boolean canBeConvertedToASearchFileCro(String term) {
        return term.matches("^(sf|SF)[0-9]{2}/[0-9]{1,6}[a-zA-Z]");
    }

    static List<String> termsThatLookLikeCroNumbers(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(CroHelper::canBeConvertedToACro)
            .map(CroHelper::covertToCanonicalCro)
            .collect(toList());
    }
}
