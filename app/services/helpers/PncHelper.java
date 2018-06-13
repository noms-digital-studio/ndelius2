package services.helpers;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * PNC: Police National Computer reference
 */
public interface PncHelper {

    static boolean canBeConvertedToAPnc(String term) {
        return term.matches("^([0-9]{2}|[0-9]{4})/[0-9]+[a-zA-Z]");
    }

    static String covertToCanonicalPnc(String pncNumber) {
        String pnc = pncNumber.substring(0, pncNumber.lastIndexOf('/') + 1) +
            Integer.parseInt(pncNumber.substring(pncNumber.lastIndexOf('/') + 1, pncNumber.length() - 1)) +
            pncNumber.substring(pncNumber.length() - 1);
        return pnc.toLowerCase();
    }

    static List<String> termsThatLookLikePncNumbers(String searchTerm) {
        return Stream.of(searchTerm.split(" "))
            .filter(PncHelper::canBeConvertedToAPnc)
            .map(PncHelper::covertToCanonicalPnc)
            .collect(toList());
    }
}
