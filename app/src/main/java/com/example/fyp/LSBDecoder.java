package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class LSBDecoder {

    public static String decodeMessage(Bitmap stegoImage) {
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();

        StringBuilder messageBuilder = new StringBuilder();

        // Variable to track the message length
        int messageLength = 0;

        // Variable to track the current bit index in the message
        int bitIndex = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = stegoImage.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Extract the least significant bits from each color channel
                char redLSB = (char) (red & 1);
                char greenLSB = (char) (green & 1);
                char blueLSB = (char) (blue & 1);

                // Increment the message length bits
                messageLength |= (redLSB << bitIndex++);
                messageLength |= (greenLSB << bitIndex++);
                messageLength |= (blueLSB << bitIndex++);

                // Check if we have enough bits to determine the message length
                if (bitIndex >= 32) {
                    // We have extracted the message length, exit the loop
                    break;
                }
            }
            if (bitIndex >= 32) {
                // We have extracted the message length, exit the outer loop
                break;
            }
        }

        // Reset bit index for decoding the actual message
        bitIndex = 0;

        // Extract the message using the determined message length
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitIndex >= messageLength * 8) {
                    // We have extracted the entire message, exit the loop
                    break;
                }

                int pixel = stegoImage.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Extract the least significant bits from each color channel
                char redLSB = (char) (red & 1);
                char greenLSB = (char) (green & 1);
                char blueLSB = (char) (blue & 1);

                // Append the LSBs to the message builder
                messageBuilder.append(redLSB);
                messageBuilder.append(greenLSB);
                messageBuilder.append(blueLSB);

                // Increment the bit index
                bitIndex += 3;
            }
        }

        // Convert the binary message to a string
        String message = messageBuilder.toString();

        // Remove the padding null characters and the message terminator
        int nullIndex = message.indexOf('\0');
        if (nullIndex != -1) {
            message = message.substring(0, nullIndex);
        }

        return message;
    }
}
