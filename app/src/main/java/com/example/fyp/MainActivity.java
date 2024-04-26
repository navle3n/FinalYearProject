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

    private static final String TAG = "MainActivity";
    private Uri imageUri;
    private EditText messageEditText, passwordEditText;
    private ImageView imageView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
    }

    private void initializeUI() {
        messageEditText = findViewById(R.id.message_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        imageView = findViewById(R.id.image_view);
        Button selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(v -> selectImage());
        Button hideMessageButton = findViewById(R.id.hide_message_button);
        hideMessageButton.setOnClickListener(v -> hideMessage());
        Button extractMessageButton = findViewById(R.id.extract_message_button);
        extractMessageButton.setOnClickListener(v -> extractMessage());
        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> signOut());
        userEmailTextView = findViewById(R.id.user_email_text_view);
        Button aboutPrivacyButton = findViewById(R.id.about_privacy_button);

        aboutPrivacyButton.setOnClickListener(view -> openPrivacyInfo());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmailTextView.setText(currentUser.getEmail());
        } else {
            userEmailTextView.setText("No user logged in");
        }
    }
    private void openPrivacyInfo() {
        Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
        startActivity(intent);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    public void startPSNRCalculationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, CalculationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            loadImage(imageUri);
        }
    }

    private void loadImage(Uri selectedImageUri) {
        try {
            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            imageView.setImageBitmap(selectedImageBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideMessage() {
        String message = messageEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (imageUri != null && !message.isEmpty() && !password.isEmpty()) {
            new HideMessageTask(password).execute(message);
        } else {
            Toast.makeText(this, "Image, message, and password are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void extractMessage() {
        String password = passwordEditText.getText().toString();
        if (imageUri != null && !password.isEmpty()) {
            new ExtractMessageTask(password).execute(imageUri);
        } else {
            Toast.makeText(this, "Image and password are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showMessagePopup(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Extracted Message")
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

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

    private class ExtractMessageTask extends AsyncTask<Uri, Void, String> {
        private String password;

        public ExtractMessageTask(String password) {
            this.password = password;
        }

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
