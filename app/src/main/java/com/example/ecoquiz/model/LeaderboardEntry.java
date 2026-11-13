package com.example.ecoquiz.model;

/**
 * Model class representing a User's entry on the local Leaderboard.
 */
public class LeaderboardEntry {
    private int rank;
    private String username;
    private int highestScore;
    private String latestDate;

    public LeaderboardEntry(int rank, String username, int highestScore, String latestDate) {
        this.rank = rank;
        this.username = username;
        this.highestScore = highestScore;
        this.latestDate = latestDate;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(String latestDate) {
        this.latestDate = latestDate;
    }
}
