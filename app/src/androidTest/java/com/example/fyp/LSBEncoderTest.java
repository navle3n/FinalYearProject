package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class LSBEncoderTest {

    @Test
    public void testEncodeMessage() {
        // Create a mock Bitmap (e.g., 100x100 pixels)
        Bitmap mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        mockBitmap.eraseColor(Color.BLACK);

        String testMessage = "Hello, world!";
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(mockBitmap, testMessage);

        assertNotNull(encodedBitmap);
    }
}
