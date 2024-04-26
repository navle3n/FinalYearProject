package com.example.fyp;

import android.graphics.Bitmap;
import android.util.Log;

public class LSBDecoder {

    private static final String TAG = "LSBDecoder"; // Log tag for debugging outputs

    // Method to decode an encrypted message hidden in a bitmap image using LSB steganography
    public static String decodeMessage(Bitmap stegoImage, String password) {
        Log.d(TAG, "Starting message decoding");

        // Check if the provided Bitmap image is null
        if (stegoImage == null) {
            Log.e(TAG, "Stego image is null. Decoding aborted.");
            return "Error: Stego image is null.";
        }

        // Obtain the dimensions of the image
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();
        Log.d(TAG, "Decoding from image dimensions: " + width + "x" + height);

        // Read the first 32 bits from the image to determine the length of the hidden message
        StringBuilder lengthBits = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int x = i % width;
            int y = i / width;
            int pixel = stegoImage.getPixel(x, y);
            int bit = pixel & 1; // Extract the least significant bit of the pixel
            lengthBits.append(bit);
        }
        int messageLengthInBits = Integer.parseInt(lengthBits.toString(), 2);
        Log.d(TAG, "Decoded message length (in bits): " + messageLengthInBits);

        // Adjust for any additional data, such as checksums, if necessary
        int adjustedMessageLength = messageLengthInBits - 32; // Subtracting checksum bits if present

        // Decode the binary message from the image using LSB
        StringBuilder binaryMessage = new StringBuilder();
        for (int i = 32; i < 32 + adjustedMessageLength; i++) {
            int x = (i % width);
            int y = (i / width);
            int pixel = stegoImage.getPixel(x, y);
            int bit = pixel & 1; // Extract the least significant bit of the pixel
            binaryMessage.append(bit);
        }
        Log.d(TAG, "Binary message: " + binaryMessage.toString());

        // Convert the binary message to string, decrypt it, and handle any decryption errors
        StringBuilder decryptedMessage = new StringBuilder();
        try {
            String encryptedMessage = binaryToString(binaryMessage.toString());
            decryptedMessage.append(AESUtil.decrypt(encryptedMessage, password));
        } catch (Exception e) {
            Log.e(TAG, "Decryption error", e);
            return "Decryption error";
        }

        Log.d(TAG, "Decoded and decrypted message: " + decryptedMessage.toString());
        return decryptedMessage.toString();
    }

    // Helper method to convert a binary string back to text
    private static String binaryToString(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            int charCode = Integer.parseInt(binary.substring(i, i + 8), 2);
            text.append((char) charCode);
        }
        return text.toString();
    }
}
