package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BPCSDecoder {

    // Constants for error correction and security
    private static final int MINIMUM_MESSAGE_LENGTH = 8; // Minimum length for a valid message
    private static final int MAXIMUM_MESSAGE_LENGTH = Integer.MAX_VALUE; // Maximum expected message length
    private static final int ERROR_CORRECTION_THRESHOLD = 3; // Maximum allowable errors for error correction

    public static String decodeMessage(Bitmap stegoImage) {
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();

        StringBuilder binaryMessage = new StringBuilder();

        // Extract message from the stego image using BPCS decoding
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = stegoImage.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Extract the LSB of each color channel
                binaryMessage.append(red & 1);
                binaryMessage.append(green & 1);
                binaryMessage.append(blue & 1);
            }
        }

        // Validate message length
        int messageLength = binaryMessage.length();
        if (messageLength < MINIMUM_MESSAGE_LENGTH || messageLength > MAXIMUM_MESSAGE_LENGTH) {
            // Invalid message length, return null or handle accordingly
            return null;
        }

        // Convert binary message to string
        StringBuilder message = new StringBuilder();
        int length = binaryMessage.length();
        for (int i = 0; i < length; i += 8) {
            int endIndex = Math.min(i + 8, length); // Ensure endIndex doesn't exceed the length
            String binaryChar = binaryMessage.substring(i, endIndex);
            int charCode = Integer.parseInt(binaryChar, 2);
            message.append((char) charCode);
        }

        return message.toString();
    }

    // Method to perform error correction on the binary message
    private static String performErrorCorrection(String binaryMessage) {
        // No error correction implemented in this example, return the message as is
        return binaryMessage;
    }
}
