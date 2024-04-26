package com.example.fyp;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.zip.CRC32;

public class LSBEncoder {

    private static final String TAG = "LSBEncoder"; // Tag for logging

    // Method to encode a message into a bitmap image using LSB steganography and AES encryption
    public static Bitmap encodeMessage(Bitmap coverImage, String message, String password) {
        Log.d(TAG, "Starting message encoding");

        // Check if the provided Bitmap image is null
        if (coverImage == null) {
            Log.e(TAG, "Cover image is null. Encoding aborted.");
            return null;
        }

        // Create a mutable copy of the image to encode the message
        Bitmap stegoImage = coverImage.copy(Bitmap.Config.ARGB_8888, true);

        // Get image dimensions and calculate its capacity in bits
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();
        int imageCapacity = width * height;
        Log.d(TAG, "Image dimensions: " + width + "x" + height + ", Capacity: " + imageCapacity + " bits");

        // Compute CRC checksum for the message for error checking
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        String checksumBinary = Long.toBinaryString(crc.getValue());
        checksumBinary = String.format("%32s", checksumBinary).replace(' ', '0');

        // Encrypt the message using AES
        try {
            message = AESUtil.encrypt(message, password);
            Log.d(TAG, "Message encrypted: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Encryption error", e);
            return null;
        }

        // Convert message to binary string
        String binaryMessage = message.chars()
                .mapToObj(c -> String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'))
                .reduce("", (acc, b) -> acc + b);
        binaryMessage += checksumBinary; // Append checksum at the end
        Log.d(TAG, "Message in binary: " + binaryMessage);

        // Check if the message fits in the image
        int messageBitLength = binaryMessage.length();
        if (messageBitLength + 32 > imageCapacity) {
            Log.e(TAG, "Message is too long for the provided image. Required: " + (messageBitLength + 32) + ", Capacity: " + imageCapacity);
            return null;
        }

        // Encode the message length as a binary string
        String messageLengthBinary = String.format("%32s", Integer.toBinaryString(messageBitLength)).replace(' ', '0');
        encodeBits(stegoImage, messageLengthBinary + binaryMessage, 0);

        Log.d(TAG, "Message encoding completed.");
        return stegoImage;
    }

    // Helper method to encode a binary string into the image using LSB technique
    private static void encodeBits(Bitmap image, String bits, int offset) {
        for (int i = offset; i < bits.length(); i++) {
            int bit = bits.charAt(i) - '0';
            int pixelIndex = i;
            int x = pixelIndex % image.getWidth();
            int y = pixelIndex / image.getWidth();

            // Update the least significant bit of the pixel to store the bit of the message
            int pixel = image.getPixel(x, y);
            int newPixel = ((pixel & ~1) | bit);
            image.setPixel(x, y, newPixel);

            Log.d(TAG, "Encoded bit: " + bit + " into pixel: (" + x + "," + y + ")");
        }
    }
}
