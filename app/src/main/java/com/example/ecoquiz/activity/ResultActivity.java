package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ecoquiz.R;
import com.google.android.material.button.MaterialButton;

/**
 * ResultActivity: Screen 6
 * Features:
 * - Selected difficulty badge
 * - Total questions, correct answers, incorrect answers, unanswered count
 * - Total score out of 100 points
 * - Percentage calculation: (Correct / 10) * 100
 * - Performance category & tailored motivational message:
 *     90-100: Excellent!
 *     70-80:  Very Good!
 *     50-60:  Good!
 *     30-40:  Needs Improvement!
 *     0-20:   Keep Learning!
 * - Action buttons: Play Again, View Performance, Back to Home
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvCategory, tvMotivationalMessage, tvDifficultyBadge;
    private TextView tvTotalScore, tvPercentage;
    private TextView tvCorrect, tvIncorrect, tvUnanswered;
    private ImageView ivResultTrophy;
    private MaterialButton btnPlayAgain, btnViewPerformance;
    private TextView tvBackToHome;

    private String difficulty = "Easy";
    private int correct = 0;
    private int incorrect = 0;
    private int unanswered = 0;
    private int totalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Bind Views
        tvCategory = findViewById(R.id.tvPerformanceCategory);
        tvMotivationalMessage = findViewById(R.id.tvMotivationalMessage);
        tvDifficultyBadge = findViewById(R.id.tvResultDifficultyBadge);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvCorrect = findViewById(R.id.tvResultCorrect);
        tvIncorrect = findViewById(R.id.tvResultIncorrect);
        tvUnanswered = findViewById(R.id.tvResultUnanswered);
        ivResultTrophy = findViewById(R.id.ivResultTrophy);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnViewPerformance = findViewById(R.id.btnViewPerformance);
        tvBackToHome = findViewById(R.id.tvBackToHome);

        // Extract Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            difficulty = intent.getStringExtra("EXTRA_DIFFICULTY");
            if (difficulty == null) difficulty = "Easy";
            correct = intent.getIntExtra("EXTRA_CORRECT", 0);
            incorrect = intent.getIntExtra("EXTRA_INCORRECT", 0);
            unanswered = intent.getIntExtra("EXTRA_UNANSWERED", 0);
            totalScore = intent.getIntExtra("EXTRA_TOTAL_SCORE", correct * 10);
        }

        // Calculations
        int percentage = correct * 10; // (correct / 10) * 100

        // Populate Views
        tvDifficultyBadge.setText("DIFFICULTY: " + difficulty.toUpperCase());
        tvTotalScore.setText(String.valueOf(totalScore));
        tvPercentage.setText("(" + percentage + "%)");
        tvCorrect.setText(String.valueOf(correct));
        tvIncorrect.setText(String.valueOf(incorrect));
        tvUnanswered.setText(String.valueOf(unanswered));

        // Evaluate Performance Category
        evaluatePerformance(totalScore);

        // Listeners
        btnPlayAgain.setOnClickListener(v -> {
            Intent playIntent = new Intent(ResultActivity.this, QuizActivity.class);
            playIntent.putExtra("EXTRA_DIFFICULTY", difficulty);
            startActivity(playIntent);
            finish();
        });

        btnViewPerformance.setOnClickListener(v -> {
            Intent perfIntent = new Intent(ResultActivity.this, PerformanceActivity.class);
            startActivity(perfIntent);
            finish();
        });

        tvBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ResultActivity.this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });

        // Handle back press using modern OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent homeIntent = new Intent(ResultActivity.this, HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
                finish();
            }
        });
    }

    private void evaluatePerformance(int score) {
        if (score >= 90) {
            tvCategory.setText("Excellent!");
            tvMotivationalMessage.setText("Outstanding! You possess deep climate action mastery and lead the way for a sustainable future.");
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.primary_forest_green));
            ivResultTrophy.setImageResource(R.drawable.ic_trophy);
        } else if (score >= 70) {
            tvCategory.setText("Very Good!");
            tvMotivationalMessage.setText("Great effort! You understand key climate concepts and are well on your way to becoming an Eco Leader.");
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else if (score >= 50) {
            tvCategory.setText("Good!");
            tvMotivationalMessage.setText("Good attempt! Review a few more environmental topics to boost your climate awareness score.");
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.accent_gold_dark));
        } else if (score >= 30) {
            tvCategory.setText("Needs Improvement!");
            tvMotivationalMessage.setText("Every step counts! Take time to explore more climate facts and test yourself again.");
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.difficulty_medium));
        } else {
            tvCategory.setText("Keep Learning!");
            tvMotivationalMessage.setText("Climate change is urgent. Start by learning the basics of clean energy and conservation.");
            tvCategory.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        }
    }
}
