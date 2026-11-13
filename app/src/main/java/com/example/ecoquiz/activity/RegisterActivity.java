package com.example.ecoquiz.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoquiz.R;
import com.example.ecoquiz.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * RegisterActivity: Screen 2
 * Features:
 * - Full Name, Username, Email, Password, Confirm Password validation
 * - Uniqueness check for username in SQLite
 * - Strict password policy: 8+ chars, 1 uppercase, 1 digit, 1 special character
 * - Stores user with salted SHA-256 hash in ecoquiz.db
 * - Navigates back to Login on success
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;

    private DatabaseHelper databaseHelper;

    // Password validation regex: at least 8 chars, 1 uppercase, 1 number, 1 special char
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!._\\-]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        // Bind UI
        tilName = findViewById(R.id.tilRegName);
        tilUsername = findViewById(R.id.tilRegUsername);
        tilEmail = findViewById(R.id.tilRegEmail);
        tilPassword = findViewById(R.id.tilRegPassword);
        tilConfirmPassword = findViewById(R.id.tilRegConfirmPassword);

        etName = findViewById(R.id.etRegName);
        etUsername = findViewById(R.id.etRegUsername);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        // Clear previous error messages
        tilName.setError(null);
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        boolean hasError = false;

        // Validate Full Name
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Full name is required");
            hasError = true;
        }

        // Validate Username
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            hasError = true;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must have at least 3 characters");
            hasError = true;
        } else if (databaseHelper.isUsernameTaken(username)) {
            tilUsername.setError("Username is already taken, choose another");
            hasError = true;
        }

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email address is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            hasError = true;
        }

        // Validate Password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 8) {
            tilPassword.setError("Password must contain at least 8 characters");
            hasError = true;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            tilPassword.setError("Must contain 1 uppercase letter, 1 number, and 1 special symbol");
            hasError = true;
        }

        // Validate Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Save into SQLite with salted hash
        long result = databaseHelper.registerUser(name, username, email, password);

        if (result != -1) {
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_LONG).show();
            finish(); // Return to LoginActivity
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
