package com.example.fyp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShareImageActivity extends AppCompatActivity {

    private Uri selectedImageUri;
    private ImageView imageView;
    private EditText userEmailEditText;
    private Button shareButton, selectImageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);

        imageView = findViewById(R.id.preview_image_view);
        userEmailEditText = findViewById(R.id.user_email_edittext);
        shareButton = findViewById(R.id.send_image_btn);
        selectImageButton = findViewById(R.id.select_image_btn);

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                });

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
        Button viewSharedImagesButton = findViewById(R.id.view_shared_images_btn);
        viewSharedImagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShareImageActivity.this, ViewSharedImagesActivity.class);
            startActivity(intent);
        });

        shareButton.setOnClickListener(v -> {
            String userEmail = userEmailEditText.getText().toString();
            if (selectedImageUri != null && !userEmail.isEmpty()) {
                ShareImageHelper.shareImage(selectedImageUri, userEmail, new ShareImageHelper.ShareImageCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ShareImageActivity.this, "Image shared successfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ShareImageActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please select an image and enter an email.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
