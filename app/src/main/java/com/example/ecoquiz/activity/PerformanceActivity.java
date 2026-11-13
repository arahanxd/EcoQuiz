package com.example.ecoquiz.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoquiz.R;
import com.example.ecoquiz.adapter.QuizHistoryAdapter;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.model.QuizAttempt;
import com.example.ecoquiz.util.SessionManager;

import java.util.List;
import java.util.Locale;

/**
 * PerformanceActivity: Screen 7
 * Features:
 * - Comprehensive performance dashboard for the logged-in user
 * - KPI Cards: Total Quizzes Attempted, Highest Score, Average Score, Total Correct
 * - Difficulty breakdown progress bars: Easy, Medium, Hard
 * - RecyclerView displaying historical attempts (most recent first)
 * - Strict user privacy: only displays records belonging to current session user_id
 */
public class PerformanceActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTotalAttempts, tvHighestScore, tvAverageScore, tvTotalCorrect;
    private ProgressBar pbEasyAvg, pbMediumAvg, pbHardAvg;
    private TextView tvEasyAvgScore, tvMediumAvgScore, tvHardAvgScore;
    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        // Bind Views
        ivBack = findViewById(R.id.ivPerfBack);
        tvTotalAttempts = findViewById(R.id.tvPerfTotalAttempts);
        tvHighestScore = findViewById(R.id.tvPerfHighestScore);
        tvAverageScore = findViewById(R.id.tvPerfAverageScore);
        tvTotalCorrect = findViewById(R.id.tvPerfTotalCorrect);

        pbEasyAvg = findViewById(R.id.pbEasyAvg);
        pbMediumAvg = findViewById(R.id.pbMediumAvg);
        pbHardAvg = findViewById(R.id.pbHardAvg);

        tvEasyAvgScore = findViewById(R.id.tvEasyAvgScore);
        tvMediumAvgScore = findViewById(R.id.tvMediumAvgScore);
        tvHardAvgScore = findViewById(R.id.tvHardAvgScore);

        rvHistory = findViewById(R.id.rvQuizHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);

        ivBack.setOnClickListener(v -> finish());

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadPerformanceData();
    }

    private void loadPerformanceData() {
        int userId = sessionManager.getUserId();

        // High level metrics
        int totalAttempts = databaseHelper.getTotalAttempts(userId);
        int highestScore = databaseHelper.getHighestScore(userId);
        double avgScore = databaseHelper.getAverageScore(userId);
        int totalCorrect = databaseHelper.getTotalCorrectAnswers(userId);

        tvTotalAttempts.setText(String.valueOf(totalAttempts));
        tvHighestScore.setText(highestScore + " pts");
        tvAverageScore.setText(String.format(Locale.getDefault(), "%.1f pts", avgScore));
        tvTotalCorrect.setText(String.valueOf(totalCorrect));

        // Difficulty breakdowns
        double easyAvg = databaseHelper.getAverageScoreByDifficulty(userId, "Easy");
        double medAvg = databaseHelper.getAverageScoreByDifficulty(userId, "Medium");
        double hardAvg = databaseHelper.getAverageScoreByDifficulty(userId, "Hard");

        pbEasyAvg.setProgress((int) Math.round(easyAvg));
        tvEasyAvgScore.setText(String.format(Locale.getDefault(), "%.0f%%", easyAvg));

        pbMediumAvg.setProgress((int) Math.round(medAvg));
        tvMediumAvgScore.setText(String.format(Locale.getDefault(), "%.0f%%", medAvg));

        pbHardAvg.setProgress((int) Math.round(hardAvg));
        tvHardAvgScore.setText(String.format(Locale.getDefault(), "%.0f%%", hardAvg));

        // Quiz history list
        List<QuizAttempt> history = databaseHelper.getUserAttempts(userId);
        if (history == null || history.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            QuizHistoryAdapter adapter = new QuizHistoryAdapter(this, history);
            rvHistory.setAdapter(adapter);
        }
    }
}
