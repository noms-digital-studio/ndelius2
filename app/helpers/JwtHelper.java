package helpers;

import lombok.val;

import java.util.Base64;

public class JwtHelper {
    public static String principal(String bearerToken) {
        val jwtParts = bearerToken.split("\\.");
        if (hasNoBody(jwtParts)) {
            return "unknown";
        }
        val body = new String(Base64.getDecoder().decode(jwtParts[1]));
        return JsonHelper.jsonToMap(body).get("sub");
    }

    private static boolean hasNoBody(String[] jwtParts) {
        return jwtParts.length < 2;
    }
}
