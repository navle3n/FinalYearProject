package com.example.fyp;

import android.graphics.Bitmap;

public class PSNRCalculationHelper {

    public static double calculateMSE(Bitmap originalImage, Bitmap modifiedImage) {
        if (originalImage.getWidth() != modifiedImage.getWidth() || originalImage.getHeight() != modifiedImage.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions.");
        }

        long sumOfSquaredErrors = 0;
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int originalPixel = originalImage.getPixel(x, y);
                int modifiedPixel = modifiedImage.getPixel(x, y);

                int errorR = Math.abs(((originalPixel >> 16) & 0xff) - ((modifiedPixel >> 16) & 0xff));
                int errorG = Math.abs(((originalPixel >> 8) & 0xff) - ((modifiedPixel >> 8) & 0xff));
                int errorB = Math.abs((originalPixel & 0xff) - (modifiedPixel & 0xff));

                sumOfSquaredErrors += (errorR * errorR + errorG * errorG + errorB * errorB);
            }
        }
        double mse = sumOfSquaredErrors / (double) (3 * originalImage.getWidth() * originalImage.getHeight());
        return mse;
    }

    public static double calculatePSNR(Bitmap originalImage, Bitmap modifiedImage) {
        double mse = calculateMSE(originalImage, modifiedImage);
        if (mse == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return 10 * Math.log10((255 * 255) / mse);
    }
}
