package com.snakegame.vertical;

public class ScoreDTO {
    private int id;
    private String username;
    private int score;
    private String time;

    public int getId() { return id; }

    public int getScore() { return score; }

    public String getUsername() { return username; }

    public String getTime() { return time; }

    public void setId(int id) { this.id = id; }

    public void setScore(int score) { this.score = score; }

    public void setUsername(String username) { this.username = username; }

    public void setTime(String time) { this.time = time; }
}
