package com.example.fyp;

import org.junit.Test;
import static org.junit.Assert.*;


public class AESUtilTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        String input = "Hello World";
        String password = "strongpassword";

        String encrypted = AESUtil.encrypt(input, password);
        assertNotNull(encrypted);

        String decrypted = AESUtil.decrypt(encrypted, password);
        assertEquals(input, decrypted);
    }

    @Test(expected = Exception.class)
    public void testDecryptWithWrongPassword() throws Exception {
        String input = "Hello World";
        String password = "strongpassword";
        String wrongPassword = "weakpassword";

        String encrypted = AESUtil.encrypt(input, password);
        AESUtil.decrypt(encrypted, wrongPassword);
    }
}
