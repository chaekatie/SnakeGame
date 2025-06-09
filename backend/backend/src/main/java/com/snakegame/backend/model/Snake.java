package com.snakegame.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<Position> body;
    private Direction direction;
    private boolean isAlive;
    private int growthPending;
    private Position nextPosition;  // Add this field to store the next position

    public Snake() {
        this.body = new ArrayList<>();
        this.direction = Direction.RIGHT;
        this.isAlive = true;
        this.growthPending = 0;
        // Initialize snake with 3 segments
        body.add(new Position(5, 5));
        body.add(new Position(4, 5));
        body.add(new Position(3, 5));
        this.nextPosition = getNextPosition();  // Initialize next position
    }

    public List<Position> getBody() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        // Prevent 180-degree turns
        if (this.direction == Direction.UP && direction == Direction.DOWN) return;
        if (this.direction == Direction.DOWN && direction == Direction.UP) return;
        if (this.direction == Direction.LEFT && direction == Direction.RIGHT) return;
        if (this.direction == Direction.RIGHT && direction == Direction.LEFT) return;
        this.direction = direction;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public Position getHead() {
        return body.get(0);
    }

    public void eat(Food food) {
        growthPending += food.getType().getGrowth();
    }

    public void setNextPosition(Position position) {
        this.nextPosition = position;
    }

    public Position getNextPosition() {
        if (nextPosition != null) {
            return nextPosition;
        }
        
        Position head = getHead();
        int x = head.getX();
        int y = head.getY();

        switch (direction) {
            case UP:
                y++;
                break;
            case DOWN:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        return new Position(x, y);
    }

    public void move() {
        Position newHead = getNextPosition();
        body.add(0, newHead);
        
        if (growthPending > 0) {
            growthPending--;
        } else {
            body.remove(body.size() - 1);
        }
        nextPosition = null;  // Reset next position after moving
    }
}