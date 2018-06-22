package helpers;

import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public interface Encryption {

    // Example usage:
    // val encrypted = Encryption.encrypt("Some Plain Text", "ThisIsASecretKey");
    // val plainText = Encryption.decrypt(encrypted, "ThisIsASecretKey");

    static Optional<String> encrypt(String plainText, String secret) {

        try {

            val cipher = cipherFromSecret(Cipher.ENCRYPT_MODE, secret);

            return cipher != null && plainText != null ? Optional.of(Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()))) : Optional.empty();

        } catch (IllegalBlockSizeException | BadPaddingException ex) {

            return Optional.empty();
        }
    }

    static Optional<String> decrypt(String encrypted, String secret) {

        try {
            val cipher = cipherFromSecret(Cipher.DECRYPT_MODE, secret);

            return cipher != null && encrypted != null ? Optional.of(new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)))) : Optional.empty();

        } catch (IllegalBlockSizeException | BadPaddingException ex) {

            return Optional.empty();
        }
    }

    static Cipher cipherFromSecret(int initMode, String secret)  {

        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(initMode, keyFromSecret(secret));

            return cipher;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {

            return null;
        }
    }

    static Key keyFromSecret(String secret) {

        try {
            val digest = MessageDigest.getInstance("SHA-1").digest(secret.getBytes());

            return new SecretKeySpec(Arrays.copyOf(digest, 16), "AES");

        } catch (NoSuchAlgorithmException ex) {

            return null;
        }
    }
}
