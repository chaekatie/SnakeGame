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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import java.util.List;

public class GameScreen implements Screen {
    private final SnakeGame game;
    private Texture gridTile, backgroundTexture;
    private Image backgroundImage;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private final int rows = 20;
    private final int cols = 20;
    private int cellSize = 48;
    private Table boardTable, topBarTable;
    private Texture foodTexture, cupTexture, pauseTexture;
    private Texture continueTexture, soundOnTexture, soundOffTexture;
    private Image foodImage, cupImage;
    private ImageButton pauseButton, continueButton, soundOnButton, soundOffButton;
    private int hasSound;
    
    private Texture snakeHeadTexture, snakeBodyTexture;
    private Texture normalFoodTexture, specialFoodTexture, goldenFoodTexture;
    private GameStateDTO currentGameState;
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 0.15f; // Snake speed
    private Label scoreLabel;
    private BitmapFont font;

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
        
        // Load snake textures
        snakeHeadTexture = new Texture("snake/head.png");
        snakeBodyTexture = new Texture("snake/body.png");
        
        // Load food textures
        normalFoodTexture = new Texture("food/normal.png");
        specialFoodTexture = new Texture("food/special.png");
        goldenFoodTexture = new Texture("food/golden.png");

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
        
        // Create score label
        font = new BitmapFont();
        font.getData().setScale(2);
        scoreLabel = new Label("Score: 0", new Label.LabelStyle(font, Color.WHITE));
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setPosition(game.V_WIDTH / 2 - scoreLabel.getPrefWidth()/2, game.V_HEIGHT - 80);
        stage.addActor(scoreLabel);

        // Initial game state fetch
        fetchGameState();
        
        // Add keyboard listener
        Gdx.input.setInputProcessor(stage);

    }
    
    private void fetchGameState() {
        GameApi.fetchGameState(new GameApi.GameStateCallback() {
            @Override
            public void onSuccess(GameStateDTO gameState) {
                currentGameState = gameState;
                updateScoreLabel();
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error fetching game state", t);
            }
        });
    }
    
    private void updateScoreLabel() {
        if (currentGameState != null) {
            scoreLabel.setText("Score: " + currentGameState.score);
        }
    }
    
    @Override
    public void show() {
    }

    @Override
    public void render(float v) {
    	// Game logic update
        updateTimer += v;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateGame();
            updateTimer = 0;
        }
        
        // Handle input
        handleInput();
        
        // Rendering
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(v);
        stage.draw();
        
        // Draw game objects
        drawGameObjects();
    }
    
    private void updateGame() {
        GameApi.updateGame(new GameApi.GameStateCallback() {
            @Override
            public void onSuccess(GameStateDTO gameState) {
                currentGameState = gameState;
                updateScoreLabel();
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error updating game", t);
            }
        });
    }
    
    private void handleInput() {
    	if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            GameApi.sendDirection(Direction.UP);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            GameApi.sendDirection(Direction.DOWN);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            GameApi.sendDirection(Direction.LEFT);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            GameApi.sendDirection(Direction.RIGHT);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            GameApi.resetGame(new GameApi.GameStateCallback() {
                @Override
                public void onSuccess(GameStateDTO gameState) {
                    currentGameState = gameState;
                    updateScoreLabel();
                }
                @Override
                public void onError(Throwable t) {
                    Gdx.app.error("GameScreen", "Error resetting game", t);
                }
            });
        }
    }

    
    private void drawGameObjects() {
        if (currentGameState == null) return;

        // Set batch projection to match stage
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        
        // Draw snake
        List<GameStateDTO.PositionDTO> snakeBody = currentGameState.snakeBody;
        for (int i = 0; i < snakeBody.size(); i++) {
            GameStateDTO.PositionDTO segment = snakeBody.get(i);
            Texture texture = (i == 0) ? snakeHeadTexture : snakeBodyTexture;
            
            // Calculate screen position (centered grid)
            float x = boardTable.getX() + segment.x * cellSize;
            float y = boardTable.getY() + segment.y * cellSize;
            
            batch.draw(texture, x, y, cellSize, cellSize);
        }
        
        // Draw food
        for (GameStateDTO.FoodDTO food : currentGameState.foods) {
            Texture texture = getFoodTexture(food.type);
            
            // Calculate screen position (centered grid)
            float x = boardTable.getX() + food.position.x * cellSize;
            float y = boardTable.getY() + food.position.y * cellSize;
            
            batch.draw(texture, x, y, cellSize, cellSize);
        }
        
        batch.end();
    }

    private Texture getFoodTexture(GameStateDTO.FoodType type) {
        switch (type) {
            case NORMAL: return normalFoodTexture;
            case SPECIAL: return specialFoodTexture;
            case GOLDEN: return goldenFoodTexture;
            default: return normalFoodTexture;
        }
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
        snakeHeadTexture.dispose();
        snakeBodyTexture.dispose();
        normalFoodTexture.dispose();
        specialFoodTexture.dispose();
        goldenFoodTexture.dispose();
        font.dispose();
        stage.dispose();
        batch.dispose();

    }
}