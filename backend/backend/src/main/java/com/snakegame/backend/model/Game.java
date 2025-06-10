package com.snakegame.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int MAX_FOOD = 2; // Allow up to 2 foods (1 normal + 1 special/golden)
    
    private Snake snake;
    private List<Food> foods;
    private int score;
    private boolean gameOver;
    private Random random;
    private boolean justAteNormalFood;  // Track if we just ate normal food
    private boolean borderlessMode;      // Track if we're in borderless mode

    public Game() {
        this.snake = new Snake();
        this.foods = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.gameOver = false;
        this.justAteNormalFood = false;
        this.borderlessMode = false;     // Default to bordered mode
        spawnInitialFood();
    }

    public void setBorderlessMode(boolean borderless) {
        this.borderlessMode = borderless;
    }

    private void spawnInitialFood() {
        // Always spawn one normal food
        spawnNormalFood();
    }

    public void update() {
        if (gameOver) return;

        Position nextPosition = snake.getNextPosition();
        
        // Handle wall collision based on mode
        if (isWallCollision(nextPosition)) {
            if (borderlessMode) {
                // Wrap around the screen
                nextPosition = wrapPosition(nextPosition);
                // Update the snake's next position
                snake.setNextPosition(nextPosition);
            } else {
                gameOver = true;
                snake.setAlive(false);
                return;
            }
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
                 // Set flag if we ate normal food
                 if (food.getType() == Food.FoodType.NORMAL) {
                    justAteNormalFood = true;
                }
            }
        }
        foods.removeAll(foodsToRemove);

        // Move snake
        snake.move();

        // Ensure there's always one normal food
        boolean hasNormalFood = false;
        for (Food food : foods) {
            if (food.getType() == Food.FoodType.NORMAL) {
                hasNormalFood = true;
                break;
            }
        }
        if (!hasNormalFood) {
            spawnNormalFood();
        }
   
        // Small chance to spawn special/golden food only after eating normal food
        if (justAteNormalFood && foods.size() < MAX_FOOD && random.nextDouble() < 0.2) { // 20% chance
            spawnSpecialOrGoldenFood();
        }
        justAteNormalFood = false;  // Reset the flag
       }
   
       private void spawnNormalFood() {
        Position newPosition;
        do {
            newPosition = new Position(
                random.nextInt(BOARD_WIDTH),
                random.nextInt(BOARD_HEIGHT)
            );
        } while (isPositionOccupied(newPosition));
        foods.add(new Food(newPosition, Food.FoodType.NORMAL));
    }

    private void spawnSpecialOrGoldenFood() {
        Position newPosition;
        do {
            newPosition = new Position(
                random.nextInt(BOARD_WIDTH),
                random.nextInt(BOARD_HEIGHT)
            );
        } while (isPositionOccupied(newPosition));

        // 80% chance for special food, 20% chance for golden food
        Food.FoodType type = random.nextDouble() < 0.8 ? Food.FoodType.SPECIAL : Food.FoodType.GOLDEN;
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

    private Position wrapPosition(Position position) {
        int x = position.getX();
        int y = position.getY();

        // Wrap horizontally
        if (x < 0) x = BOARD_WIDTH - 1;
        if (x >= BOARD_WIDTH) x = 0;

        // Wrap vertically
        if (y < 0) y = BOARD_HEIGHT - 1;
        if (y >= BOARD_HEIGHT) y = 0;

        return new Position(x, y);
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
        // Create a new snake instance
        this.snake = new Snake();
        // Clear and reinitialize foods list
        this.foods = new ArrayList<>();
        // Reset score
        this.score = 0;
        // Reset game over state
        this.gameOver = false;
        // Spawn initial food
        spawnInitialFood();
    }
}