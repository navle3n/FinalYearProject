package com.example.fyp;

import android.graphics.Bitmap;

public class SSIMCalculationHelper {

    // Constants for SSIM calculation
    private static final double K1 = 0.01;
    private static final double K2 = 0.03;
    private static final double L = 255; // Dynamic range of the pixel-values
    private static final double C1 = (K1 * L) * (K1 * L);
    private static final double C2 = (K2 * L) * (K2 * L);

    public static double calculateSSIM(Bitmap img1, Bitmap img2) {
        // Ensure the images are the same size
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            throw new IllegalArgumentException("Input images must have the same dimensions.");
        }

        double ssim = 0.0;
        // TODO: maybe divide the images into smaller windows and average the SSIM values rather than using the image as one window?
        double meanX = calculateMean(img1);
        double meanY = calculateMean(img2);
        double varianceX = calculateVariance(img1, meanX);
        double varianceY = calculateVariance(img2, meanY);
        double covariance = calculateCovariance(img1, img2, meanX, meanY);

        ssim = ((2 * meanX * meanY + C1) * (2 * covariance + C2)) / ((meanX * meanX + meanY * meanY + C1) * (varianceX + varianceY + C2));

        return ssim;
    }

    private static double calculateMean(Bitmap img) {
        long sum = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                sum += greyScale(img.getPixel(x, y));
            }
        }
        return (double) sum / (img.getWidth() * img.getHeight());
    }

    private static double greyScale(int color) {
        return (0.299 * ((color >> 16) & 0xff)) + (0.587 * ((color >> 8) & 0xff)) + (0.114 * (color & 0xff));
    }

    private static double calculateVariance(Bitmap img, double mean) {
        double variance = 0.0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                double value = greyScale(img.getPixel(x, y));
                variance += Math.pow(value - mean, 2);
            }
        }
        return variance / (img.getWidth() * img.getHeight());
    }

    private static double calculateCovariance(Bitmap img1, Bitmap img2, double meanX, double meanY) {
        double covariance = 0.0;
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                double valueX = greyScale(img1.getPixel(x, y));
                double valueY = greyScale(img2.getPixel(x, y));
                covariance += (valueX - meanX) * (valueY - meanY);
            }
        }
        return covariance / (img1.getWidth() * img1.getHeight());
    }

}