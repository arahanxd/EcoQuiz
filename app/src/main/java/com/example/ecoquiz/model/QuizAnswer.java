package com.example.ecoquiz.model;

/**
 * Model class representing an individual question answer inside a quiz attempt.
 */
public class QuizAnswer {
    private int answerId;
    private int attemptId;
    private int questionId;
    private String selectedOption;
    private int isCorrect; // 1 = true, 0 = false

    public QuizAnswer() {
    }

    public QuizAnswer(int answerId, int attemptId, int questionId, String selectedOption, int isCorrect) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }
}
