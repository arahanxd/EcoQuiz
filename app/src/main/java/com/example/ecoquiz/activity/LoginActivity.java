package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoquiz.R;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.model.User;
import com.example.ecoquiz.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity: Screen 3
 * Features:
 * - Username & Password inputs with visibility toggle
 * - Credentials verification against ecoquiz.db using salted SHA-256
 * - Login session creation using SharedPreferences
 * - Seamless navigation to HomeActivity or RegisterActivity
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Bind views
        tilUsername = findViewById(R.id.tilLoginUsername);
        tilPassword = findViewById(R.id.tilLoginPassword);
        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Login button click listener
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Navigation to RegisterActivity
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Authenticate with SQLite database
        User authenticatedUser = databaseHelper.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Save session
            sessionManager.createLoginSession(
                    authenticatedUser.getUserId(),
                    authenticatedUser.getUsername(),
                    authenticatedUser.getName(),
                    authenticatedUser.getEmail()
            );

            Toast.makeText(this, "Welcome, " + authenticatedUser.getName() + "!", Toast.LENGTH_SHORT).show();

            // Navigate to Home Dashboard
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            tilPassword.setError("Invalid username or password");
            Toast.makeText(this, "Authentication failed. Please check credentials.", Toast.LENGTH_LONG).show();
        }
    }
}
