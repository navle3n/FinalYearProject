package com.example.fyp;

import android.graphics.Bitmap;
import android.util.Log;

public class LSBDecoder {

    private static final String TAG = "LSBDecoder";

    public static String decodeMessage(Bitmap stegoImage) {
        Log.d(TAG, "Starting message decoding");

        if (stegoImage == null) {
            Log.e(TAG, "Stego image is null. Decoding aborted.");
            return "Error: Stego image is null.";
        }

        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();
        Log.d(TAG, "Decoding from image dimensions: " + width + "x" + height);

        // Decode the message length
        StringBuilder lengthBits = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int x = i % width;
            int y = i / width;
            int pixel = stegoImage.getPixel(x, y);
            int bit = pixel & 1;
            lengthBits.append(bit);
        }
        int messageLengthInBits = Integer.parseInt(lengthBits.toString(), 2);
        Log.d(TAG, "Decoded message length (in bits): " + messageLengthInBits);

        // Adjust messageLengthInBits to account for the checksum
        int adjustedMessageLength = messageLengthInBits - 32; // Subtract checksum length

        // Now decode the actual message
        StringBuilder binaryMessage = new StringBuilder();
        for (int i = 32; i < 32 + adjustedMessageLength; i++) { // Use adjustedMessageLength
            int x = (i % width);
            int y = (i / width);
            int pixel = stegoImage.getPixel(x, y);
            int bit = pixel & 1;
            binaryMessage.append(bit);
        }
        Log.d(TAG, "Binary message: " + binaryMessage.toString());

        // Convert binary message to string
        StringBuilder decodedMessage = new StringBuilder();
        for (int i = 0; i < binaryMessage.length(); i += 8) {
            String byteString = binaryMessage.substring(i, i + 8);
            int charCode = Integer.parseInt(byteString, 2);
            decodedMessage.append((char) charCode);
        }

        Log.d(TAG, "Decoded message: " + decodedMessage.toString());
        return decodedMessage.toString();
    }
}