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

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String encrypt(String data, String password) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        SecretKeySpec key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(salt, 0, 16)));
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedDataWithSalt = new byte[salt.length + encryptedData.length];
        System.arraycopy(salt, 0, encryptedDataWithSalt, 0, salt.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithSalt, salt.length, encryptedData.length);

        return Base64.encodeToString(encryptedDataWithSalt, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedDataWithSalt, String password) throws Exception {
        byte[] decodedData = Base64.decode(encryptedDataWithSalt, Base64.DEFAULT);

        byte[] salt = Arrays.copyOfRange(decodedData, 0, SALT_LENGTH);
        byte[] encryptedData = Arrays.copyOfRange(decodedData, SALT_LENGTH, decodedData.length);

        SecretKeySpec key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Arrays.copyOfRange(salt, 0, 16)));
        byte[] originalData = cipher.doFinal(encryptedData);

        return new String(originalData);
    }

    private static SecretKeySpec generateKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] key = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }
}
