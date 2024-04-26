package com.example.fyp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationHelper {

    // TAG for logging
    private static final String TAG = "AuthHelper";

    // Method to sign in with email and password using Firebase Authentication
    public static void signInWithEmailAndPassword(String email, String password, FirebaseAuth mAuth, Context context) {
        Log.d(TAG, "Attempting to sign in with email: " + email);
        // Call Firebase method to sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log success and show toast
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(context, "Authentication successful.", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity after successful login
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        // Log failure and show toast with error message
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(context, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to authenticate using Google sign-in with Firebase
    public static void firebaseAuthWithGoogle(String idToken, FirebaseAuth mAuth, Context context) {
        Log.d(TAG, "Attempting to sign in with Google ID token.");
        // Call Firebase method to authenticate with Google
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log success and show toast
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(context, "Google sign in successful.", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity after successful login
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        // Log failure and show toast with error message
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(context, "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
