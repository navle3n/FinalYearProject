package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Setting the layout for the activity

        mAuth = FirebaseAuth.getInstance(); // Initializing Firebase Authentication

        // Binding UI elements with the activity
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button registerButton = findViewById(R.id.register_btn);
        Button googleRegisterButton = findViewById(R.id.google_register_btn);

        // Setting an onClick listener for the email/password registration button
        registerButton.setOnClickListener(view -> {
            // Getting the email and password from the user input
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            // Calling the helper method to register the user with email and password
            RegisterHelper.registerWithEmailAndPassword(email, password, mAuth, this);
        });

        // Setting an onClick listener for the Google registration button
        googleRegisterButton.setOnClickListener(view -> RegisterHelper.registerWithGoogle(this, mAuth));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handling the result of the Google Sign-In intent
        if (requestCode == 9001 && data != null) {
            // Processing the Google Sign-In data
            RegisterHelper.handleGoogleAccount(data, mAuth, this);
        }
    }
}
