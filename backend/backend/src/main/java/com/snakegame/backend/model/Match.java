package com.snakegame.backend.model;

import jakarta.persistence.*;

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

    private int food1Count;
    private int food2Count;
    private int food3Count;

    public Match() {}

    public Match(User user, int totalScore, int playTime, int food1, int food2, int food3) {
        this.user = user;
        this.totalScore = totalScore;
        this.playTime = playTime;
        this.food1Count = food1;
        this.food2Count = food2;
        this.food3Count = food3;
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
