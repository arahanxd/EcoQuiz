package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecoquiz.R;
import com.example.ecoquiz.adapter.DifficultyPagerAdapter;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.model.DifficultyItem;
import com.example.ecoquiz.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeActivity: Screen 4
 * Features:
 * - Welcome greeting with user's name from SessionManager
 * - SDG 13 identification and application logo
 * - Real-time statistics: Total Quizzes Attempted, Highest Score from ecoquiz.db
 * - Horizontal carousel using ViewPager2 with 3 Difficulty Cards (Easy, Medium, Hard)
 * - Navigation to QuizActivity with selected difficulty
 * - Quick action buttons: Leaderboard, Performance History, Profile, Logout
 */
public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvTotalQuizzes, tvHighestScore;
    private ImageView ivLogout;
    private ViewPager2 vpDifficulty;
    private MaterialButton btnNavLeaderboard, btnNavPerformance, btnNavProfile;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        // Security check: ensure active session
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_home);
        databaseHelper = new DatabaseHelper(this);

        // Bind Views
        tvWelcome = findViewById(R.id.tvHomeWelcome);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvHighestScore = findViewById(R.id.tvHighestScore);
        ivLogout = findViewById(R.id.ivLogout);
        vpDifficulty = findViewById(R.id.vpDifficulty);
        btnNavLeaderboard = findViewById(R.id.btnNavLeaderboard);
        btnNavPerformance = findViewById(R.id.btnNavPerformance);
        btnNavProfile = findViewById(R.id.btnNavProfile);

        // Setup User Greeting
        String name = sessionManager.getName();
        tvWelcome.setText("Hello, " + name + "!");

        // Setup Difficulty Carousel (ViewPager2)
        setupDifficultyCarousel();

        // Setup Navigation Clicks
        btnNavLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        btnNavPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerformanceActivity.class);
            startActivity(intent);
        });

        btnNavProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ivLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        int userId = sessionManager.getUserId();
        int attempts = databaseHelper.getTotalAttempts(userId);
        int highestScore = databaseHelper.getHighestScore(userId);

        tvTotalQuizzes.setText(String.valueOf(attempts));
        tvHighestScore.setText(highestScore + " pts");
    }

    private void setupDifficultyCarousel() {
        List<DifficultyItem> list = new ArrayList<>();
        list.add(new DifficultyItem(
                "Easy",
                "Basic climate awareness, recycling, solar power & global warming",
                10, 30, 100,
                R.color.difficulty_easy,
                R.drawable.ic_leaf
        ));
        list.add(new DifficultyItem(
                "Medium",
                "Greenhouse gases, deforestation, carbon emissions & Paris Agreement",
                10, 20, 100,
                R.color.difficulty_medium,
                R.drawable.ic_timer
        ));
        list.add(new DifficultyItem(
                "Hard",
                "Climate science, albedo effect, permafrost feedback & SDG 13 targets",
                10, 10, 100,
                R.color.difficulty_hard,
                R.drawable.ic_trophy
        ));

        DifficultyPagerAdapter adapter = new DifficultyPagerAdapter(this, list, item -> {
            // Launch Quiz with selected difficulty
            Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
            intent.putExtra("EXTRA_DIFFICULTY", item.getTitle());
            startActivity(intent);
        });

        vpDifficulty.setAdapter(adapter);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to end your current session?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
