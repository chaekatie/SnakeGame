package com.snakegame.vertical;

import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GameStateDTO implements Json.Serializable {
    public List<PositionDTO> snakeBody;
    public List<FoodDTO> foods;
    public int score;
    public boolean gameOver;

    public GameStateDTO() {
        this.snakeBody = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.score = 0;
        this.gameOver = false;
    }

    public static class PositionDTO implements Json.Serializable {
        public int x;
        public int y;

        public PositionDTO() {
            this.x = 0;
            this.y = 0;
        }

        @Override
        public void write(Json json) {
            json.writeValue("x", x);
            json.writeValue("y", y);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            if (jsonData.has("x")) x = jsonData.getInt("x");
            if (jsonData.has("y")) y = jsonData.getInt("y");
        }
    }

    public static class FoodDTO implements Json.Serializable {
        public PositionDTO position;
        public FoodType type;
        public int points;

        public FoodDTO() {
            this.position = new PositionDTO();
            this.type = FoodType.NORMAL;
            this.points = 10;
        }

        @Override
        public void write(Json json) {
            json.writeValue("position", position);
            json.writeValue("type", type);
            json.writeValue("points", points);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            if (jsonData.has("position")) {
                position = json.readValue("position", PositionDTO.class, jsonData);
            }
            if (jsonData.has("type")) {
                type = FoodType.valueOf(jsonData.getString("type"));
            }
            if (jsonData.has("points")) {
                points = jsonData.getInt("points");
            }
        }
    }

    public enum FoodType {
        NORMAL,
        SPECIAL,
        GOLDEN
    }

    @Override
    public void write(Json json) {
        json.writeValue("snakeBody", snakeBody);
        json.writeValue("foods", foods);
        json.writeValue("score", score);
        json.writeValue("gameOver", gameOver);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        if (jsonData.has("snakeBody")) {
            snakeBody = json.readValue("snakeBody", List.class, PositionDTO.class, jsonData);
        }
        if (jsonData.has("foods")) {
            foods = json.readValue("foods", List.class, FoodDTO.class, jsonData);
        }
        if (jsonData.has("score")) {
            score = jsonData.getInt("score");
        }
        if (jsonData.has("gameOver")) {
            gameOver = jsonData.getBoolean("gameOver");
        }
    }
}
