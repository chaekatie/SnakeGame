package com.snakegame.vertical;

public class MatchDTO {
    private Long userId;
    private int totalScore;
    private int playTime;
    private String normalFoodCount;
    private String specialFoodCount;
    private String goldenFoodCount;

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

    public void setNormalFoodCount(String normal) { this.normalFoodCount = normal; }
    public String getNormalFoodCount() { return this.normalFoodCount; }

    public void setSpecialFoodCount(String special) { this.specialFoodCount = special; }
    public String getSpecialFoodCount() { return this.specialFoodCount; }

    public void setGoldenFoodCount(String golden) { this.goldenFoodCount = golden; }
    public String getGoldenFoodCount() { return this.goldenFoodCount; }
}
