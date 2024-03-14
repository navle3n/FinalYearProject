package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class LSBEncoder {

    // Function to encode a message into an image
    public static Bitmap encodeMessage(Bitmap coverImage, String message) {
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();
        Bitmap stegoImage = Bitmap.createBitmap(width, height, coverImage.getConfig());

        int messageLength = message.length();
        int messageIndex = 0;

        // Encode message length into the first 32 bits of the image
        int remainingMessageLength = messageLength;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = coverImage.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Encode the message length into the first 32 bits of the image
                if (messageIndex < 32) {
                    int bit = (messageLength >> (31 - messageIndex)) & 1;
                    red = modifyLSB(red, bit);
                    messageIndex++;
                }

                // Encode the message into the remaining bits of the image
                else if (messageIndex - 32 < messageLength) {
                    char c = message.charAt(messageIndex - 32);
                    String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');

                    // Modify the least significant bits of the color channels
                    red = modifyLSB(red, binaryChar.charAt(0));
                    green = modifyLSB(green, binaryChar.charAt(1));
                    blue = modifyLSB(blue, binaryChar.charAt(2));

                    messageIndex++;
                }

                // Create a new pixel with modified color values
                int newPixel = Color.argb(alpha, red, green, blue);
                stegoImage.setPixel(x, y, newPixel);
            }
        }

        return stegoImage;
    }

    // Function to modify the least significant bit of a color channel
    private static int modifyLSB(int color, int bit) {
        // Clear the least significant bit and set it to the desired value
        return (color & 0xFE) | bit;
    }
}
