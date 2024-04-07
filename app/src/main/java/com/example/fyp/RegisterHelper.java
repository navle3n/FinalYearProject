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

    public static void registerWithEmailAndPassword(String email, String password, FirebaseAuth mAuth, Context context) {
        Log.d(TAG, "Attempting to register with email: " + email);
        Log.d(TAG, "registerWithEmailAndPassword: starting registration for email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void registerWithGoogle(Activity activity, FirebaseAuth mAuth) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, 9001);
    }

    public static void handleGoogleAccount(Intent data, FirebaseAuth mAuth, Activity activity) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            String idToken = task.getResult(ApiException.class).getIdToken();
            firebaseAuthWithGoogleForRegistration(idToken, mAuth, activity);
        } catch (ApiException e) {
            Log.w("RegisterHelper", "Google registration failed", e);
        }
    }

    private static void firebaseAuthWithGoogleForRegistration(String idToken, FirebaseAuth mAuth, Activity activity) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d("RegisterHelper", "Registration with Google: success");
                        // Navigate to MainActivity or update UI to reflect successful registration
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Log.w("RegisterHelper", "Registration with Google: failure", task.getException());
                        // Update UI to show registration failed (optional)
                    }
                });
    }

}
