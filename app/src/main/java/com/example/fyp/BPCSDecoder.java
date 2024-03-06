package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BPCSDecoder {

    // Constants for error correction and security
    private static final int MINIMUM_MESSAGE_LENGTH = 8; // Minimum length for a valid message
    private static final int MAXIMUM_MESSAGE_LENGTH = 1000; // Maximum expected message length
    private static final int ERROR_CORRECTION_THRESHOLD = 3; // Maximum allowable errors for error correction

    public static String decodeMessage(Bitmap stegoImage) {
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();

        StringBuilder binaryMessage = new StringBuilder();

        // Extract message from the stego image using BPCS decoding
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Extract the LSB of the blue channel
                int blueLSB = Color.blue(stegoImage.getPixel(x, y)) & 1;
                binaryMessage.append(blueLSB);
            }
        }

        // Validate message length
        int messageLength = binaryMessage.length();
        if (messageLength < MINIMUM_MESSAGE_LENGTH || messageLength > MAXIMUM_MESSAGE_LENGTH) {
            // Invalid message length, return null or handle accordingly
            return null;
        }

        // Perform error correction
        String correctedMessage = performErrorCorrection(binaryMessage.toString());

        // Convert binary message to string
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < correctedMessage.length(); i += 8) {
            String binaryChar = correctedMessage.substring(i, i + 8);
            int charCode = Integer.parseInt(binaryChar, 2);
            message.append((char) charCode);
        }

        return message.toString();
    }

    // Method to perform error correction on the binary message
    private static String performErrorCorrection(String binaryMessage) {
        // Define the length of each block (including parity bit)
        final int blockLength = 4; // 3 data bits + 1 parity bit

        StringBuilder correctedMessage = new StringBuilder();

        // Split the binary message into blocks
        for (int i = 0; i < binaryMessage.length(); i += blockLength) {
            String block = binaryMessage.substring(i, Math.min(i + blockLength, binaryMessage.length()));

            // Count the number of 1s in the block
            int onesCount = countOnes(block);

            // Check if the parity bit is correct
            if (onesCount % 2 == 0 && block.charAt(block.length() - 1) == '1') {
                // Parity bit indicates an odd number of 1s, but the count is even
                // Flip the last bit to correct the error
                correctedMessage.append(flipLastBit(block));
            } else if (onesCount % 2 != 0 && block.charAt(block.length() - 1) == '0') {
                // Parity bit indicates an even number of 1s, but the count is odd
                // Flip the last bit to correct the error
                correctedMessage.append(flipLastBit(block));
            } else {
                // No error detected, append the block as is
                correctedMessage.append(block);
            }
        }

        return correctedMessage.toString();
    }

    // Helper method to count the number of 1s in a block
    private static int countOnes(String block) {
        int count = 0;
        for (int i = 0; i < block.length() - 1; i++) {
            if (block.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    // Helper method to flip the last bit of a block
    private static String flipLastBit(String block) {
        char lastBit = block.charAt(block.length() - 1);
        if (lastBit == '0') {
            return block.substring(0, block.length() - 1) + '1';
        } else {
            return block.substring(0, block.length() - 1) + '0';
        }
    }

}
