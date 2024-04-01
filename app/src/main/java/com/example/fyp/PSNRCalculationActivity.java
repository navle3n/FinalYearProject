package com.example.fyp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class PSNRCalculationActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_COVER_IMAGE = 1;
    private static final int REQUEST_SELECT_ENCODED_IMAGE = 2;
    private Uri coverImageUri;
    private Uri encodedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psnr_calculation);

        Button selectCoverImageButton = findViewById(R.id.select_cover_image_button);
        Button selectEncodedImageButton = findViewById(R.id.select_encoded_image_button);
        Button calculatePsnrButton = findViewById(R.id.calculate_psnr_button);

        selectCoverImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_COVER_IMAGE));
        selectEncodedImageButton.setOnClickListener(v -> selectImage(REQUEST_SELECT_ENCODED_IMAGE));
        calculatePsnrButton.setOnClickListener(v -> calculatePSNR());
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (requestCode == REQUEST_SELECT_COVER_IMAGE) {
                coverImageUri = selectedImageUri;
                Toast.makeText(this, "Cover image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_SELECT_ENCODED_IMAGE) {
                encodedImageUri = selectedImageUri;
                Toast.makeText(this, "Encoded image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void calculatePSNR() {
        if (coverImageUri == null || encodedImageUri == null) {
            Toast.makeText(this, "Please select both images", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap coverImage = getBitmapFromUri(coverImageUri);
            Bitmap encodedImage = getBitmapFromUri(encodedImageUri);
            double psnr = PSNRCalculationHelper.calculatePSNR(coverImage, encodedImage);
            Toast.makeText(this, "PSNR: " + psnr + " dB", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading images", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }
}
