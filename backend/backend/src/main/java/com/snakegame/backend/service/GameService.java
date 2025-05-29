package com.snakegame.backend.service;

import com.snakegame.backend.model.Direction;
import com.snakegame.backend.model.Game;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Game game;

    public GameService() {
        this.game = new Game();
    }

    public void update() {
        game.update();
    }

    public void changeDirection(Direction direction) {
        game.changeDirection(direction);
    }

    public void reset() {
        game.reset();
    }

    public Game getGame() {
        return game;
    }
} 