package com.example.fyp;

import android.app.Activity;
import android.app.AlertDialog;
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

    private static final int REQUEST_SELECT_COVER_IMAGE = 1;
    private static final int REQUEST_SELECT_ENCODED_IMAGE = 2;
    private Uri coverImageUri;
    private Uri encodedImageUri;
    private ImageView coverImageView;
    private ImageView encodedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psnr_calculation);

        Button selectCoverImageButton = findViewById(R.id.select_cover_image_button);
        Button selectEncodedImageButton = findViewById(R.id.select_encoded_image_button);
        Button calculatePsnrButton = findViewById(R.id.calculate_psnr_button);
        Button calculateSsimButton = findViewById(R.id.calculate_ssim_button); // SSIM button

        coverImageView = findViewById(R.id.cover_image_view);
        encodedImageView = findViewById(R.id.encoded_image_view);

        selectCoverImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_COVER_IMAGE));
        selectEncodedImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_ENCODED_IMAGE));
        calculatePsnrButton.setOnClickListener(v -> calculatePSNR());
        calculateSsimButton.setOnClickListener(v -> calculateSSIM()); // Set OnClickListener for SSIM

        Log.d("CalculationActivity", "Activity created and listeners initialized.");
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
        Log.d("CalculationActivity", "Image selection intent triggered.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (requestCode == REQUEST_SELECT_COVER_IMAGE) {
                coverImageUri = selectedImageUri;
                updateImageView(coverImageView, coverImageUri);
                Log.d("CalculationActivity", "Cover image selected and displayed.");
            } else if (requestCode == REQUEST_SELECT_ENCODED_IMAGE) {
                encodedImageUri = selectedImageUri;
                updateImageView(encodedImageView, encodedImageUri);
                Log.d("CalculationActivity", "Encoded image selected and displayed.");
            }
        }
    }

    private void calculatePSNR() {
        if (coverImageUri == null || encodedImageUri == null) {
            showDialog("Error", "Please select both images before calculating PSNR.");
            return;
        }

        try {
            Bitmap coverImage = getBitmapFromUri(coverImageUri);
            Bitmap encodedImage = getBitmapFromUri(encodedImageUri);

            if (coverImage.getWidth() != encodedImage.getWidth() ||
                    coverImage.getHeight() != encodedImage.getHeight()) {
                showDialog("Error", "Images must have the same dimensions.");
                return;
            }

            double psnr = PSNRCalculationHelper.calculatePSNR(coverImage, encodedImage);
            showDialog("PSNR Calculation", "PSNR: " + psnr + " dB");
        } catch (IOException e) {
            showDialog("Error", "Error loading images for PSNR calculation.");
        }
    }

    private void calculateSSIM() {
        if (coverImageUri == null || encodedImageUri == null) {
            showDialog("Error", "Please select both images before calculating SSIM.");
            return;
        }

        try {
            Bitmap coverImage = getBitmapFromUri(coverImageUri);
            Bitmap encodedImage = getBitmapFromUri(encodedImageUri);

            if (coverImage.getWidth() != encodedImage.getWidth() ||
                    coverImage.getHeight() != encodedImage.getHeight()) {
                showDialog("Error", "Images must have the same dimensions for SSIM calculation.");
                return;
            }

            double ssim = SSIMCalculationHelper.calculateSSIM(coverImage, encodedImage);
            showDialog("SSIM Calculation", "SSIM: " + ssim);
        } catch (IOException e) {
            showDialog("Error", "Error loading images for SSIM calculation.");
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    private void updateImageView(ImageView imageView, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e("CalculationActivity", "Error updating image view.", e);
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
