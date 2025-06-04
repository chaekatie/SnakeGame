package com.snakegame.vertical;

public class UserHighScoreDTO {
    private String username;
    private int highscore;

    public void setUsername(String username) { this.username = username; }
    public void setHighScore(int highScore) { this.highscore = highScore; }

    public String getUsername() { return this.username; }
    public int getHighScore() { return this.highscore; }
}
