package com.example.fyp;

import android.graphics.Bitmap;

public class PSNRCalculationHelper {

    // Calculate the Mean Squared Error (MSE) between two images
    public static double calculateMSE(Bitmap originalImage, Bitmap modifiedImage) {
        // Check if both images have the same dimensions
        if (originalImage.getWidth() != modifiedImage.getWidth() || originalImage.getHeight() != modifiedImage.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions.");
        }

        long sumOfSquaredErrors = 0;
        // Iterate over all pixels
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                // Get the pixel value from both images at position (x, y)
                int originalPixel = originalImage.getPixel(x, y);
                int modifiedPixel = modifiedImage.getPixel(x, y);

                // Calculate the absolute error for each color channel
                int errorR = Math.abs(((originalPixel >> 16) & 0xff) - ((modifiedPixel >> 16) & 0xff));
                int errorG = Math.abs(((originalPixel >> 8) & 0xff) - ((modifiedPixel >> 8) & 0xff));
                int errorB = Math.abs((originalPixel & 0xff) - (modifiedPixel & 0xff));

                // Sum the squares of the color channel errors
                sumOfSquaredErrors += (errorR * errorR + errorG * errorG + errorB * errorB);
            }
        }
        // Calculate MSE as the average of the sum of squared errors, adjusted for the three color channels
        double mse = sumOfSquaredErrors / (double) (3 * originalImage.getWidth() * originalImage.getHeight());
        return mse;
    }

    // Calculate the Peak Signal-to-Noise Ratio (PSNR) between two images
    public static double calculatePSNR(Bitmap originalImage, Bitmap modifiedImage) {
        double mse = calculateMSE(originalImage, modifiedImage);
        // If MSE is zero, the PSNR is infinite (the images are identical)
        if (mse == 0) {
            return Double.POSITIVE_INFINITY;
        }
        // Calculate PSNR using the formula: 10 * log10((255^2) / MSE)
        // MAX_I is the maximum possible pixel value of the image
        return 10 * Math.log10((255 * 255) / mse);
    }
}
