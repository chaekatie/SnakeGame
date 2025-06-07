package com.snakegame.vertical;

public enum FoodType {
    APPLE("food\\apple.png"),
    BANANA("food\\banana.png"),
    GRAPE("food\\grape.png"),
    KIWI("food\\kiwi.png"),
    STRAWBERRY("food\\strawberry.png");
    public final String texturePath;

    FoodType(String texturePath) {
        this.texturePath = texturePath;
    }
}

