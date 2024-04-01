package com.example.fyp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final String TAG = "MainActivity";
    private Uri imageUri;
    private EditText messageEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Redirect to AuthenticationActivity
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack to prevent user coming back to MainActivity on pressing back button
            startActivity(intent);
        });
    }

    private void initializeUI() {
        messageEditText = findViewById(R.id.message_edit_text);
        imageView = findViewById(R.id.image_view);
        Button selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(v -> selectImage());
        Button hideMessageButton = findViewById(R.id.hide_message_button);
        hideMessageButton.setOnClickListener(v -> hideMessage());
        Button extractMessageButton = findViewById(R.id.extract_message_button);
        extractMessageButton.setOnClickListener(v -> extractMessage());
    }
    public void startPSNRCalculationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, PSNRCalculationActivity.class);
        startActivity(intent);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            loadImage(selectedImageUri);
        }
    }

    private void loadImage(Uri selectedImageUri) {
        imageUri = selectedImageUri;
        try {
            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imageView.setImageBitmap(selectedImageBitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image", e);
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideMessage() {
        String message = messageEditText.getText().toString();
        if (imageUri != null && !message.isEmpty()) {
            new HideMessageTask().execute(message);
        } else {
            Toast.makeText(this, "Please select an image and enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void extractMessage() {
        if (imageUri != null) {
            new ExtractMessageTask().execute(imageUri);
        } else {
            Toast.makeText(this, "Please select an image to extract a message from", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessagePopup(String message) {
        View alertLayout = getLayoutInflater().inflate(R.layout.popup_message, null);
        TextView textViewMessage = alertLayout.findViewById(R.id.extracted_message_textview);
        textViewMessage.setText(message);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Extracted Message")
                .setView(alertLayout)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private class HideMessageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                return LSBEncoder.encodeMessage(selectedImageBitmap, params[0]);
            } catch (IOException e) {
                Log.e(TAG, "Error during encoding", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap encodedImage) {
            if (encodedImage != null) {
                imageView.setImageBitmap(encodedImage);
                Toast.makeText(MainActivity.this, "Message hidden successfully", Toast.LENGTH_SHORT).show();
                saveEncodedImageToFile(encodedImage, "stegImage.png");
            } else {
                Toast.makeText(MainActivity.this, "Failed to hide message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveEncodedImageToFile(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Log.d(TAG, "Stego-image saved to Pictures: " + fileName);
            Toast.makeText(MainActivity.this, "Image saved to Pictures", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, "Error saving stego-image", e);
            Toast.makeText(MainActivity.this, "Error saving image", Toast.LENGTH_LONG).show();
        }
    }

    private class ExtractMessageTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... params) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(params[0]);
                Bitmap stegoImage = BitmapFactory.decodeStream(inputStream);
                return LSBDecoder.decodeMessage(stegoImage);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found for decoding", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String extractedMessage) {
            if (extractedMessage != null) {
                showMessagePopup(extractedMessage);
            } else {
                Toast.makeText(MainActivity.this, "Failed to extract message", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
