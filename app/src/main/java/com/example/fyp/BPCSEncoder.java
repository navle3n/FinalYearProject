package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BPCSEncoder {

    // Constants for maximum message length per color channel
    private static final int MAX_MESSAGE_LENGTH_PER_CHANNEL = Integer.MAX_VALUE; // Example value, adjust as needed

    public static Bitmap encodeMessage(Bitmap coverImage, String message) {
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();

        // Calculate maximum message length based on image dimensions and available bits per color channel
        int maxMessageLength = width * height * 3 * MAX_MESSAGE_LENGTH_PER_CHANNEL;

        // Check if message length exceeds maximum capacity
        if (message.length() > maxMessageLength) {
            // Handle message length exceeding capacity (e.g., truncate message or resize image)
            // For demonstration, we'll truncate the message
            message = message.substring(0, maxMessageLength);
        }

        // Convert message to binary
        StringBuilder binaryMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryMessage.append(binaryChar);
        }

        int messageLength = binaryMessage.length();
        int messageIndex = 0;

        // Create a new bitmap for the stego image
        Bitmap stegoImage = Bitmap.createBitmap(width, height, coverImage.getConfig());

        // Embed message into the least significant bit of each color channel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = coverImage.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Embed message into the least significant bit of each color channel
                if (messageIndex < messageLength) {
                    int bit = binaryMessage.charAt(messageIndex++) - '0';
                    red = (red & 0xFE) | bit;
                }
                if (messageIndex < messageLength) {
                    int bit = binaryMessage.charAt(messageIndex++) - '0';
                    green = (green & 0xFE) | bit;
                }
                if (messageIndex < messageLength) {
                    int bit = binaryMessage.charAt(messageIndex++) - '0';
                    blue = (blue & 0xFE) | bit;
                }

                int newPixel = Color.rgb(red, green, blue);
                stegoImage.setPixel(x, y, newPixel);
            }
        }

        return stegoImage;
    }
}
