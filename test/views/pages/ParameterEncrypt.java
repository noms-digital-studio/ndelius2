package views.pages;

import helpers.Encryption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ParameterEncrypt {
    private static String secretKey = "ThisIsASecretKey";

    private ParameterEncrypt() {
        // util
    }

    public static String encrypt(String value) {
        try {
            return URLEncoder.encode(Encryption.encrypt(value, secretKey).orElseThrow(() -> new RuntimeException("Encrypt failed")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
