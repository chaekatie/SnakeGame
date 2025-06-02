package com.snakegame.backend.dto;

import java.time.LocalDateTime;

public class ScoreRequest {
    private String username;
    private int score;
    private LocalDateTime achievedAt;

    public ScoreRequest(String username, int score, LocalDateTime achievedAt){
        this.username = username;
        this.score = score;
        this.achievedAt = achievedAt;
    }

    public int getScore() { return score; }

    public String getUsername() { return username; }

    public LocalDateTime getAchievedAt() { return achievedAt; }

    public void setScore(int score) { this.score = score; }

    public void setUsername(String username) { this.username = username; }

    public void setAchievedAt(LocalDateTime achievedAt) { this.achievedAt = achievedAt; }
}
