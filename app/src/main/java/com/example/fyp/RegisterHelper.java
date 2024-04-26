package com.example.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterHelper {

    private static final String TAG = "RegisterHelper";

    // Method to register a user with email and password
    public static void registerWithEmailAndPassword(String email, String password, FirebaseAuth mAuth, Context context) {
        Log.d(TAG, "Attempting to register with email: " + email);
        // Start registration process
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show();
                        // Redirect user to main activity upon successful registration
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        // Registration failed
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to initiate Google sign-in
    public static void registerWithGoogle(Activity activity, FirebaseAuth mAuth) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        // Start Google Sign In activity
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, 9001);
    }

    // Method to handle Google sign-in callback
    public static void handleGoogleAccount(Intent data, FirebaseAuth mAuth, Activity activity) {
        Task        <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Get the Google ID token from the task result
            String idToken = task.getResult(ApiException.class).getIdToken();
            // Authenticate with Firebase using the Google ID token
            firebaseAuthWithGoogleForRegistration(idToken, mAuth, activity);
        } catch (ApiException e) {
            // Google sign in failed
            Log.w("RegisterHelper", "Google registration failed", e);
        }
    }

    // Method to authenticate with Firebase using Google credentials
    private static void firebaseAuthWithGoogleForRegistration(String idToken, FirebaseAuth mAuth, Activity activity) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Google sign-in successful, redirect to main activity
                        Log.d("RegisterHelper", "Registration with Google: success");
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        // Google sign-in failed
                        Log.w("RegisterHelper", "Registration with Google: failure", task.getException());
                    }
                });
    }
}
