package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LSBEncoder {
    private static final int BITS_PER_CHAR = 8;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void encodeMessage(Bitmap bitmap, String message, File outputFile) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int messageLength = message.length();
        int charIndex = 0;

        FileOutputStream fos = new FileOutputStream(outputFile);

        // Encode message length into the first 32 bits of the image
        encodeLength(bitmap, messageLength);

        // Encode each character of the message into the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);

                // Get the RGB components
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Encode message into the least significant bits of RGB components
                if (charIndex < messageLength) {
                    char character = message.charAt(charIndex++);
                    red = encodeChar(red, character, BITS_PER_CHAR);
                    green = encodeChar(green, character, BITS_PER_CHAR);
                    blue = encodeChar(blue, character, BITS_PER_CHAR);
                }

                // Update the pixel color with the modified RGB components
                int encodedPixel = Color.rgb(red, green, blue);
                bitmap.setPixel(x, y, encodedPixel);
            }
        }

        // Write the encoded image to the output file
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }

    private static void encodeLength(Bitmap bitmap, int messageLength) {
        for (int i = 0; i < 32; i++) {
            int x = i % bitmap.getWidth();
            int y = i / bitmap.getWidth();
            int pixel = bitmap.getPixel(x, y);
            int red = Color.red(pixel);
            int bit = (messageLength >> i) & 1;
            red = (red & 0xFE) | bit;
            bitmap.setPixel(x, y, Color.rgb(red, Color.green(pixel), Color.blue(pixel)));
        }
    }

    private static int encodeChar(int colorComponent, char character, int bitsPerChar) {
        return (colorComponent & 0xFE) | ((character >> (bitsPerChar - 1)) & 1);
    }
}
