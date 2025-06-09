package com.snakegame.backend.controller;

import com.snakegame.backend.dto.GameState;
import com.snakegame.backend.model.Direction;
import com.snakegame.backend.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/state")
    public GameState getGameState() {
        var game = gameService.getGame();
        return new GameState(
            game.getSnake().getBody(),
            game.getFoods(),
            game.getScore(),
            game.isGameOver()
        );
    }

    @PostMapping("/direction")
    public void changeDirection(@RequestBody Direction direction) {
        gameService.changeDirection(direction);
    }

    @PostMapping("/update")
    public void update() {
        gameService.update();
    }

    @PostMapping("/reset")
    public void reset() {
        gameService.reset();
    }
}