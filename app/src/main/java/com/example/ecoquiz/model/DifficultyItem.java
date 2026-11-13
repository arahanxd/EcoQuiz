package com.example.ecoquiz.model;

/**
 * Model class for difficulty level card shown in Home dashboard carousel.
 */
public class DifficultyItem {
    private String title;          // Easy, Medium, Hard
    private String subtitle;       // Topic focus
    private int questionsCount;    // 10
    private int timeSeconds;       // 30, 20, 10
    private int maxScore;          // 100
    private int colorResId;        // Difficulty color
    private int iconResId;

    public DifficultyItem(String title, String subtitle, int questionsCount, int timeSeconds, int maxScore, int colorResId, int iconResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.questionsCount = questionsCount;
        this.timeSeconds = timeSeconds;
        this.maxScore = maxScore;
        this.colorResId = colorResId;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getColorResId() {
        return colorResId;
    }

    public int getIconResId() {
        return iconResId;
    }
}
