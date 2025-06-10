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
        // Get current head position
        Position head = getHead();

        // Calculate what the next position would be with the new direction
        Position nextPos = new Position(head.getX(), head.getY());
        switch (direction) {
            case UP: nextPos.setY(head.getY() + 1); break;
            case DOWN: nextPos.setY(head.getY() - 1); break;
            case LEFT: nextPos.setX(head.getX() - 1); break;
            case RIGHT: nextPos.setX(head.getX() + 1); break;
        }

        // Prevent 180-degree turns
        if (this.direction == Direction.UP && direction == Direction.DOWN) return;
        if (this.direction == Direction.DOWN && direction == Direction.UP) return;
        if (this.direction == Direction.LEFT && direction == Direction.RIGHT) return;
        if (this.direction == Direction.RIGHT && direction == Direction.LEFT) return;
        this.direction = direction;

        // Prevent moves that would cause immediate self-collision
        // Skip the tail since it will move away
        for (int i = 0; i < body.size() - 1; i++) {
            if (nextPos.equals(body.get(i))) {
                return; // Don't allow the move if it would cause collision
            }
        }
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