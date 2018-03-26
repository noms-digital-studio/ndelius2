package helpers;

public class PncHelper {

    public static boolean canBeConvertedToAPnc(String term) {
        return term.matches("^([0-9]{2}|[0-9]{4})/[0-9]+[a-zA-Z]");
    }

    public static String covertToCanonicalPnc(String pncNumber) {
        String pnc = pncNumber.substring(0, pncNumber.lastIndexOf('/') + 1) +
            Integer.parseInt(pncNumber.substring(pncNumber.lastIndexOf('/') + 1, pncNumber.length() - 1)) +
            pncNumber.substring(pncNumber.length() - 1);
        return pnc.toLowerCase();
    }
}
