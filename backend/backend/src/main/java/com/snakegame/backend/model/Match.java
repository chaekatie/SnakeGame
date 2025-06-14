package com.snakegame.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")

public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int totalScore;
    private int playTime;
    private String normalFoodCount;
    private String specialFoodCount;
    private String goldenFoodCount;

    public Match() {}

    public Match(User user, int totalScore, int playTime, String normal, String special, String golden) {
        this.user = user;
        this.totalScore = totalScore;
        this.playTime = playTime;
        this.normalFoodCount = normal;
        this.specialFoodCount = special;
        this.goldenFoodCount = golden;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
