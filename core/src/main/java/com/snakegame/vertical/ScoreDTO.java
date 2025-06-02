package com.snakegame.vertical;

public class ScoreDTO {
    private String username;
    private int score;
    private String achievedAt;

    public ScoreDTO(String username, int score, String achievedAt){
        this.username = username;
        this.score = score;
        this.achievedAt = achievedAt;
    }

    public int getScore() { return score; }

    public String getUsername() { return username; }

    public String getAchievedAt() { return achievedAt; }

    public void setScore(int score) { this.score = score; }

    public void setUsername(String username) { this.username = username; }

    public void setAchievedAt(String achievedAt) { this.achievedAt = achievedAt; }
}
