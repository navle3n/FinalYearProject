package com.example.fyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class CalculationActivity extends AppCompatActivity {

    private static final String TAG = "CalculationActivity"; // Tag for logging
    private static final int REQUEST_SELECT_COVER_IMAGE = 1; // Request code for selecting cover image
    private static final int REQUEST_SELECT_ENCODED_IMAGE = 2; // Request code for selecting encoded image
    private Uri coverImageUri; // URI for selected cover image
    private Uri encodedImageUri; // URI for selected encoded image
    private ImageView coverImageView; // ImageView for displaying the cover image
    private ImageView encodedImageView; // ImageView for displaying the encoded image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psnr_calculation);

        // Initialize UI components
        Button selectCoverImageButton = findViewById(R.id.select_cover_image_button);
        Button selectEncodedImageButton = findViewById(R.id.select_encoded_image_button);
        Button calculatePsnrButton = findViewById(R.id.calculate_psnr_button);
        Button calculateSsimButton = findViewById(R.id.calculate_ssim_button);

        coverImageView = findViewById(R.id.cover_image_view);
        encodedImageView = findViewById(R.id.encoded_image_view);

        // Set listeners for buttons
        selectCoverImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_COVER_IMAGE));
        selectEncodedImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_ENCODED_IMAGE));
        calculatePsnrButton.setOnClickListener(v -> calculatePSNR());
        calculateSsimButton.setOnClickListener(v -> calculateSSIM());

        Log.d(TAG, "Activity created and listeners initialized.");
    }

    // Method to initiate an image selection intent
    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
        Log.d(TAG, "Image selection intent triggered.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (requestCode == REQUEST_SELECT_COVER_IMAGE) {
                coverImageUri = selectedImageUri;
                updateImageView(coverImageView, coverImageUri);
                Log.d(TAG, "Cover image selected and displayed.");
            } else if (requestCode == REQUEST_SELECT_ENCODED_IMAGE) {
                encodedImageUri = selectedImageUri;
                updateImageView(encodedImageView, encodedImageUri);
                Log.d(TAG, "Encoded image selected and displayed.");
            }
        }
    }

    // Method to calculate PSNR between two images
    private void calculatePSNR() {
        if (coverImageUri == null || encodedImageUri == null) {
            showDialog("Error", "Please select both images before calculating PSNR.");
            Log.e("Error", "Please select both images before calculating PSNR.");
            return;
        }

        try {
            Bitmap coverImage = getBitmapFromUri(coverImageUri);
            Bitmap encodedImage = getBitmapFromUri(encodedImageUri);

            if (coverImage.getWidth() != encodedImage.getWidth() ||
                    coverImage.getHeight() != encodedImage.getHeight()) {
                showDialog("Error", "Images must have the same dimensions.");
                Log.e("Error", "Images must have the same dimensions.");
                return;
            }

            double psnr = PSNRCalculationHelper.calculatePSNR(coverImage, encodedImage);
            showDialog("PSNR Calculation", "PSNR: " + psnr + " dB");
            Log.e("PSNR Calculation", "PSNR: " + psnr + " dB");
        } catch (IOException e) {
            showDialog("Error", "Error loading images for PSNR calculation.");
            Log.e("Error", "Error loading images for PSNR calculation.");
        }
    }

    // Method to calculate SSIM between two images
    private void calculateSSIM() {
        if (coverImageUri == null || encodedImageUri == null) {
            showDialog("Error", "Please select both images before calculating SSIM.");
            Log.e("Error", "Please select both images before calculating SSIM.");
            return;
        }

        try {
            Bitmap coverImage = getBitmapFromUri(coverImageUri);
            Bitmap encodedImage = getBitmapFromUri(encodedImageUri);

            if (coverImage.getWidth() != encodedImage.getWidth() ||
                    coverImage.getHeight() != encodedImage.getHeight()) {
                showDialog("Error", "Images must have the same dimensions for SSIM calculation.");
                Log.e("Error", "Images must have the same dimensions for SSIM calculation.");
                return;
            }

            double ssim = SSIMCalculationHelper.calculateSSIM(coverImage, encodedImage);
            showDialog("SSIM Calculation", "SSIM: " + ssim);
            Log.e("SSIM Calculation", "SSIM: " + ssim);
        } catch (IOException e) {
            showDialog("Error", "Error loading images for SSIM calculation.");
            Log.e("Error", "Error loading images for SSIM calculation.");
        }
    }

    // Helper method to fetch a Bitmap from a Uri
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    // Helper method to update ImageView with a new image from Uri
    private void updateImageView(ImageView imageView, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "ImageView updated with new image.");
        } catch (IOException e) {
            Log.e(TAG, "Error updating image view.", e);
        }
    }

    // Method to show a dialog with a title and message
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
