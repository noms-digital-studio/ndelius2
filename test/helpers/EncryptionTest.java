package helpers;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionTest {

    @Test
    public void testEncryptionAndDecryption() {

        val plainText = "Some Plain Text";
        val secretKey = "ThisIsASecretKey";

        val encrypted = Encryption.encrypt(plainText, secretKey);
        val decrypted = Encryption.decrypt(encrypted, secretKey);

        assertEquals(plainText, decrypted);
        assertNotEquals(plainText, encrypted);
    }
}
