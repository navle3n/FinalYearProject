package com.example.fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final String IMAGE_FILE_NAME = "selected_image.jpg";

    private EditText messageEditText;
    private ImageView imageView;
    private String selectedImageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.message_edit_text);
        imageView = findViewById(R.id.image_view);

        findViewById(R.id.select_image_button).setOnClickListener(v -> selectImage());
        findViewById(R.id.hide_message_button).setOnClickListener(v -> hideMessage());
        findViewById(R.id.extract_message_button).setOnClickListener(v -> extractMessage());
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
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                selectedImageFilePath = saveBitmapToFile(selectedImageBitmap);
                imageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), IMAGE_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void hideMessage() {
        String message = messageEditText.getText().toString();
        if (selectedImageFilePath != null && !message.isEmpty()) {
            new HideMessageTask().execute(message, selectedImageFilePath);
        } else {
            Toast.makeText(this, "Please select an image and enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void extractMessage() {
        if (selectedImageFilePath != null) {
            new ExtractMessageTask().execute(selectedImageFilePath);
        } else {
            Toast.makeText(this, "Please select an image to extract a message from", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessagePopup(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_message, null);

        TextView textViewMessage = alertLayout.findViewById(R.id.extracted_message_textview);
        textViewMessage.setText(message);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Extracted Message");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private class HideMessageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String message = strings[0];
            String imagePath = strings[1];
            Bitmap selectedImageBitmap = BitmapFactory.decodeFile(imagePath);
            try {
                return BPCSEncoder.encodeMessage(selectedImageBitmap, message);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap encodedImage) {
            if (encodedImage != null) {
                imageView.setImageBitmap(encodedImage);
                Toast.makeText(MainActivity.this, "Message hidden successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to hide message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ExtractMessageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String imagePath = strings[0];
            Bitmap selectedImageBitmap = BitmapFactory.decodeFile(imagePath);
            try {
                return BPCSDecoder.decodeMessage(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String extractedMessage) {
            if (extractedMessage != null) {
                showMessagePopup(extractedMessage);
                Toast.makeText(MainActivity.this, "Message extracted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to extract message", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
