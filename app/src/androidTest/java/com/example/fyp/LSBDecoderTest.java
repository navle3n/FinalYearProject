package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.example.fyp.LSBDecoder;

public class LSBDecoderTest {

    @Test
    public void testDecodeMessage_Success() {
        // Mock bitmap with encoded message "Hello, world!"
        Bitmap mockBitmap = createMockBitmap("Hello, world!");

        String decodedMessage = LSBDecoder.decodeMessage(mockBitmap);

        assertEquals("Hello, world!", decodedMessage);
    }

    @Test
    public void testDecodeMessage_EmptyBitmap() {
        Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        String decodedMessage = LSBDecoder.decodeMessage(emptyBitmap);

        assertEquals("Error: Failed to extract message.", decodedMessage);
    }

    @Test
    public void testDecodeMessage_NullBitmap() {
        String decodedMessage = LSBDecoder.decodeMessage(null);

        assertEquals("Error: Bitmap is null.", decodedMessage);
    }

    private Bitmap createMockBitmap(String message) {
        // Calculate the total number of bits to encode (message length in bits + 32 for message length encoding)
        int totalBits = message.length() * 8 + 32;

        // Calculate the total number of pixels needed, assuming 3 bits per pixel (one bit per color channel)
        int totalPixels = (int) Math.ceil(totalBits / 3.0);

        // Ensure the bitmap width is enough to hold all pixels in a single row
        int bitmapWidth = totalPixels;

        // Create an array of pixels to hold the encoded message and message length
        int[] pixels = new int[totalPixels];

        // Encode message length in the first 32 bits
        String binaryMessageLength = String.format("%32s", Integer.toBinaryString(message.length())).replace(' ', '0');
        for (int i = 0; i < 32; i++) {
            int colorValue = Character.getNumericValue(binaryMessageLength.charAt(i));
            pixels[i] = Color.rgb(colorValue, colorValue, colorValue); // Using grayscale for simplicity
        }

        // Encode message in the rest of the pixels
        for (int i = 0; i < message.length(); i++) {
            char character = message.charAt(i);
            String binaryChar = String.format("%8s", Integer.toBinaryString(character)).replace(' ', '0');
            for (int j = 0; j < 8; j++) {
                int bitIndex = 32 + i * 8 + j; // Adjust index to start after message length bits
                int pixelIndex = bitIndex / 3; // Calculate pixel index for this bit
                int colorValue = Character.getNumericValue(binaryChar.charAt(j));
                // Set or merge the bit into the existing pixel value to preserve previous bits
                pixels[pixelIndex] = mergeBitIntoPixel(pixels[pixelIndex], colorValue, bitIndex % 3);
            }
        }

        // Create and return the bitmap from the pixels array
        return Bitmap.createBitmap(pixels, bitmapWidth, 1, Bitmap.Config.ARGB_8888);
    }

    // Helper method to merge a single bit into one of the RGB channels of a pixel
    private int mergeBitIntoPixel(int currentPixel, int bit, int position) {
        int[] rgb = {Color.red(currentPixel), Color.green(currentPixel), Color.blue(currentPixel)};
        rgb[position] = (rgb[position] & 0xFE) | bit; // Merge bit into the correct color channel
        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

}
