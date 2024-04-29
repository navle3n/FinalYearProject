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
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Log tag for debugging
    private Uri imageUri; // Uri to store the selected image
    private EditText messageEditText, passwordEditText; // Input fields for the message and password
    private ImageView imageView; // Displays the selected or processed image
    private TextView userEmailTextView; // Displays the logged-in user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI(); // Initialize UI components and set up event listeners
    }

    private void initializeUI() {
        // Binding UI components to their respective views
        messageEditText = findViewById(R.id.message_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        imageView = findViewById(R.id.image_view);
        Button selectImageButton = findViewById(R.id.select_image_button);
        Button hideMessageButton = findViewById(R.id.hide_message_button);
        Button extractMessageButton = findViewById(R.id.extract_message_button);
        Button signOutButton = findViewById(R.id.sign_out_button);
        userEmailTextView = findViewById(R.id.user_email_text_view);
        Button aboutPrivacyButton = findViewById(R.id.about_privacy_button);

        // Set onClick listeners for various actions
        selectImageButton.setOnClickListener(v -> selectImage());
        hideMessageButton.setOnClickListener(v -> hideMessage());
        extractMessageButton.setOnClickListener(v -> extractMessage());
        signOutButton.setOnClickListener(v -> signOut());
        aboutPrivacyButton.setOnClickListener(view -> openPrivacyInfo());

        // Display current user's email, or a default text if no user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmailTextView.setText(currentUser.getEmail());
        } else {
            userEmailTextView.setText("No user logged in");
        }
    }

    // Launch the Privacy Activity
    private void openPrivacyInfo() {
        Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
        startActivity(intent);
    }

    // Start an intent to select an image from the device
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    // Start an intent to navigate to Calulation activity
    public void startPSNRCalculationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, CalculationActivity.class);
        startActivity(intent);
    }

    // Handling the result from the image selection intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            loadImage(imageUri);
        }
    }

    // Load and display the selected image in the ImageView
    private void loadImage(Uri selectedImageUri) {
        try {
            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            imageView.setImageBitmap(selectedImageBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    // Encrypt and hide a message within the selected image using LSB encoding
    private void hideMessage() {
        String message = messageEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (imageUri != null && !message.isEmpty() && !password.isEmpty()) {
            new HideMessageTask(password).execute(message);
        } else {
            Toast.makeText(this, "Image, message, and password are required", Toast.LENGTH_SHORT).show();
        }
    }

    // Extract and decrypt a message from the selected image
    private void extractMessage() {
        String password = passwordEditText.getText().toString();
        if (imageUri != null && !password.isEmpty()) {
            new ExtractMessageTask(password).execute(imageUri);
        } else {
            Toast.makeText(this, "Image and password are required", Toast.LENGTH_SHORT).show();
        }
    }

    // Sign out the current user and return to the Authentication Activity
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Display a popup with the extracted message
    private void showMessagePopup(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Extracted Message")
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // AsyncTask for hiding a message inside an image
    private class HideMessageTask extends AsyncTask<String, Void, Bitmap> {
        private String password;

        public HideMessageTask(String password) {
            this.password = password;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String message = params[0];
            try {
                Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                String encryptedMessage = AESUtil.encrypt(message, this.password);
                return LSBEncoder.encodeMessage(originalImage, encryptedMessage, this.password);
            } catch (Exception e) {
                Log.e(TAG, "Error in hiding message", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
                Toast.makeText(MainActivity.this, "Message hidden successfully", Toast.LENGTH_SHORT).show();
                saveEncodedImageToFile(result, "StegImage.png");
            } else {
                Toast.makeText(MainActivity.this, "Failed to hide message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the processed image to the device storage
    private void saveEncodedImageToFile(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(MainActivity.this, "Encoded image saved to Pictures", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Failed to save encoded image", Toast.LENGTH_SHORT).show();
        }
    }

    // AsyncTask for extracting a message from an image
    private class ExtractMessageTask extends AsyncTask<Uri, Void, String> {
        private String password;

        // Constructor for the ExtractMessageTask, initializes with the encryption/decryption password
        public ExtractMessageTask(String password) {
            this.password = password;
        }

        // Extract message and decrypt
        @Override
        protected String doInBackground(Uri... params) {
            Uri imageUri = params[0];
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap stegoImage = BitmapFactory.decodeStream(inputStream);
                String extractedEncryptedMessage = LSBDecoder.decodeMessage(stegoImage, this.password);
                return AESUtil.decrypt(extractedEncryptedMessage, this.password);
            } catch (Exception e) {
                Log.e(TAG, "Error in extracting message", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                showMessagePopup(result);
            } else {
                Toast.makeText(MainActivity.this, "Failed to extract message", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
