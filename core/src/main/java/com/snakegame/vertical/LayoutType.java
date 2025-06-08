package com.snakegame.vertical;

public enum LayoutType {
    GRASS("layouts\\gridtile1.png", "layouts\\gridtile2.png"),
    BASIC("layouts\\layout1_1.png", "layouts\\layout1_2.png"),
    BROWNBOXES("layouts\\boxes2_1.png", "layouts\\boxes2_2.png"),
    GREENBOXES("layouts\\boxes1_1.png", "layouts\\boxes1_2.png");
    public final String texturePath1;
    public final String texturePath2;

    LayoutType(String texturePath1, String texturePath2) {
        this.texturePath1 = texturePath1;
        this.texturePath2 = texturePath2;
    }
}

