package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import org.junit.Test;
import static org.junit.Assert.*;

public class LSBEncoderTest {

    // Test case to encode a message into a bitmap
    @Test
    public void testEncodeMessage() {
        // Create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";

        // Attempt to encode the message into the bitmap
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);

        // Ensure that the encoding operation returns a non-null bitmap
        assertNotNull(encodedBitmap);
    }

    // Test case to ensure encoding fails when message exceeds capacity
    @Test
    public void testEncodeMessageWithInsufficientCapacity() {
        // Create a small bitmap
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        String message = "This is too long message for this small image";
        String password = "password";

        // Attempt to encode the message into the bitmap
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);

        // Ensure that the encoding operation returns null due to insufficient capacity
        assertNull(encodedBitmap);
    }
}
