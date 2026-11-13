package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoquiz.R;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.util.SessionManager;

/**
 * SplashActivity: Screen 1
 * Features:
 * - EcoQuiz logo
 * - Application title & "Learn Today, Protect Tomorrow." tagline
 * - UN SDG 13 identification badge
 * - Loading indicator with subtle fade-in animation
 * - Session check: navigates to HomeActivity if logged in, else to LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2200;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        // Pre-initialize database on background thread so tables and seed questions are ready
        new Thread(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            dbHelper.getReadableDatabase();
        }).start();

        // Subtle animation for branding
        ImageView ivLogo = findViewById(R.id.ivSplashLogo);
        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        TextView tvTagline = findViewById(R.id.tvSplashTagline);

        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        ivLogo.startAnimation(fadeIn);
        tvTitle.startAnimation(fadeIn);
        tvTagline.startAnimation(fadeIn);

        // Delayed navigation check
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ivLogo.clearAnimation();
            tvTitle.clearAnimation();
            tvTagline.clearAnimation();

            Intent intent;
            if (sessionManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
