package com.snakegame.backend.model;

public class Food {
    private Position position;
    private FoodType type;
    private int points;

    public Food(Position position, FoodType type) {
        this.position = position;
        this.type = type;
        this.points = type.getPoints();
    }

    public Position getPosition() {
        return position;
    }

    public FoodType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public enum FoodType {
        NORMAL(10, 1),    // Regular food, grows snake by 1
        SPECIAL(20, 2),   // Special food, grows snake by 2
        GOLDEN(50, 3);    // Golden food, grows snake by 3

        private final int points;
        private final int growth;

        FoodType(int points, int growth) {
            this.points = points;
            this.growth = growth;
        }

        public int getPoints() {
            return points;
        }

        public int getGrowth() {
            return growth;
        }
    }
} 