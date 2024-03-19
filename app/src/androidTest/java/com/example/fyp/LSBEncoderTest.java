package com.example.fyp;

import org.junit.Test;
import static org.junit.Assert.*;
import android.graphics.Bitmap;
import android.graphics.Color;


public class LSBEncoderTest {

    @Test
    public void testBasicEncoding() {
        Bitmap coverImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        String message = "Hello World";
        Bitmap stegoImage = LSBEncoder.encodeMessage(coverImage, message);

        assertNotNull("StegoImage should not be null after encoding", stegoImage);
    }

    @Test
    public void testEncodeMessage() {
        // Create a mock Bitmap (e.g., 100x100 pixels)
        Bitmap mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        mockBitmap.eraseColor(Color.BLACK);

        String testMessage = "Hello, world!";
        Bitmap encodedBitmap = LSBEncoder.encodeMessage(mockBitmap, testMessage);

        assertNotNull(encodedBitmap);
    }

    @Test
    public void testMessageTooLong() {
        Bitmap coverImage = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888); // Small image for test
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) { // Generating a message longer than the image can handle
            longMessage.append("a");
        }
        Bitmap stegoImage = LSBEncoder.encodeMessage(coverImage, longMessage.toString());
        assertNull("StegoImage should be null when encoding a too long message", stegoImage);
    }

    @Test
    public void testSpecialCharactersHandling() {
        Bitmap coverImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        String message = "Hello, World! \n\t Special Characters: ñ @ # $ % ^ & * ( ) _ +";
        Bitmap stegoImage = LSBEncoder.encodeMessage(coverImage, message);
        assertNotNull("StegoImage should not be null after encoding", stegoImage);

        // Assuming LSBDecoder.decodeMessage(Bitmap) is available and correctly implemented
        String decodedMessage = LSBDecoder.decodeMessage(stegoImage);
        assertEquals("Decoded message should match the original", message, decodedMessage);
    }

}
