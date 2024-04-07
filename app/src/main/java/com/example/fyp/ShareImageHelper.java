package com.example.fyp;

import android.net.Uri;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShareImageHelper {

    public interface ShareImageCallback {
        void onSuccess();
        void onError(String message);
    }
    public static void shareImage(Uri imageUri, String userEmail, ShareImageCallback callback) {
        Log.d("ShareImageHelper", "Attempting to share image. Email: " + userEmail + ", Uri: " + imageUri.toString());
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(userEmail)
                .addOnCompleteListener(task -> {
                    Log.d("AuthCheck", "fetchSignInMethodsForEmail onComplete. Success: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            Log.d("AuthCheck", "Email not registered: " + userEmail);
                            callback.onError("Email not registered.");
                        } else {
                            Log.d("AuthCheck", "Email is registered: " + userEmail + ", proceeding to upload image.");
                            uploadImage(imageUri, userEmail, callback);
                        }
                    } else {
                        Log.e("AuthCheck", "Failed to check user email: " + userEmail, task.getException());
                        callback.onError("Failed to check user email.");
                    }
                });
    }


    private static void uploadImage(Uri imageUri, String userEmail, ShareImageCallback callback) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "shared_" + timestamp + ".jpg";
        Log.d("UploadImage", "Uploading image as " + fileName);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + fileName);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("UploadImage", "Image upload successful. Getting download URL...");
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d("UploadImage", "Got download URL: " + uri.toString());
                                saveImageInfoToDatabase(uri.toString(), userEmail, callback);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UploadImage", "Failed to get download URL.", e);
                                callback.onError("Failed to get image URL.");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("UploadImage", "Failed to upload image.", e);
                    callback.onError("Failed to upload image.");
                });
    }

    private static void saveImageInfoToDatabase(String imageUrl, String userEmail, ShareImageCallback callback) {
        String key = FirebaseDatabase.getInstance().getReference().child("shared_images").push().getKey();
        Log.d("SaveToDatabase", "Saving image info to database. Key: " + key + ", Image URL: " + imageUrl);

        FirebaseDatabase.getInstance().getReference().child("shared_images").child(key)
                .setValue(new SharedImage(imageUrl, FirebaseAuth.getInstance().getCurrentUser().getEmail(), userEmail))
                .addOnSuccessListener(aVoid -> {
                    Log.d("SaveToDatabase", "Image info saved successfully.");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("SaveToDatabase", "Failed to save image info.", e);
                    callback.onError("Failed to save image info.");
                });
    }


    static class SharedImage {
        public String imageUrl;
        public String fromUser;
        public String toUser;

        public SharedImage(String imageUrl, String fromUser, String toUser) {
            this.imageUrl = imageUrl;
            this.fromUser = fromUser;
            this.toUser = toUser;
        }
    }
}
