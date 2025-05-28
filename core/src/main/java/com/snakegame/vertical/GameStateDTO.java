package com.snakegame.vertical;

import java.util.List;

public class GameStateDTO {
    public List<PositionDTO> snakeBody;
    public List<FoodDTO> foods;
    public int score;
    public boolean gameOver;

    public static class PositionDTO {
        public int x;
        public int y;
    }

    public static class FoodDTO {
        public PositionDTO position;
        public FoodType type;
        public int points;
    }

    public enum FoodType {
        NORMAL,
        SPECIAL,
        GOLDEN
    }
}
