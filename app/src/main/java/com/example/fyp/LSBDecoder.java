package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;

public class LSBDecoder {
    private static final int BITS_PER_CHAR = 8;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decodeMessage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Decode message length from the first 32 bits of the image
        int messageLength = decodeLength(bitmap);

        StringBuilder messageBuilder = new StringBuilder();
        int charIndex = 0;

        // Decode each character of the message from the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);

                // Get the RGB components
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Decode message from the least significant bits of RGB components
                if (charIndex < messageLength) {
                    char character = (char) (
                            (decodeChar(red, BITS_PER_CHAR) << (BITS_PER_CHAR - 1)) |
                                    (decodeChar(green, BITS_PER_CHAR) << (BITS_PER_CHAR - 1)) |
                                    (decodeChar(blue, BITS_PER_CHAR))
                    );
                    messageBuilder.append(character);
                    charIndex++;
                }
            }
        }

        return messageBuilder.toString();
    }

    // Decode message length from the first 32 bits of the image
    private static int decodeLength(Bitmap bitmap) {
        int messageLength = 0;
        for (int i = 0; i < 32; i++) {
            int x = i % bitmap.getWidth();
            int y = i / bitmap.getWidth();
            int pixel = bitmap.getPixel(x, y);
            int red = Color.red(pixel);
            int bit = red & 1;
            messageLength |= (bit << i);
        }
        return messageLength;
    }

    // Decode a character from the least significant bits of a color component
    private static int decodeChar(int colorComponent, int bitsPerChar) {
        return colorComponent & 1;
    }
}
