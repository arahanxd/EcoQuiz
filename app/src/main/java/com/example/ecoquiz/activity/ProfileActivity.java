package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoquiz.R;
import com.example.ecoquiz.util.SessionManager;
import com.google.android.material.button.MaterialButton;

/**
 * ProfileActivity: Displays authenticated user credentials and SDG 13 details.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvFullName, tvUsername, tvEmail;
    private MaterialButton btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        ivBack = findViewById(R.id.ivProfileBack);
        tvFullName = findViewById(R.id.tvProfileFullName);
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnProfileLogout);

        ivBack.setOnClickListener(v -> finish());

        // Populate User Info
        tvFullName.setText(sessionManager.getName());
        tvUsername.setText("@" + sessionManager.getUsername());
        tvEmail.setText(sessionManager.getEmail());

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to end your current session?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logoutUser();
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
