package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import org.junit.Test;
import static org.junit.Assert.*;
import android.util.Log;

public class LSBDecoderTest {

    private static final String TAG = "LSBDecoderTest";

    // Test case to decode a message from an encoded bitmap
    @Test
    public void testDecodeMessage() {
        Log.d(TAG, "Starting testDecodeMessage");

        // Create a bitmap to encode the message
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";

        // Encode the message into the bitmap
        Log.d(TAG, "Encoding message");
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNotNull(encodedBitmap);

        // Decode the message from the encoded bitmap
        Log.d(TAG, "Decoding message");
        String decodedMessage = LSBDecoder.decodeMessage(encodedBitmap, password);
        assertNotNull(decodedMessage);
    }

    // Test case to ensure decoding fails with wrong password
    @Test
    public void testDecodeMessageWithWrongPassword() {
        Log.d(TAG, "Starting testDecodeMessageWithWrongPassword");

        // Create a bitmap to encode the message
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";
        String wrongPassword = "wrongpassword";

        // Encode the message into the bitmap with correct password
        Log.d(TAG, "Encoding message with correct password");
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNotNull(encodedBitmap);

        // Attempt to decode with a wrong password, should not match the original message
        Log.d(TAG, "Decoding message with wrong password");
        String decodedMessage = LSBDecoder.decodeMessage(encodedBitmap, wrongPassword);
        assertNotEquals(message, decodedMessage);
    }
}
