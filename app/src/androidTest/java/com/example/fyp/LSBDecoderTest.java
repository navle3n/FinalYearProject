package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import org.junit.Test;
import static org.junit.Assert.*;
import android.util.Log;

public class LSBDecoderTest {

    private static final String TAG = "LSBDecoderTest";

    @Test
    public void testDecodeMessage() {
        Log.d(TAG, "Starting testDecodeMessage");
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";

        Log.d(TAG, "Encoding message");
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNotNull(encodedBitmap);

        Log.d(TAG, "Decoding message");
        String decodedMessage = LSBDecoder.decodeMessage(encodedBitmap, password);
        assertNotNull(decodedMessage);
    }

    @Test
    public void testDecodeMessageWithWrongPassword() {
        Log.d(TAG, "Starting testDecodeMessageWithWrongPassword");
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";
        String wrongPassword = "wrongpassword";

        Log.d(TAG, "Encoding message with correct password");
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNotNull(encodedBitmap);

        Log.d(TAG, "Decoding message with wrong password");
        String decodedMessage = LSBDecoder.decodeMessage(encodedBitmap, wrongPassword);
        assertNotEquals(message, decodedMessage);
    }
}
