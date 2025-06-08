package com.snakegame.vertical;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class GameScreen implements Screen {
    private final SnakeGame game;
    private Texture gridTile, backgroundTexture;
    private Image backgroundImage;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private final int rows = 20;
    private final int cols = 20;
    private int cellSize = 32;
    private Table boardTable, topBarTable;
    private Texture foodTexture, cupTexture, pauseTexture;
    private Texture resetTexture, soundOnTexture, soundOffTexture;
    private Image foodImage, cupImage;
    private ImageButton pauseButton, resetButton, soundOnButton, soundOffButton;
    private int hasSound;
    private Array<FoodType> selectedFoods;
    private LayoutType selectedLayout;

    private Texture snakeHeadTexture, snakeBodyTexture;
    private Texture normalFoodTexture, specialFoodTexture, goldenFoodTexture;
    private Texture snakeTailTexture;
    private Texture snakeCornerTexture;
    private Sprite headSprite, bodySprite, tailSprite, cornerSprite;
    private GameStateDTO currentGameState;
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 0.15f; // Snake speed
    private float directionChangeCooldown = 0;
    private static final float DIRECTION_CHANGE_COOLDOWN = 0.05f; // Cooldown between direction changes
    private Label scoreLabel;
    private BitmapFont font;
    private boolean isPaused = false; // Add pause state flag

    // Add animation timers for food effects
    private float specialFoodTimer = 0;
    private float goldenFoodTimer = 0;
    private static final float SPECIAL_FOOD_ANIMATION_SPEED = 2f;
    private static final float GOLDEN_FOOD_ANIMATION_SPEED = 1.5f;

    public GameScreen(SnakeGame game, Array<FoodType> selectedFoods, LayoutType selectedLayout){
        this.game = game;
        this.selectedFoods = selectedFoods;
        OrthographicCamera camera = new OrthographicCamera();
        //gridTile = new Texture("backgrounds\\gridtile1.png");
        batch = new SpriteBatch();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, batch);

        // Initialize with empty game state
        currentGameState = new GameStateDTO();
        currentGameState.snakeBody = new ArrayList<>();
        currentGameState.foods = new ArrayList<>();
        currentGameState.score = 0;

        backgroundTexture = new Texture("backgrounds\\bgempty.png");
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        backgroundImage = new Image(background);
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);

        // Load snake textures
        snakeHeadTexture = new Texture("snake/head.png");
        snakeBodyTexture = new Texture("snake/body.png");
        snakeTailTexture = new Texture("snake/tail.png");
        snakeCornerTexture = new Texture("snake/connector.png");

        // Initialize sprites
        headSprite = new Sprite(snakeHeadTexture);
        bodySprite = new Sprite(snakeBodyTexture);
        tailSprite = new Sprite(snakeTailTexture);
        cornerSprite = new Sprite(snakeCornerTexture);

        // Set sprite sizes
        headSprite.setSize(cellSize, cellSize);
        bodySprite.setSize(cellSize, cellSize);
        tailSprite.setSize(cellSize, cellSize);
        cornerSprite.setSize(cellSize, cellSize);

        // Load food textures
        System.out.println("FOOD ONE: " + selectedFoods.get(0).texturePath);
        System.out.println("FOOD TWO: " + selectedFoods.get(1).texturePath);
        System.out.println("FOOD THREE: " + selectedFoods.get(2).texturePath);
        normalFoodTexture = new Texture(selectedFoods.get(0).texturePath);
        specialFoodTexture = new Texture(selectedFoods.get(1).texturePath);
        goldenFoodTexture = new Texture(selectedFoods.get(2).texturePath);

        //region Top bar
        topBarTable = new Table();
        topBarTable.setFillParent(true);

        pauseTexture = new Texture("buttons\\pause.png");
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTexture));
        pauseButton = new ImageButton(pauseDrawable);
        game.buttonAnimation(pauseButton);

        resetTexture = new Texture("buttons\\play.png");
        TextureRegionDrawable resetDrawable = new TextureRegionDrawable(new TextureRegion(resetTexture));
        resetButton = new ImageButton(resetDrawable);
        game.buttonAnimation(resetButton);

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
                game.backgroundMusic.setVolume(0f); // Mute music
                game.snakeHiss.setVolume(game.hissLoopId, 0f); // Mute hiss
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
                game.backgroundMusic.setVolume(0.2f); // Restore music volume
                game.snakeHiss.setVolume(game.hissLoopId, 2f); // Restore hiss volume
                soundOnButton.setVisible(true);
                soundOnButton.setTouchable(Touchable.enabled);
                soundOffButton.setVisible(false);
                soundOffButton.setTouchable(Touchable.disabled);
            }
        });

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(3f);
                // Reset game state
                GameApi.resetGame(new GameApi.GameStateCallback() {
                    @Override
                    public void onSuccess(GameStateDTO gameState) {
                        if (gameState == null) {
                            Gdx.app.error("GameScreen", "resetGame: Received null gameState from backend!");
                            return;
                        }
                        currentGameState = gameState;
                        updateScoreLabel();
                        isPaused = false;
                        Gdx.app.log("GameScreen", "Game reset successful. New snake size: " +
                            (gameState.snakeBody != null ? gameState.snakeBody.size() : 0));
                    }
                    @Override
                    public void onError(Throwable t) {
                        Gdx.app.error("GameScreen", "Error resetting game", t);
                    }
                });
            }
        });

        // Add pause button listener
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(3f);
                isPaused = !isPaused; // Toggle pause state
            }
        });

        Stack soundBtnStack = new Stack();
        soundBtnStack.add(soundOffButton);
        soundBtnStack.add(soundOnButton);

        topBarTable.top().padTop(100);
        topBarTable.add(pauseButton).pad(10).left();
        topBarTable.add(resetButton).pad(10).center();
        topBarTable.add(soundBtnStack).pad(10).right();
        stage.addActor(topBarTable);
        //endregion

        //region Drawboard
        this.selectedLayout = selectedLayout;
//        TextureRegionDrawable cell_1 = new TextureRegionDrawable(new TextureRegion(new Texture("backgrounds\\layouts\\gridtile1.png")));
//        TextureRegionDrawable cell_2 = new TextureRegionDrawable(new TextureRegion(new Texture("backgrounds\\layouts\\gridtile2.png")));
        TextureRegionDrawable cell_1 = new TextureRegionDrawable(new TextureRegion(new Texture(selectedLayout.texturePath1)));
        TextureRegionDrawable cell_2 = new TextureRegionDrawable(new TextureRegion(new Texture(selectedLayout.texturePath2)));

        boardTable = new Table();

        // Calculate the total board size
        float totalBoardWidth = cols * cellSize;
        float totalBoardHeight = rows * cellSize;

        // Center the board on screen
        float boardX = (game.V_WIDTH - totalBoardWidth) / 2f;
        float boardY = (game.V_HEIGHT - totalBoardHeight) / 2f;

        boardTable.setPosition(boardX, boardY);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Image cell = new Image((row + col) % 2 == 0 ? cell_1 : cell_2);
                boardTable.add(cell).size(cellSize, cellSize);
            }
            boardTable.row();
        }

        boardTable.pack();

        boardTable.setPosition(
        		boardX,
        		boardY
        	);

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
    }

    private void fetchGameState() {
        GameApi.fetchGameState(new GameApi.GameStateCallback() {
            @Override
            public void onSuccess(GameStateDTO gameState) {
                if (gameState == null) {
                    Gdx.app.error("GameScreen", "fetchGameState: Received null gameState from backend!");
                    return;
                }
                currentGameState = gameState;
                updateScoreLabel();
                Gdx.app.log("GameScreen", "Game state fetched successfully. Snake size: " +
                    (gameState.snakeBody != null ? gameState.snakeBody.size() : 0));
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
        if (!isPaused) { // Only update game if not paused
        updateTimer += v;
            directionChangeCooldown = Math.max(0, directionChangeCooldown - v);

            if (updateTimer >= game.getCurrentDifficulty().updateInterval) {
            updateGame();
            updateTimer = 0;
            }
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
                if (gameState == null) {
                    Gdx.app.error("GameScreen", "updateGame: Received null gameState from backend!");
                    return;
                }

                // Check if food was eaten by comparing scores
                if (currentGameState != null && gameState.score > currentGameState.score) {
                    // Find which food was eaten by comparing food lists
                    for (GameStateDTO.FoodDTO oldFood : currentGameState.foods) {
                        boolean foodStillExists = false;
                        for (GameStateDTO.FoodDTO newFood : gameState.foods) {
                            if (oldFood.position.x == newFood.position.x &&
                                oldFood.position.y == newFood.position.y) {
                                foodStillExists = true;
                                break;
                            }
                        }
                        if (!foodStillExists) {
                            // Play sound based on the eaten food's type
                            switch (oldFood.type) {
                                case NORMAL:
                                    game.normalFoodSound.play(game.getSfxVolume());
                                    break;
                                case SPECIAL:
                                    game.specialFoodSound.play(game.getSfxVolume());
                                    break;
                                case GOLDEN:
                                    game.goldenFoodSound.play(game.getSfxVolume());
                                    break;
                            }
                            break;
                        }
                    }
                }

                currentGameState = gameState;
                updateScoreLabel();
                Gdx.app.log("GameScreen", "Game updated successfully:");
                Gdx.app.log("GameScreen", "- Snake size: " +
                    (gameState.snakeBody != null ? gameState.snakeBody.size() : 0));
                if (gameState.snakeBody != null && !gameState.snakeBody.isEmpty()) {
                    GameStateDTO.PositionDTO head = gameState.snakeBody.get(0);
                    Gdx.app.log("GameScreen", "- Snake head position: (" + head.x + ", " + head.y + ")");
                }
                Gdx.app.log("GameScreen", "- Score: " + gameState.score);
                Gdx.app.log("GameScreen", "- Game Over: " + gameState.gameOver);
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error updating game", t);
                t.printStackTrace();
            }
        });
    }

    private void handleInput() {
        // Remove stage input processor temporarily to allow keyboard input
        Gdx.input.setInputProcessor(null);

        // Only process direction changes if cooldown is 0
        if (directionChangeCooldown <= 0) {
    	if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                Gdx.app.log("GameScreen", "UP key pressed - sending direction UP");
            GameApi.sendDirection(Direction.UP);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                Gdx.app.log("GameScreen", "DOWN key pressed - sending direction DOWN");
            GameApi.sendDirection(Direction.DOWN);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                Gdx.app.log("GameScreen", "LEFT key pressed - sending direction LEFT");
            GameApi.sendDirection(Direction.LEFT);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                Gdx.app.log("GameScreen", "RIGHT key pressed - sending direction RIGHT");
            GameApi.sendDirection(Direction.RIGHT);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Gdx.app.log("GameScreen", "R key pressed - resetting game");
            GameApi.resetGame(new GameApi.GameStateCallback() {
                @Override
                public void onSuccess(GameStateDTO gameState) {
                    if (gameState == null) {
                        Gdx.app.error("GameScreen", "resetGame: Received null gameState from backend!");
                        return;
                    }
                    currentGameState = gameState;
                    updateScoreLabel();
                    Gdx.app.log("GameScreen", "Game reset successful. New snake size: " +
                        (gameState.snakeBody != null ? gameState.snakeBody.size() : 0));
                }
                @Override
                public void onError(Throwable t) {
                    Gdx.app.error("GameScreen", "Error resetting game", t);
                }
            });
        }

        // Restore stage input processor for UI elements
        Gdx.input.setInputProcessor(stage);
    }


    private void drawGameObjects() {
        if (currentGameState == null) {
            Gdx.app.log("GameScreen", "Current game state is null");
            return;
        }

        // Update animation timers
        specialFoodTimer += Gdx.graphics.getDeltaTime();
        goldenFoodTimer += Gdx.graphics.getDeltaTime();

        // Get board's position
        float boardX = boardTable.getX();
        float boardY = boardTable.getY();

        // Set batch projection to match stage
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        // Draw snake
        List<GameStateDTO.PositionDTO> snakeBody = currentGameState.snakeBody;
        if (snakeBody != null) {
            Gdx.app.log("GameScreen", "Drawing snake with " + snakeBody.size() + " segments");
            for (int i = 0; i < snakeBody.size(); i++) {
                GameStateDTO.PositionDTO segment = snakeBody.get(i);

                // Calculate screen position (centered grid)
                float x = boardX + segment.x * cellSize;
                float y = boardY + segment.y * cellSize;

                Sprite currentSprite;
                float rotation = 0;

                if (i == 0) {
                    // Head rotation
                    currentSprite = headSprite;
                    if (i + 1 < snakeBody.size()) {
                        GameStateDTO.PositionDTO next = snakeBody.get(i + 1);
                        rotation = calculateRotation(next, segment);
                    }
                } else if (i == snakeBody.size() - 1) {
                    // Tail rotation
                    currentSprite = tailSprite;
                    GameStateDTO.PositionDTO prev = snakeBody.get(i - 1);
                    rotation = calculateRotation(segment, prev);
                } else {
                    // Body rotation - check if this is a corner
                    GameStateDTO.PositionDTO prev = snakeBody.get(i - 1);
                    GameStateDTO.PositionDTO next = snakeBody.get(i + 1);

                    if (isCorner(prev, segment, next)) {
                        currentSprite = cornerSprite;
                        rotation = calculateCornerRotation(prev, segment, next);
                    } else {
                        currentSprite = bodySprite;
                        rotation = calculateRotation(prev, next);
                    }
                }

                // Set position and rotation
                currentSprite.setPosition(x, y);
                currentSprite.setRotation(rotation);
                currentSprite.draw(batch);
            }
        }

        // Draw food
        if (currentGameState.foods != null) {
            Gdx.app.log("GameScreen", "Drawing " + currentGameState.foods.size() + " food items");
            for (GameStateDTO.FoodDTO food : currentGameState.foods) {
                Texture texture = getFoodTexture(food.type);

                // Calculate screen position (centered grid)
                float x = boardTable.getX() + food.position.x * cellSize;
                float y = boardTable.getY() + food.position.y * cellSize;

                // Apply different effects based on food type
                switch (food.type) {
                    case SPECIAL:
                        // Zoom in/out effect for special food
                        float specialScale = 1.0f + 0.2f * (float)Math.sin(specialFoodTimer * SPECIAL_FOOD_ANIMATION_SPEED);
                        float specialSize = cellSize * specialScale;
                        float specialOffset = (specialSize - cellSize) / 2;
                        batch.draw(texture,
                            x - specialOffset,
                            y - specialOffset,
                            specialSize,
                            specialSize);
                        break;

                    case GOLDEN:
                        // Enhanced golden glow effect with pulsing glow
                        float glowPulse = 0.3f + 0.2f * (float)Math.sin(goldenFoodTimer * GOLDEN_FOOD_ANIMATION_SPEED);
                        float glowSize = 10f + 15f * (float)Math.sin(goldenFoodTimer * GOLDEN_FOOD_ANIMATION_SPEED);

                        // Outer glow (larger, more transparent)
                        batch.setColor(1f, 0.8f, 0.2f, glowPulse * 0.5f);
                        batch.draw(texture,
                            x - glowSize,
                            y - glowSize,
                            cellSize + (glowSize * 2),
                            cellSize + (glowSize * 2));

                        // Middle glow
                        batch.setColor(1f, 0.9f, 0.3f, glowPulse * 0.7f);
                        batch.draw(texture,
                            x - (glowSize * 0.7f),
                            y - (glowSize * 0.7f),
                            cellSize + (glowSize * 1.4f),
                            cellSize + (glowSize * 1.4f));

                        // Inner glow
                        batch.setColor(1f, 1f, 0.4f, glowPulse);
                        batch.draw(texture,
                            x - (glowSize * 0.4f),
                            y - (glowSize * 0.4f),
                            cellSize + (glowSize * 0.8f),
                            cellSize + (glowSize * 0.8f));

                        // Draw main sprite (constant size)
                        batch.setColor(1f, 1f, 1f, 1f);
                        batch.draw(texture, x, y, cellSize, cellSize);
                        break;

                    default:
                        // Normal food - no special effects
                        batch.draw(texture, x, y, cellSize, cellSize);
                        break;
                }
            }
        }

        batch.end();
    }

    private float calculateRotation(GameStateDTO.PositionDTO from, GameStateDTO.PositionDTO to) {
        float dx = to.x - from.x;
        float dy = to.y - from.y;

        if (dx > 0) return 0;           // Right
        if (dx < 0) return 180;         // Left
        if (dy > 0) return 90;          // Up
        if (dy < 0) return 270;         // Down
        return 0;                       // Default to right
    }

    private boolean isCorner(GameStateDTO.PositionDTO prev, GameStateDTO.PositionDTO current, GameStateDTO.PositionDTO next) {
        // Check if the snake is making a turn
        int dx1 = current.x - prev.x;
        int dy1 = current.y - prev.y;
        int dx2 = next.x - current.x;
        int dy2 = next.y - current.y;

        // If both movements are not in the same direction, it's a corner
        return (dx1 != dx2) || (dy1 != dy2);
    }

    private float calculateCornerRotation(GameStateDTO.PositionDTO prev, GameStateDTO.PositionDTO current, GameStateDTO.PositionDTO next) {
        int dx1 = current.x - prev.x;
        int dy1 = current.y - prev.y;
        int dx2 = next.x - current.x;
        int dy2 = next.y - current.y;

        String from = direction(dx1, dy1);
        String to = direction(dx2, dy2);

        if ((from.equals("LEFT") && to.equals("DOWN")) || (from.equals("UP") && to.equals("RIGHT")))
            return 0f;   // ┌ top-left

        if ((from.equals("DOWN") && to.equals("RIGHT")) || (from.equals("LEFT") && to.equals("UP")))
            return 90f;  // ┐ top-right

        if ((from.equals("RIGHT") && to.equals("UP")) || (from.equals("DOWN") && to.equals("LEFT")))
            return 180f; // ┘ bottom-right

        if ((from.equals("UP") && to.equals("LEFT")) || (from.equals("RIGHT") && to.equals("DOWN")))
            return 270f; // └ bottom-left

        return 0f;
    }

    private String direction(int dx, int dy) {
        if (dx == 1) return "RIGHT";
        if (dx == -1) return "LEFT";
        if (dy == 1) return "UP";
        if (dy == -1) return "DOWN";
        return "NONE";
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
        snakeTailTexture.dispose();
        snakeCornerTexture.dispose();
        normalFoodTexture.dispose();
        specialFoodTexture.dispose();
        goldenFoodTexture.dispose();
        font.dispose();
        stage.dispose();
        batch.dispose();

    }
}
