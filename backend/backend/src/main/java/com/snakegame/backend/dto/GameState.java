package com.snakegame.backend.dto;

import java.util.List;
import com.snakegame.backend.model.Position;
import com.snakegame.backend.model.Food;

public class GameState {
    private List<Position> snakeBody;
    private List<Food> foods;
    private int score;
    private boolean gameOver;

    public GameState(List<Position> snakeBody, List<Food> foods, int score, boolean gameOver) {
        this.snakeBody = snakeBody;
        this.foods = foods;
        this.score = score;
        this.gameOver = gameOver;
    }

    public List<Position> getSnakeBody() {
        return snakeBody;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}