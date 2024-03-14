package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class BPCSEncoder {

    // Constants for maximum message length per color channel
    private static final int MAX_MESSAGE_LENGTH_PER_CHANNEL = Integer.MAX_VALUE; // Example value, adjust as needed

    // Constants for the threshold and complexity measures
    private static final int THRESHOLD = 2; // Example value, adjust as needed

    // Method to encode message using BPCS
    public static Bitmap encodeMessage(Bitmap coverImage, String message) {
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();

        // Calculate maximum message length based on image dimensions and available bits per color channel
        int maxMessageLength = width * height * 3 * MAX_MESSAGE_LENGTH_PER_CHANNEL;

        // Check if message length exceeds maximum capacity
        if (message.length() > maxMessageLength) {
            // Handle message length exceeding capacity (e.g., truncate message)
            // For demonstration, we'll truncate the message
            int endIndex = Math.min(maxMessageLength, message.length());
            if (endIndex >= 0) {
                message = message.substring(0, endIndex);
            } else {
                // Handle the case where endIndex is negative (should not occur)
                // For now, just log the issue
                Log.e("BPCSEncoder", "Negative endIndex detected: " + endIndex);
            }
        }

        // Convert message to binary
        StringBuilder binaryMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            String binaryChar = Integer.toBinaryString(c);
            binaryMessage.append(String.format("%8s", binaryChar).replace(' ', '0'));
        }

        int messageLength = binaryMessage.length();
        int messageIndex = 0;

        // Create a new bitmap for the stego image
        Bitmap stegoImage = Bitmap.createBitmap(width, height, coverImage.getConfig());

        // Embed message using BPCS algorithm
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = coverImage.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Apply BPCS embedding only to pixels with sufficient complexity
                if (calculateComplexity(pixel) > THRESHOLD) {
                    // Embed message bits into LSBs of color channels if message bits are available
                    if (messageIndex < messageLength) {
                        red = embedBit(red, binaryMessage.charAt(messageIndex++));
                    }
                    if (messageIndex < messageLength) {
                        green = embedBit(green, binaryMessage.charAt(messageIndex++));
                    }
                    if (messageIndex < messageLength) {
                        blue = embedBit(blue, binaryMessage.charAt(messageIndex++));
                    }
                }

                int newPixel = Color.argb(alpha, red, green, blue);
                stegoImage.setPixel(x, y, newPixel);
            }
        }

        return stegoImage;
    }

    // Method to calculate the complexity of a pixel
    private static int calculateComplexity(int pixel) {
        // Example complexity calculation (sum of absolute differences of neighboring pixels)
        // Modify this based on a more accurate complexity measure if needed
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        return Math.abs(red - green) + Math.abs(green - blue) + Math.abs(blue - red);
    }

    // Method to embed a bit into the least significant bit of a color channel
    private static int embedBit(int color, char bit) {
        return (color & 0xFE) | (bit - '0');
    }
}
