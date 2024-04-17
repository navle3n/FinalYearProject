package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import org.junit.Test;
import static org.junit.Assert.*;


public class LSBEncoderTest {

    @Test
    public void testEncodeMessage() {
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Config.ARGB_8888);
        String message = "Secret";
        String password = "password";

        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNotNull(encodedBitmap);
    }

    @Test
    public void testEncodeMessageWithInsufficientCapacity() {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        String message = "This is too long message for this small image";
        String password = "password";

        Bitmap encodedBitmap = LSBEncoder.encodeMessage(bitmap, message, password);
        assertNull(encodedBitmap);
    }
}
