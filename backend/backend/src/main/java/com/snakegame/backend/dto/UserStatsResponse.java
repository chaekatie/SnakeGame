package com.snakegame.backend.dto;

public class UserStatsResponse {
    private int totalMatches;
    private int totalFood1;
    private int totalFood2;
    private int totalFood3;
    private int totalPlayTime;
    private int highestScore;

    // Constructor
    public UserStatsResponse(int totalMatches, int totalFood1, int totalFood2, int totalFood3, int totalPlayTime, int highestScore) {
        this.totalMatches = totalMatches;
        this.totalFood1 = totalFood1;
        this.totalFood2 = totalFood2;
        this.totalFood3 = totalFood3;
        this.totalPlayTime = totalPlayTime;
        this.highestScore = highestScore;
    }

    public UserStatsResponse() {
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public int getTotalFood1() {
        return totalFood1;
    }

    public void setTotalFood1(int totalFood1) {
        this.totalFood1 = totalFood1;
    }

    public int getTotalFood2() {
        return totalFood2;
    }

    public void setTotalFood2(int totalFood2) {
        this.totalFood2 = totalFood2;
    }

    public int getTotalFood3() {
        return totalFood3;
    }

    public void setTotalFood3(int totalFood3) {
        this.totalFood3 = totalFood3;
    }

    public int getTotalPlayTime() {
        return totalPlayTime;
    }

    public void setTotalPlayTime(int totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
}
