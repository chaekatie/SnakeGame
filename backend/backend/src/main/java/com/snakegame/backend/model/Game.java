package com.snakegame.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int MAX_FOOD = 3;
    
    private Snake snake;
    private List<Food> foods;
    private int score;
    private boolean gameOver;
    private Random random;

    public Game() {
        this.snake = new Snake();
        this.foods = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.gameOver = false;
        spawnInitialFood();
    }

    private void spawnInitialFood() {
        for (int i = 0; i < MAX_FOOD; i++) {
            spawnFood();
        }
    }

    public void update() {
        if (gameOver) return;

        Position nextPosition = snake.getNextPosition();
        
        // Check wall collision
        if (isWallCollision(nextPosition)) {
            gameOver = true;
            snake.setAlive(false);
            return;
        }

        // Check self collision
        if (isSelfCollision(nextPosition)) {
            gameOver = true;
            snake.setAlive(false);
            return;
        }

        // Check food collisions
        List<Food> foodsToRemove = new ArrayList<>();
        for (Food food : foods) {
            if (nextPosition.equals(food.getPosition())) {
                snake.eat(food);
                score += food.getPoints();
                foodsToRemove.add(food);
            }
        }
        foods.removeAll(foodsToRemove);

        // Move snake
        snake.move();

        // Spawn new food if needed
        while (foods.size() < MAX_FOOD) {
            spawnFood();
        }
    }

    private void spawnFood() {
        Position newPosition;
        do {
            newPosition = new Position(
                random.nextInt(BOARD_WIDTH),
                random.nextInt(BOARD_HEIGHT)
            );
        } while (isPositionOccupied(newPosition));

        // Randomly select food type with different probabilities
        double chance = random.nextDouble();
        Food.FoodType type;
        if (chance < 0.7) { // 70% chance for normal food
            type = Food.FoodType.NORMAL;
        } else if (chance < 0.9) { // 20% chance for special food
            type = Food.FoodType.SPECIAL;
        } else { // 10% chance for golden food
            type = Food.FoodType.GOLDEN;
        }

        foods.add(new Food(newPosition, type));
    }

    private boolean isPositionOccupied(Position position) {
        if (snake.getBody().contains(position)) {
            return true;
        }
        for (Food food : foods) {
            if (food.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWallCollision(Position position) {
        return position.getX() < 0 || position.getX() >= BOARD_WIDTH ||
               position.getY() < 0 || position.getY() >= BOARD_HEIGHT;
    }

    private boolean isSelfCollision(Position position) {
        return snake.getBody().contains(position);
    }

    public void changeDirection(Direction direction) {
        snake.setDirection(direction);
    }

    public Snake getSnake() {
        return snake;
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

    public void reset() {
        this.snake = new Snake();
        this.foods.clear();
        this.score = 0;
        this.gameOver = false;
        spawnInitialFood();
    }
}