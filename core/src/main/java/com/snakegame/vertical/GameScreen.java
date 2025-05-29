package com.snakegame.vertical;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private final SnakeGame game;
    private Texture gridTile, backgroundTexture;
    private Image backgroundImage;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private final int rows = 40;
    private final int cols = 40;
    private int cellSize = 48;
    private Table boardTable, topBarTable, snakeTable;
    private Texture foodTexture, cupTexture, pauseTexture;
    private Texture continueTexture, soundOnTexture, soundOffTexture;
    private Image foodImage, cupImage, snakeImage;
    private ImageButton pauseButton, continueButton, soundOnButton, soundOffButton;
    private int hasSound;

    public GameScreen(SnakeGame game){
        this.game = game;

        OrthographicCamera camera = new OrthographicCamera();
        gridTile = new Texture("backgrounds\\gridtile1.png");
        batch = new SpriteBatch();
        viewport = new FitViewport(game.V_WIDTH,game.V_HEIGHT, camera);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("backgrounds\\bgempty.png");
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        backgroundImage = new Image(background);
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);

        //region Top bar
        topBarTable = new Table();
        topBarTable.setFillParent(true);

        pauseTexture = new Texture("buttons\\pause.png");
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTexture));
        pauseButton = new ImageButton(pauseDrawable);
        game.buttonAnimation(pauseButton);

        continueTexture = new Texture("buttons\\play.png");
        TextureRegionDrawable continueDrawable = new TextureRegionDrawable(new TextureRegion(continueTexture));
        continueButton = new ImageButton(continueDrawable);
        game.buttonAnimation(continueButton);

        soundOnTexture = new Texture("buttons\\soundon.png");
        TextureRegionDrawable soundOnDrawable = new TextureRegionDrawable(new TextureRegion(soundOnTexture));
        soundOnButton = new ImageButton(soundOnDrawable);
        game.buttonAnimation(soundOnButton);

        soundOffTexture = new Texture("buttons\\soundoff.png");
        TextureRegionDrawable soundOffDrawable = new TextureRegionDrawable(new TextureRegion(soundOffTexture));
        soundOffButton = new ImageButton(soundOffDrawable);
        game.buttonAnimation(soundOffButton);

        soundOffButton.setVisible(false);
        soundOffButton.setTouchable(Touchable.disabled);
        soundOnButton.setVisible(true);
        soundOnButton.setTouchable(Touchable.enabled);

        soundOnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("sound on button clicked!");
                game.clicking.play(3f);
                soundOffButton.setVisible(true);
                soundOffButton.setTouchable(Touchable.enabled);
                soundOnButton.setVisible(false);
                soundOnButton.setTouchable(Touchable.disabled);
            }
        });

        soundOffButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("sound off button clicked!");
                game.clicking.play(3f);
                soundOnButton.setVisible(true);
                soundOnButton.setTouchable(Touchable.enabled);
                soundOffButton.setVisible(false);
                soundOffButton.setTouchable(Touchable.disabled);
            }
        });

        Stack soundBtnStack = new Stack();
        soundBtnStack.add(soundOffButton);
        soundBtnStack.add(soundOnButton);

        topBarTable.top().padTop(100);
        topBarTable.add(pauseButton).pad(10).left();
        topBarTable.add(continueButton).pad(10).center();
        topBarTable.add(soundBtnStack).pad(10).right();
        stage.addActor(topBarTable);
        //endregion

        //region Drawboard
        TextureRegionDrawable cell_1 = new TextureRegionDrawable(new TextureRegion(new Texture("backgrounds\\gridtile1.png")));
        TextureRegionDrawable cell_2 = new TextureRegionDrawable(new TextureRegion(new Texture("backgrounds\\gridtile2.png")));

        boardTable = new Table();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col += 2) {
                if (row %2 == 0){
                    Image cell1 = new Image(cell_1);
                    Image cell2 = new Image(cell_2);
                    boardTable.add(cell1).size(cellSize, cellSize).pad(0);
                    boardTable.add(cell2).size(cellSize, cellSize).pad(0);
                }
                else
                {
                    Image cell1 = new Image(cell_1);
                    Image cell2 = new Image(cell_2);
                    boardTable.add(cell2).size(cellSize, cellSize).pad(0);
                    boardTable.add(cell1).size(cellSize, cellSize).pad(0);
                }
            }
            boardTable.row();
        }
        stage.addActor(boardTable);
        //endregion

        //region Snake
        Table theSnake = new Table();
        TextureRegionDrawable snakeHead = new TextureRegionDrawable(new TextureRegion(new Texture("randoms\\snakehead.png")));
        //TextureRegionDrawable snakeBody = new TextureRegionDrawable(new TextureRegion(new Texture("randoms\\snakebody.png")));
        snakeImage = new Image(snakeHead);
        snakeTable = new Table();
        snakeTable.add(snakeImage).size(cellSize, cellSize);
        snakeTable.row();
        stage.addActor(snakeTable);

        //ednregion

    }
    @Override
    public void show() {
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(v);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(i, i1, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
    }
}
