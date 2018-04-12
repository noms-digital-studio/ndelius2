package helpers;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Base64;

public class JwtHelper {

    @Data
    @NoArgsConstructor
    public static class User {
        private String sub;
    }
    public static String principal(String bearerToken) {
        val jwtParts = bearerToken.split("\\.");
        if (hasNoBody(jwtParts)) {
            return "unknown";
        }
        val body = new String(Base64.getDecoder().decode(jwtParts[1]));
        return JsonHelper.readValue(body, User.class).getSub();
    }

    private static boolean hasNoBody(String[] jwtParts) {
        return jwtParts.length < 2;
    }
}
