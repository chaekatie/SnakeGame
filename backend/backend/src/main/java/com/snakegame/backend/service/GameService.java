package com.snakegame.backend.service;

import com.snakegame.backend.model.Direction;
import com.snakegame.backend.model.Game;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class GameService {
    private Game game;

    public GameService() {
        this.game = new Game();
    }

    @PostConstruct
    public void init() {
        reset();
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

    public void setBorderlessMode(boolean borderless) {
        game.setBorderlessMode(borderless);
    }

    public Game getGame() {
        return game;
    }
} 