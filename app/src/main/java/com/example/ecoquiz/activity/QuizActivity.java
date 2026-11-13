package com.example.ecoquiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ecoquiz.R;
import com.example.ecoquiz.database.DatabaseHelper;
import com.example.ecoquiz.model.Question;
import com.example.ecoquiz.model.QuizAnswer;
import com.example.ecoquiz.model.QuizAttempt;
import com.example.ecoquiz.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * QuizActivity: Screen 5 (Core Quiz Interface)
 * Features:
 * - Loads exactly 10 questions randomized from ecoquiz.db for selected difficulty
 * - Accurate CountDownTimer: Easy=30s, Medium=20s, Hard=10s
 * - Safe timer cancellation before re-scheduling or Activity destruction
 * - Single selection enforcement: prevents multiple score increments
 * - Automatic timeout handling (marks question unanswered and shows feedback)
 * - Persists completed attempt and answers into SQLite
 * - Passes summary statistics to ResultActivity via Intent extras
 */
public class QuizActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvDifficultyBadge, tvQuizTimer, tvQuestionCounter, tvQuestionText;
    private ProgressBar pbQuizProgress;

    private RelativeLayout layoutA, layoutB, layoutC, layoutD;
    private TextView tvTextA, tvTextB, tvTextC, tvTextD;
    private TextView tvBadgeA, tvBadgeB, tvBadgeC, tvBadgeD;

    private MaterialCardView cardExplanation;
    private TextView tvFeedbackTitle, tvExplanationText;
    private MaterialButton btnNext;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private String difficulty = "Easy";
    private long timerDurationMillis = 30000; // Default Easy 30s
    private CountDownTimer countDownTimer;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private boolean isAnswerSubmitted = false;

    // Running scores
    private int correctCount = 0;
    private int incorrectCount = 0;
    private int unansweredCount = 0;
    private int totalScore = 0;

    // Track answers to save in QuizAnswers table
    private final List<QuizAnswer> userAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        // Retrieve selected difficulty from Intent
        if (getIntent().hasExtra("EXTRA_DIFFICULTY")) {
            difficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");
        }

        // Set timer based on difficulty: Easy=30s, Medium=20s, Hard=10s
        if ("Medium".equalsIgnoreCase(difficulty)) {
            timerDurationMillis = 20000;
        } else if ("Hard".equalsIgnoreCase(difficulty)) {
            timerDurationMillis = 10000;
        } else {
            timerDurationMillis = 30000;
        }

        bindViews();
        setupListeners();

        // Handle back press using modern OnBackPressedCallback (replaces deprecated onBackPressed)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmQuitQuiz();
            }
        });

        // Load 10 questions for this difficulty
        questionList = databaseHelper.getQuestionsByDifficulty(difficulty);

        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "No questions found for " + difficulty, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDifficultyBadge.setText(difficulty.toUpperCase());
        displayQuestion(currentQuestionIndex);
    }

    private void bindViews() {
        ivBack = findViewById(R.id.ivQuizBack);
        tvDifficultyBadge = findViewById(R.id.tvQuizDifficultyBadge);
        tvQuizTimer = findViewById(R.id.tvQuizTimer);
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        pbQuizProgress = findViewById(R.id.pbQuizProgress);

        layoutA = findViewById(R.id.layoutOptionA);
        layoutB = findViewById(R.id.layoutOptionB);
        layoutC = findViewById(R.id.layoutOptionC);
        layoutD = findViewById(R.id.layoutOptionD);

        tvTextA = findViewById(R.id.tvOptionAText);
        tvTextB = findViewById(R.id.tvOptionBText);
        tvTextC = findViewById(R.id.tvOptionCText);
        tvTextD = findViewById(R.id.tvOptionDText);

        tvBadgeA = findViewById(R.id.tvOptionABadge);
        tvBadgeB = findViewById(R.id.tvOptionBBadge);
        tvBadgeC = findViewById(R.id.tvOptionCBadge);
        tvBadgeD = findViewById(R.id.tvOptionDBadge);

        cardExplanation = findViewById(R.id.cardExplanation);
        tvFeedbackTitle = findViewById(R.id.tvFeedbackTitle);
        tvExplanationText = findViewById(R.id.tvExplanationText);
        btnNext = findViewById(R.id.btnNextQuestion);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> confirmQuitQuiz());

        layoutA.setOnClickListener(v -> handleOptionSelected("A"));
        layoutB.setOnClickListener(v -> handleOptionSelected("B"));
        layoutC.setOnClickListener(v -> handleOptionSelected("C"));
        layoutD.setOnClickListener(v -> handleOptionSelected("D"));

        btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                displayQuestion(currentQuestionIndex);
            } else {
                finishQuiz();
            }
        });
    }

    private void displayQuestion(int index) {
        // Reset states
        isAnswerSubmitted = false;
        cancelTimer();

        // Reset UI backgrounds
        resetOptionLayouts();
        enableOptionClicks(true);
        cardExplanation.setVisibility(View.GONE);
        btnNext.setEnabled(false);

        // Update progress & counter
        pbQuizProgress.setProgress(index + 1);
        tvQuestionCounter.setText("Question " + (index + 1) + " of " + questionList.size());

        if (index == questionList.size() - 1) {
            btnNext.setText(R.string.btn_finish);
        } else {
            btnNext.setText(R.string.btn_next);
        }

        // Set question data
        Question q = questionList.get(index);
        tvQuestionText.setText(q.getQuestion());
        tvTextA.setText(q.getOptionA());
        tvTextB.setText(q.getOptionB());
        tvTextC.setText(q.getOptionC());
        tvTextD.setText(q.getOptionD());

        // Start fresh timer for current question
        startTimer(timerDurationMillis);
    }

    private void startTimer(long millis) {
        cancelTimer();
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000) + 1;
                tvQuizTimer.setText(secondsRemaining + "s");
            }

            @Override
            public void onFinish() {
                tvQuizTimer.setText("0s");
                handleTimeExpired();
            }
        }.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void handleOptionSelected(String selectedOption) {
        if (isAnswerSubmitted) return; // Prevent multiple clicks
        isAnswerSubmitted = true;
        cancelTimer();
        enableOptionClicks(false);

        Question q = questionList.get(currentQuestionIndex);
        boolean isCorrect = selectedOption.equalsIgnoreCase(q.getCorrectOption());

        // Highlight selected and correct options
        highlightAnswers(selectedOption, q.getCorrectOption());

        if (isCorrect) {
            correctCount++;
            totalScore += 10;
            tvFeedbackTitle.setText("Correct! +10 Points");
            tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else {
            incorrectCount++;
            tvFeedbackTitle.setText("Incorrect! Correct answer is: Option " + q.getCorrectOption());
            tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        }

        // Record for DB
        userAnswers.add(new QuizAnswer(0, 0, q.getQuestionId(), selectedOption, isCorrect ? 1 : 0));

        // Show explanation
        tvExplanationText.setText(q.getExplanation() != null ? q.getExplanation() : "");
        cardExplanation.setVisibility(View.VISIBLE);
        btnNext.setEnabled(true);
    }

    private void handleTimeExpired() {
        if (isAnswerSubmitted) return;
        isAnswerSubmitted = true;
        enableOptionClicks(false);

        Question q = questionList.get(currentQuestionIndex);
        unansweredCount++;

        // Highlight correct option
        highlightAnswers(null, q.getCorrectOption());

        tvFeedbackTitle.setText("Time Out! Correct answer is: Option " + q.getCorrectOption());
        tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        tvExplanationText.setText(q.getExplanation() != null ? q.getExplanation() : "");
        cardExplanation.setVisibility(View.VISIBLE);

        // Record unanswered in DB answers
        userAnswers.add(new QuizAnswer(0, 0, q.getQuestionId(), "None", 0));
        btnNext.setEnabled(true);
    }

    private void highlightAnswers(String selected, String correct) {
        // Highlight correct as green
        if ("A".equalsIgnoreCase(correct)) layoutA.setBackgroundResource(R.drawable.bg_option_correct);
        if ("B".equalsIgnoreCase(correct)) layoutB.setBackgroundResource(R.drawable.bg_option_correct);
        if ("C".equalsIgnoreCase(correct)) layoutC.setBackgroundResource(R.drawable.bg_option_correct);
        if ("D".equalsIgnoreCase(correct)) layoutD.setBackgroundResource(R.drawable.bg_option_correct);

        // If user picked wrong option, highlight it red
        if (selected != null && !selected.equalsIgnoreCase(correct)) {
            if ("A".equalsIgnoreCase(selected)) layoutA.setBackgroundResource(R.drawable.bg_option_incorrect);
            if ("B".equalsIgnoreCase(selected)) layoutB.setBackgroundResource(R.drawable.bg_option_incorrect);
            if ("C".equalsIgnoreCase(selected)) layoutC.setBackgroundResource(R.drawable.bg_option_incorrect);
            if ("D".equalsIgnoreCase(selected)) layoutD.setBackgroundResource(R.drawable.bg_option_incorrect);
        }
    }

    private void resetOptionLayouts() {
        layoutA.setBackgroundResource(R.drawable.bg_option_default);
        layoutB.setBackgroundResource(R.drawable.bg_option_default);
        layoutC.setBackgroundResource(R.drawable.bg_option_default);
        layoutD.setBackgroundResource(R.drawable.bg_option_default);
    }

    private void enableOptionClicks(boolean enabled) {
        layoutA.setEnabled(enabled);
        layoutB.setEnabled(enabled);
        layoutC.setEnabled(enabled);
        layoutD.setEnabled(enabled);
    }

    private void finishQuiz() {
        cancelTimer();

        // Save Attempt to SQLite
        int userId = sessionManager.getUserId();
        QuizAttempt attempt = new QuizAttempt(
                0,
                userId,
                difficulty,
                correctCount,
                incorrectCount,
                unansweredCount,
                totalScore,
                null // let DatabaseHelper generate current date
        );

        long attemptId = databaseHelper.insertQuizAttempt(attempt, userAnswers);

        // Pass results to ResultActivity via Intent Extras
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("EXTRA_ATTEMPT_ID", attemptId);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty);
        intent.putExtra("EXTRA_TOTAL_QUESTIONS", 10);
        intent.putExtra("EXTRA_CORRECT", correctCount);
        intent.putExtra("EXTRA_INCORRECT", incorrectCount);
        intent.putExtra("EXTRA_UNANSWERED", unansweredCount);
        intent.putExtra("EXTRA_TOTAL_SCORE", totalScore);
        startActivity(intent);
        finish();
    }

    private void confirmQuitQuiz() {
        new AlertDialog.Builder(this)
                .setTitle("Quit Quiz?")
                .setMessage("Your current quiz progress will be lost. Are you sure you want to exit?")
                .setPositiveButton("Quit", (dialog, which) -> {
                    cancelTimer();
                    finish();
                })
                .setNegativeButton("Continue", null)
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
}
