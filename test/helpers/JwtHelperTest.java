package helpers;

import org.junit.Test;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtHelperTest {

    @Test
    public void principalIsExtractedFromJwtSubject() {

        assertThat(JwtHelper.principal(
                generateToken("{\"sub\":\"cn=fake.user,ou=Users,dc=moj,dc=com\",\"uid\":\"fake.user\",\"exp\":1517631939}")))
                .isEqualTo("cn=fake.user,ou=Users,dc=moj,dc=com");
    }

    @Test
    public void probationAreasAreExtractedFromprobationAreaCodes() {

        assertThat(JwtHelper.probationAreaCodes(
                generateToken("{\"sub\":\"cn=fake.user,ou=Users,dc=moj,dc=com\",\"uid\":\"fake.user\",\"exp\":1517631939, \"probationAreaCodes\": [\"N01\", \"N02\"]}")))
                .containsExactly("N01", "N02");
    }

    private static String generateToken(String body) {
        return String.format("eyJhbGciOiJIUzUxMiJ9.%s.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ", Base64.getEncoder().encodeToString(body.getBytes()));
    }

    public static String generateToken() {
        return generateTokenWithSubject("cn=fake.user,ou=Users,dc=moj,dc=com");
    }
    public static String generateTokenWithProbationAreaCodes(List<String> probationAreaCodes) {
        return generateToken(String.format("{\"sub\":\"cn=fake.user,ou=Users,dc=moj,dc=com\",\"uid\":\"fake.user\",\"exp\":1517631939, \"probationAreaCodes\": %s}", JsonHelper.stringify(probationAreaCodes)));
    }
    public static String generateTokenWithSubject(String subject) {
        return generateToken(String.format("{\"sub\":\"%s\",\"uid\":\"fake.user\",\"exp\":1517631939, \"probationAreaCodes\": [\"N01\", \"N02\"]}", subject));
    }
    public static String generateTokenWithUsername(String username) {
        return generateToken(String.format("{\"sub\":\"cn=%s,ou=Users,dc=moj,dc=com\",\"uid\":\"%s\",\"exp\":1517631939, \"probationAreaCodes\": [\"N01\", \"N02\"]}", username, username));
    }
}
