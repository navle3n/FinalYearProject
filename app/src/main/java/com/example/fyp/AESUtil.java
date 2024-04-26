package com.example.fyp;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import android.util.Base64;

public class AESUtil {

    // Constants used in the encryption and decryption processes.
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256"; // Algorithm used for generating key from a password.
    private static final int ITERATION_COUNT = 65536; // The number of PBKDF2 hardening rounds to use.
    private static final int KEY_LENGTH = 256; // Length of the generated key in bits.
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes.

    // Encrypts data using AES/CBC/PKCS5Padding algorithm.
    public static String encrypt(String data, String password) throws Exception {
        // Generate a random salt
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Generate the encryption key from the password and salt
        SecretKeySpec key = generateKey(password, salt);

        // Initialize the Cipher for encryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(salt, 0, 16)));
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Prepend salt to encrypted data
        byte[] encryptedDataWithSalt = new byte[salt.length + encryptedData.length];
        System.arraycopy(salt, 0, encryptedDataWithSalt, 0, salt.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithSalt, salt.length, encryptedData.length);

        // Return Base64 encoded string of salt + encrypted data
        return Base64.encodeToString(encryptedDataWithSalt, Base64.DEFAULT);
    }

    // Decrypts previously encrypted data using AES/CBC/PKCS5Padding algorithm.
    public static String decrypt(String encryptedDataWithSalt, String password) throws Exception {
        // Decode the Base64 string
        byte[] decodedData = Base64.decode(encryptedDataWithSalt, Base64.DEFAULT);

        // Extract the salt and the encrypted data
        byte[] salt = Arrays.copyOfRange(decodedData, 0, SALT_LENGTH);
        byte[] encryptedData = Arrays.copyOfRange(decodedData, SALT_LENGTH, decodedData.length);

        // Generate the decryption key from the password and salt
        SecretKeySpec key = generateKey(password, salt);

        // Initialize the Cipher for decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(salt, 0, 16)));
        byte[] originalData = cipher.doFinal(encryptedData);

        // Return the decrypted string
        return new String(originalData);
    }

    // Helper method to generate a SecretKeySpec from a password and salt.
    private static SecretKeySpec generateKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }
}
