package com.snakegame.backend.dto;

public class MatchRequest {
    private Long userId;
    private int totalScore;
    private int playTime;
    private int food1Count;
    private int food2Count;
    private int food3Count;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getFood1Count() {
        return food1Count;
    }

    public void setFood1Count(int food1Count) {
        this.food1Count = food1Count;
    }

    public int getFood2Count() {
        return food2Count;
    }

    public void setFood2Count(int food2Count) {
        this.food2Count = food2Count;
    }

    public int getFood3Count() {
        return food3Count;
    }

    public void setFood3Count(int food3Count) {
        this.food3Count = food3Count;
    }
}
