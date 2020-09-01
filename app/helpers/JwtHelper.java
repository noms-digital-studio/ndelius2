package helpers;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class JwtHelper {

    @Data
    @NoArgsConstructor
    public static class User {
        private String sub;
        private String uid;
        private List<String> probationAreaCodes;
    }
    public static String principal(String bearerToken) {
        return asUser(bearerToken).map(User::getSub).orElse("unknown");
    }

    public static String username(String bearerToken) {
        return asUser(bearerToken).map(User::getUid).orElse("unknown");
    }

    public static List<String> probationAreaCodes(String bearerToken) {
        return asUser(bearerToken).map(User::getProbationAreaCodes).orElse(emptyList());
    }

    private static Optional<User> asUser(String bearerToken) {
        val jwtParts = bearerToken.split("\\.");
        if (hasNoBody(jwtParts)) {
            return Optional.empty();
        }
        val body = new String(Base64.getDecoder().decode(jwtParts[1]));
        return Optional.of(JsonHelper.readValue(body, User.class));
    }

    private static boolean hasNoBody(String[] jwtParts) {
        return jwtParts.length < 2;
    }
}
