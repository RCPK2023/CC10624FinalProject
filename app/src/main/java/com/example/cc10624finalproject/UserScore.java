package com.example.cc10624finalproject;

public class UserScore {
    // Attributes from the User class
    private int userId;
    private String name;
    private String password;

    private int scoreId;
    private int time;
    private String difficulty;
    private int score;

    public UserScore(int userId, String name, String password, int scoreId, int time, String difficulty, int score) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.scoreId = scoreId;
        this.time = time;
        this.difficulty = difficulty;
        this.score = score;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-20s %-20d %-20d", getName(), getDifficulty(), getTime(), getScore());
    }





}

