package com.example.cc10624finalproject;

public class Score {

    private int time;
    private String difficulty;
    private int score;

    public Score(int time, String difficulty, int score){
        this.time = time;
        this.difficulty = difficulty;
        this.score = score;
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
        return String.format("%-25s %-25d %-10d", difficulty, time, score);
    }

}
