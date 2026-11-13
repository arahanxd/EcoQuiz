package com.example.ecoquiz.model;

/**
 * Model class representing a completed Quiz Attempt.
 */
public class QuizAttempt {
    private int attemptId;
    private int userId;
    private String difficulty;
    private int correctAnswers;
    private int incorrectAnswers;
    private int unanswered;
    private int totalScore;
    private String attemptDate;

    public QuizAttempt() {
    }

    public QuizAttempt(int attemptId, int userId, String difficulty, int correctAnswers, 
                       int incorrectAnswers, int unanswered, int totalScore, String attemptDate) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.difficulty = difficulty;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.unanswered = unanswered;
        this.totalScore = totalScore;
        this.attemptDate = attemptDate;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(int incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public int getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(int unanswered) {
        this.unanswered = unanswered;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(String attemptDate) {
        this.attemptDate = attemptDate;
    }
}
