package com.example.fyp;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.zip.CRC32;

public class LSBEncoder {

    private static final String TAG = "LSBEncoder";

    public static Bitmap encodeMessage(Bitmap coverImage, String message) {
        Log.d(TAG, "Starting message encoding");

        if (coverImage == null) {
            Log.e(TAG, "Cover image is null. Encoding aborted.");
            return null;
        }

        // Ensure image is mutable
        Bitmap stegoImage = coverImage.copy(Bitmap.Config.ARGB_8888, true);

        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();
        int imageCapacity = width * height; // Corrected capacity calculation
        Log.d(TAG, "Image dimensions: " + width + "x" + height + ", Capacity: " + imageCapacity + " bits");

        // Calculate checksum
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        String checksumBinary = Long.toBinaryString(crc.getValue());
        checksumBinary = String.format("%32s", checksumBinary).replace(' ', '0');

        String binaryMessage = message.chars()
                .mapToObj(c -> String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'))
                .reduce("", (acc, b) -> acc + b);
        binaryMessage += checksumBinary; // Append checksum at the end

        int messageBitLength = binaryMessage.length();
        if (messageBitLength + 32 > imageCapacity) {
            Log.e(TAG, "Message is too long for the provided image. Required: " + (messageBitLength + 32) + ", Capacity: " + imageCapacity);
            return null;
        }

        // Encode message length in bits (including checksum) and message
        String messageLengthBinary = String.format("%32s", Integer.toBinaryString(messageBitLength)).replace(' ', '0');
        encodeBits(stegoImage, messageLengthBinary + binaryMessage, 0);

        Log.d(TAG, "Message encoding completed.");
        return stegoImage;
    }

    private static void encodeBits(Bitmap image, String bits, int offset) {
        for (int i = offset; i < bits.length(); i++) {
            int bit = bits.charAt(i) - '0';
            int pixelIndex = i;
            int x = pixelIndex % image.getWidth();
            int y = pixelIndex / image.getWidth();

            int pixel = image.getPixel(x, y);
            int newPixel = ((pixel & ~1) | bit); // Set the LSB of the pixel
            image.setPixel(x, y, newPixel);

            Log.d(TAG, "Encoded bit: " + bit + " into pixel: (" + x + "," + y + ")");
        }
    }
}
