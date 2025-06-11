package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameStateManager {
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int MAX_FOOD = 2;

    private List<GameStateDTO.PositionDTO> snakeBody;
    private List<GameStateDTO.FoodDTO> foods;
    private int score;
    private boolean gameOver;
    private Direction currentDirection;
    private Random random;
    private boolean justAteNormalFood;
    private boolean borderlessMode;
    private int growthPending;  // Add this to track pending growth

    public GameStateManager() {
        reset();
    }

    public void reset() {
        snakeBody = new ArrayList<>();
        // Initialize snake with 3 segments
        snakeBody.add(new GameStateDTO.PositionDTO(5, 5));
        snakeBody.add(new GameStateDTO.PositionDTO(4, 5));
        snakeBody.add(new GameStateDTO.PositionDTO(3, 5));
        
        foods = new ArrayList<>();
        score = 0;
        gameOver = false;
        currentDirection = Direction.RIGHT;
        random = new Random();
        justAteNormalFood = false;
        borderlessMode = false;
        growthPending = 0;  // Initialize growth pending
        
        spawnInitialFood();
    }

    private void spawnInitialFood() {
        spawnNormalFood();
    }

    private void spawnNormalFood() {
        GameStateDTO.PositionDTO newPosition;
        do {
            newPosition = new GameStateDTO.PositionDTO(
                random.nextInt(BOARD_WIDTH),
                random.nextInt(BOARD_HEIGHT)
            );
        } while (isPositionOccupied(newPosition));
        
        GameStateDTO.FoodDTO food = new GameStateDTO.FoodDTO();
        food.position = newPosition;
        food.type = GameStateDTO.FoodType.NORMAL;
        food.points = 10;
        foods.add(food);
    }

    private void spawnSpecialOrGoldenFood() {
        GameStateDTO.PositionDTO newPosition;
        do {
            newPosition = new GameStateDTO.PositionDTO(
                random.nextInt(BOARD_WIDTH),
                random.nextInt(BOARD_HEIGHT)
            );
        } while (isPositionOccupied(newPosition));

        GameStateDTO.FoodDTO food = new GameStateDTO.FoodDTO();
        food.position = newPosition;
        // 80% chance for special food, 20% chance for golden food
        if (random.nextDouble() < 0.8) {
            food.type = GameStateDTO.FoodType.SPECIAL;
            food.points = 20;
        } else {
            food.type = GameStateDTO.FoodType.GOLDEN;
            food.points = 50;
        }
        foods.add(food);
    }

    private boolean isPositionOccupied(GameStateDTO.PositionDTO position) {
        for (GameStateDTO.PositionDTO bodyPart : snakeBody) {
            if (bodyPart.x == position.x && bodyPart.y == position.y) {
                return true;
            }
        }
        for (GameStateDTO.FoodDTO food : foods) {
            if (food.position.x == position.x && food.position.y == position.y) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        if (gameOver) return;

        GameStateDTO.PositionDTO nextPosition = getNextPosition();
        
        // Handle wall collision based on mode
        if (isWallCollision(nextPosition)) {
            if (borderlessMode) {
                nextPosition = wrapPosition(nextPosition);
            } else {
                gameOver = true;
                return;
            }
        }

        // Check self collision
        if (isSelfCollision(nextPosition)) {
            gameOver = true;
            return;
        }

        // Check food collisions
        List<GameStateDTO.FoodDTO> foodsToRemove = new ArrayList<>();
        for (GameStateDTO.FoodDTO food : foods) {
            if (nextPosition.x == food.position.x && nextPosition.y == food.position.y) {
                score += food.points;
                foodsToRemove.add(food);
                if (food.type == GameStateDTO.FoodType.NORMAL) {
                    justAteNormalFood = true;
                    growthPending += 1;  // Grow by 1 for normal food
                } else if (food.type == GameStateDTO.FoodType.SPECIAL) {
                    growthPending += 2;  // Grow by 2 for special food
                } else if (food.type == GameStateDTO.FoodType.GOLDEN) {
                    growthPending += 3;  // Grow by 3 for golden food
                }
            }
        }
        foods.removeAll(foodsToRemove);

        // Move snake
        snakeBody.add(0, nextPosition);
        
        // Handle growth
        if (growthPending > 0) {
            growthPending--;  // Decrease pending growth
        } else {
            snakeBody.remove(snakeBody.size() - 1);  // Remove tail only if not growing
        }

        // Ensure there's always one normal food
        boolean hasNormalFood = false;
        for (GameStateDTO.FoodDTO food : foods) {
            if (food.type == GameStateDTO.FoodType.NORMAL) {
                hasNormalFood = true;
                break;
            }
        }
        if (!hasNormalFood) {
            spawnNormalFood();
        }

        // Small chance to spawn special/golden food
        if (justAteNormalFood && foods.size() < MAX_FOOD && random.nextDouble() < 0.2) {
            spawnSpecialOrGoldenFood();
        }
        justAteNormalFood = false;
    }

    private GameStateDTO.PositionDTO getNextPosition() {
        GameStateDTO.PositionDTO head = snakeBody.get(0);
        GameStateDTO.PositionDTO nextPos = new GameStateDTO.PositionDTO(head.x, head.y);

        switch (currentDirection) {
            case UP: nextPos.y++; break;
            case DOWN: nextPos.y--; break;
            case LEFT: nextPos.x--; break;
            case RIGHT: nextPos.x++; break;
        }

        return nextPos;
    }

    private boolean isWallCollision(GameStateDTO.PositionDTO position) {
        return position.x < 0 || position.x >= BOARD_WIDTH ||
               position.y < 0 || position.y >= BOARD_HEIGHT;
    }

    private boolean isSelfCollision(GameStateDTO.PositionDTO position) {
        // Skip the tail since it will move away
        for (int i = 1; i < snakeBody.size() - 1; i++) {
            GameStateDTO.PositionDTO bodyPart = snakeBody.get(i);
            if (position.x == bodyPart.x && position.y == bodyPart.y) {
                return true;
            }
        }
        return false;
    }

    private GameStateDTO.PositionDTO wrapPosition(GameStateDTO.PositionDTO position) {
        int x = position.x;
        int y = position.y;

        if (x < 0) x = BOARD_WIDTH - 1;
        if (x >= BOARD_WIDTH) x = 0;
        if (y < 0) y = BOARD_HEIGHT - 1;
        if (y >= BOARD_HEIGHT) y = 0;

        return new GameStateDTO.PositionDTO(x, y);
    }

    public void changeDirection(Direction direction) {
        // Prevent 180-degree turns
        if (currentDirection == Direction.UP && direction == Direction.DOWN) return;
        if (currentDirection == Direction.DOWN && direction == Direction.UP) return;
        if (currentDirection == Direction.LEFT && direction == Direction.RIGHT) return;
        if (currentDirection == Direction.RIGHT && direction == Direction.LEFT) return;

        // Get the next position with the new direction
        Direction oldDirection = currentDirection;
        currentDirection = direction;
        GameStateDTO.PositionDTO nextPos = getNextPosition();
        
        // Check if the next position would cause immediate collision
        // Skip the tail since it will move away
        for (int i = 1; i < snakeBody.size() - 1; i++) {
            GameStateDTO.PositionDTO bodyPart = snakeBody.get(i);
            if (nextPos.x == bodyPart.x && nextPos.y == bodyPart.y) {
                // If collision would occur, revert the direction change
                currentDirection = oldDirection;
                return;
            }
        }
    }

    public void setBorderlessMode(boolean borderless) {
        this.borderlessMode = borderless;
    }

    public GameStateDTO getGameState() {
        GameStateDTO gameState = new GameStateDTO();
        gameState.snakeBody = new ArrayList<>(snakeBody);
        gameState.foods = new ArrayList<>(foods);
        gameState.score = score;
        gameState.gameOver = gameOver;
        return gameState;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public void syncWithServerState(GameStateDTO serverState) {
        if (serverState == null) return;

        // Update snake body
        snakeBody.clear();
        if (serverState.snakeBody != null) {
            snakeBody.addAll(serverState.snakeBody);
        }

        // Update foods
        foods.clear();
        if (serverState.foods != null) {
            foods.addAll(serverState.foods);
        }

        // Update other state
        score = serverState.score;
        gameOver = serverState.gameOver;
        growthPending = 0;  // Reset growth pending when syncing with server
    }
} 