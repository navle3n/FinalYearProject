package com.example.fyp;

import org.junit.Test;
import static org.junit.Assert.*;

public class AESUtilTest {

    // Test case to ensure encryption and decryption work correctly
    @Test
    public void testEncryptDecrypt() throws Exception {
        String input = "Hello World";
        String password = "strongpassword";

        // Encrypt the input string
        String encrypted = AESUtil.encrypt(input, password);
        assertNotNull(encrypted);

        // Decrypt the encrypted string
        String decrypted = AESUtil.decrypt(encrypted, password);

        // Assert that decrypted string matches the original input
        assertEquals(input, decrypted);
    }

    // Test case to ensure decryption fails with wrong password
    @Test(expected = Exception.class)
    public void testDecryptWithWrongPassword() throws Exception {
        String input = "Hello World";
        String password = "strongpassword";
        String wrongPassword = "weakpassword";

        // Encrypt the input string with the correct password
        String encrypted = AESUtil.encrypt(input, password);

        // Attempt to decrypt with a wrong password, should throw an exception
        AESUtil.decrypt(encrypted, wrongPassword);
    }
}
