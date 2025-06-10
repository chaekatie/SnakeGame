package com.snakegame.vertical;

public class MatchDTO {
    private long id;
    private UserDTO user;
    private int totalScore;
    private int playTime;
    private String normalFoodCount;
    private String specialFoodCount;
    private String goldenFoodCount;

    public MatchDTO() {}

    public MatchDTO(MatchDTO match){
        this.id = match.id;
        this.user = match.user;
        this.totalScore = match.totalScore;
        this.normalFoodCount = match.normalFoodCount;
        this.specialFoodCount = match.specialFoodCount;
        this.goldenFoodCount = match.goldenFoodCount;
        this.playTime = match.playTime;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public UserDTO getUser() {
        return user;
    }

    public void setUserId(UserDTO user) {
        this.user = user;
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
