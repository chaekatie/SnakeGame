package com.snakegame.vertical;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.Integer.parseInt;

public class GameScreen implements Screen {
    private final SnakeGame game;
    private Image backgroundImage;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;

    private final int rows = 20;
    private final int cols = 20;
    private int cellSize = 32;
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 0.15f; // Snake speed
    private float directionChangeCooldown = 0;
    private static final float DIRECTION_CHANGE_COOLDOWN = 0.02f; //  Faster direction changes

    private Table boardTable, topBarTable;
    private Texture resetTexture, soundOnTexture, soundOffTexture, pauseTexture, playTexture, backgroundTexture;
    private ImageButton pauseButton, resetButton, soundOnButton, soundOffButton;
    private TextureRegionDrawable pauseDrawable, playDrawable;
    private Array<FoodType> selectedFoods;
    private LayoutType selectedLayout;
    private Skin skin;
    private Label normalLabel, specialLabel, goldenLabel;
    private int eatenNormal, eatenSpecial, eatenGolden, totalScores;
    private LocalDateTime startTime, endTime;
    private long playTime;

    private Texture snakeHeadTexture, snakeBodyTexture, snakeTailTexture, snakeCornerTexture;
    private Texture normalFoodTexture, specialFoodTexture, goldenFoodTexture;
    private Sprite headSprite, bodySprite, tailSprite, cornerSprite;
    private GameStateDTO currentGameState;

    private Label scoreLabel, startTimeLabel, scoreMessage, playtimeMessage, detailsScoreMessage;
    private BitmapFont font;
    private boolean isPaused = false, isLoggedIn, hasSavedScore = false, hasSavedMatch = false;
    private Dialog gameOverDialog, announcingDialog;
    private DateTimeFormatter formatter;

    // Add animation timers for food effects
    private float specialFoodTimer = 0;
    private float goldenFoodTimer = 0;
    private static final float SPECIAL_FOOD_ANIMATION_SPEED = 2f;
    private static final float GOLDEN_FOOD_ANIMATION_SPEED = 1.5f;

    public GameScreen(SnakeGame game, Array<FoodType> selectedFoods, LayoutType selectedLayout){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        batch = new SpriteBatch();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        this.isLoggedIn = game.getLoggedIn();
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.selectedFoods = selectedFoods;
        this.selectedLayout = selectedLayout;
        eatenNormal = eatenSpecial = eatenGolden = 0;

        Texture dialogTex = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialogDrawble = new TextureRegionDrawable(new TextureRegion(dialogTex));

        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;

        Label.LabelStyle customLabel1 = new Label.LabelStyle();
        customLabel1.font = game.theBigFont;

        // Initialize with empty game state
        currentGameState = new GameStateDTO();
        currentGameState.snakeBody = new ArrayList<>();
        currentGameState.foods = new ArrayList<>();
        currentGameState.score = 0;
        currentGameState.gameOver = false;

        //region Background
        backgroundTexture = new Texture("backgrounds\\bgempty.png");
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        backgroundImage = new Image(background);
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);
        backgroundImage.toBack();
        //endregion

        //region Textures and Sprites
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
        //endregion

        //region Draw Board
        TextureRegionDrawable cell_1 = new TextureRegionDrawable(new TextureRegion(new Texture(selectedLayout.texturePath1)));
        TextureRegionDrawable cell_2 = new TextureRegionDrawable(new TextureRegion(new Texture(selectedLayout.texturePath2)));
        boardTable = new Table();

        // Calculate the total board size
        float totalBoardWidth = cols * cellSize;
        float totalBoardHeight = rows * cellSize;
        // Center the board on screen
        float boardX = (game.V_WIDTH - totalBoardWidth) / 2f;
        float boardY = (game.V_HEIGHT - totalBoardHeight) / 2f;

        boardX += -15f;
        boardTable.setPosition(boardX, boardY);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Image cell = new Image((row + col) % 2 == 0 ? cell_1 : cell_2);
                boardTable.add(cell).size(cellSize, cellSize);
            }
            boardTable.row();
        }

        boardTable.pack();
        boardTable.setPosition(boardX, boardY);
        stage.addActor(boardTable);
        //endregion

        //region GameOver Dialog
        gameOverDialog = new Dialog("GAME OVER", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals("restart")) {
                    resetGame();
                } else {
                    // Reset game before going back to menu
                    GameApi.resetGame(new GameApi.GameStateCallback() {
                        @Override
                        public void onSuccess(GameStateDTO gameState) {
                            Gdx.app.postRunnable(() -> {
                                game.setScreen(new MenuScreen(game));
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Gdx.app.error("GameScreen", "Error resetting game before menu", t);
                            Gdx.app.postRunnable(() -> {
                                game.setScreen(new MenuScreen(game));
                            });
                        }
                    });
                }
            }
        };

        gameOverDialog.getContentTable().setBackground(dialogDrawble);
        Label message1 = new Label("YOU LOST!",customLabel);
        Label message2 = new Label("What would you like to do now?", customLabel);
        scoreMessage = new Label("Total Score: 0", customLabel);
        //detailsScoreMessage = new Label(selectedFoods.get(0) + "", customLabel);
        playtimeMessage = new Label("Playing time: ", customLabel);
        gameOverDialog.text(message1).center();
        gameOverDialog.getContentTable().row();
        gameOverDialog.text(scoreMessage).center();
        gameOverDialog.getContentTable().row();
//        gameOverDialog.text(detailsScoreMessage);
//        gameOverDialog.getContentTable().row();
        gameOverDialog.text(playtimeMessage).center();
        gameOverDialog.getContentTable().row();
        gameOverDialog.text(message2).center();
        gameOverDialog.getContentTable().row();

        gameOverDialog.button("Play Again", "restart");
        gameOverDialog.button("Back to Menu", "menu");
        //endregion

        //region Announcing Dialog
        announcingDialog = new Dialog("ATTENTION", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals("restart")) {
                    resetGame();
                } else if(object.equals("continue")) {
                    isPaused = !isPaused; // Toggle pause state
                    // Switch back to pause texture when continuing
                    pauseButton.getStyle().imageUp = pauseDrawable;
                } else {
                    // Reset game before going back to menu
                    GameApi.resetGame(new GameApi.GameStateCallback() {
                        @Override
                        public void onSuccess(GameStateDTO gameState) {
                            Gdx.app.postRunnable(() -> {
                                game.setScreen(new MenuScreen(game));
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Gdx.app.error("GameScreen", "Error resetting game before menu", t);
                            Gdx.app.postRunnable(() -> {
                                game.setScreen(new MenuScreen(game));
                            });
                        }
                    });
                }
            }
        };

        announcingDialog.getContentTable().setBackground(dialogDrawble);
        Label message_1 = new Label("YOU HAS PAUSED THE GAME!",customLabel);
        Label message_2 = new Label("What would you like to do now?", customLabel);
        Label message_3 = new Label("If you restart, this match won't be save!", customLabel);
        announcingDialog.text(message_1);
        announcingDialog.getContentTable().row();
        announcingDialog.text(message_2);
        announcingDialog.getContentTable().row();
        announcingDialog.text(message_3);

        announcingDialog.button("Continue Playing", "continue");
        announcingDialog.button("Restart the match", "restart");
        announcingDialog.button("Back to menu", "menu");
        //endregion

        //region Top bar
        topBarTable = new Table();
        topBarTable.setFillParent(true);

        pauseTexture = new Texture("buttons\\pause.png");
        playTexture = new Texture("buttons\\play.png");
        pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTexture));
        playDrawable = new TextureRegionDrawable(new TextureRegion(playTexture));
        pauseButton = new ImageButton(pauseDrawable);
        game.buttonAnimation(pauseButton);

        resetTexture = new Texture("buttons\\reset.png");
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
                game.clicking.play(game.getSfxVolume());
                // Mute all sounds
                game.backgroundMusic.setVolume(0f);
                game.snakeHiss.setVolume(game.hissLoopId, 0f);
                // Store current sound IDs before muting
                long normalFoodId = game.normalFoodSound.play(0f);
                long specialFoodId = game.specialFoodSound.play(0f);
                long goldenFoodId = game.goldenFoodSound.play(0f);
                game.normalFoodSound.setVolume(normalFoodId, 0f);
                game.specialFoodSound.setVolume(specialFoodId, 0f);
                game.goldenFoodSound.setVolume(goldenFoodId, 0f);
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
                game.clicking.play(game.getSfxVolume());
                // Restore all sounds to their proper volumes
                game.backgroundMusic.setVolume(game.getSfxVolume());
                game.snakeHiss.setVolume(game.hissLoopId, game.getSfxVolume());
                // Store current sound IDs before restoring volume
                long normalFoodId = game.normalFoodSound.play(game.getSfxVolume());
                long specialFoodId = game.specialFoodSound.play(game.getSfxVolume());
                long goldenFoodId = game.goldenFoodSound.play(game.getSfxVolume());
                game.normalFoodSound.setVolume(normalFoodId, game.getSfxVolume());
                game.specialFoodSound.setVolume(specialFoodId, game.getSfxVolume());
                game.goldenFoodSound.setVolume(goldenFoodId, game.getSfxVolume());
                soundOnButton.setVisible(true);
                soundOnButton.setTouchable(Touchable.enabled);
                soundOffButton.setVisible(false);
                soundOffButton.setTouchable(Touchable.disabled);
            }
        });

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(game.getSfxVolume());
                // Reset game state
                resetGame();
            }
        });

        // Add pause button listener
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(game.getSfxVolume());
                isPaused = !isPaused; // Toggle pause state

                // Update button texture based on pause state
                if (isPaused) {
                    pauseButton.getStyle().imageUp = playDrawable;
                } else {
                    pauseButton.getStyle().imageUp = pauseDrawable;
                }

                announcingDialog.show(stage);
            }
        });

        Stack soundBtnStack = new Stack();
        soundBtnStack.add(soundOffButton);
        soundBtnStack.add(soundOnButton);

        topBarTable.top().padTop(120);
        topBarTable.add(pauseButton).pad(60, 10, 10, 10).left();
        topBarTable.add(resetButton).pad(60, 10, 10, 10).center();
        topBarTable.add(soundBtnStack).pad(60,10,10,10).right();
        stage.addActor(topBarTable);

        // Create score label
        font = new BitmapFont();
        font.getData().setScale(2);
        scoreLabel = new Label("SCORE: 0", customLabel1);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setPosition(game.V_WIDTH / 2 - scoreLabel.getPrefWidth()/2, game.V_HEIGHT -  150);
        stage.addActor(scoreLabel);
        //endregion

        // region Down Bar
        Image normalFood = new Image(new TextureRegionDrawable(new TextureRegion(normalFoodTexture)));
        normalFood.setScale(1f);
        Image specialFood = new Image(new TextureRegionDrawable(new TextureRegion(specialFoodTexture)));
        specialFood.setScale(1f);
        Image goldenFood = new Image(new TextureRegionDrawable(new TextureRegion(goldenFoodTexture)));
        goldenFood.setScale(1f);

        normalLabel = new Label("(10): 0", customLabel1);
        specialLabel = new Label("(20): 0", customLabel1);
        goldenLabel = new Label("(30): 0", customLabel1);

        Table eatenFoodsTable = new Table();
        eatenFoodsTable.add(normalFood).padRight(10);
        eatenFoodsTable.add(normalLabel).padRight(40);
        eatenFoodsTable.add(specialFood).padRight(10);
        eatenFoodsTable.add(specialLabel).padRight(40);
        eatenFoodsTable.add(goldenFood).padRight(10);
        eatenFoodsTable.add(goldenLabel).row();
        eatenFoodsTable.pack();
        eatenFoodsTable.setPosition(backgroundImage.getX() + 20, backgroundImage.getY() + 200);
        stage.addActor(eatenFoodsTable);

        // Time tracking label
        startTimeLabel = new Label("Start time: hh:mm:ss dd-mm-yy", customLabel1);
        startTimeLabel.setPosition(backgroundImage.getX(), backgroundImage.getY() + 100);
        stage.addActor(startTimeLabel);
        //endregion

        // Initial game state fetch
        //fetchGameState();
        resetGame();
    }

    private void resetGame() {
        GameApi.resetGame(new GameApi.GameStateCallback() {
            @Override
            public void onSuccess(GameStateDTO gameState) {
                if (gameState == null) {
                    Gdx.app.error("GameScreen", "resetGame: Received null gameState from backend!");
                    return;
                }
                currentGameState = gameState;
                updateScoreLabel();

                eatenNormal = eatenSpecial = eatenGolden = totalScores = 0;
                hasSavedScore = isPaused = false;

                // Update UI
                normalLabel.setText("(10): 0");
                specialLabel.setText("(20): 0");
                goldenLabel.setText("(30): 0");

                startTime = LocalDateTime.now();
                startTimeLabel.setText("Start time: " + startTime.format(formatter));
                System.out.println("START TIME: "+ startTime);

                Gdx.app.log("GameScreen", "Game reset successful. New snake size: " +
                    (gameState.snakeBody != null ? gameState.snakeBody.size() : 0));
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error resetting game", t);
            }
        });
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

                if (startTime == null && !gameState.gameOver) {
                    startTime = LocalDateTime.now();
                    startTimeLabel.setText("Start time: " + startTime.format(formatter));
                    System.out.println("START TIME: "+ startTime);
                }

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
            scoreLabel.setText("SCORE: " + currentGameState.score);
        }
    }

    @Override
    public void show() {
        GestureDetector gestureDetector = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if (directionChangeCooldown > 0 || isPaused || currentGameState == null || currentGameState.gameOver) return false;

                Direction currentDirection = null;
                if (currentGameState.snakeBody != null && currentGameState.snakeBody.size() >= 2) {
                    GameStateDTO.PositionDTO head = currentGameState.snakeBody.get(0);
                    GameStateDTO.PositionDTO neck = currentGameState.snakeBody.get(1);
                    currentDirection = getDirectionFromPositions(head, neck);
                }

                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0 && currentDirection != Direction.LEFT) {
                        Gdx.app.log("GameScreen", "Swipe RIGHT - sending direction RIGHT");
                        GameApi.sendDirection(Direction.RIGHT);
                    } else if (velocityX < 0 && currentDirection != Direction.RIGHT) {
                        Gdx.app.log("GameScreen", "Swipe LEFT - sending direction LEFT");
                        GameApi.sendDirection(Direction.LEFT);
                    }
                } else {
                    if (velocityY < 0 && currentDirection != Direction.DOWN) {
                        Gdx.app.log("GameScreen", "Swipe UP - sending direction UP");
                        GameApi.sendDirection(Direction.UP);
                    } else if (velocityY > 0 && currentDirection != Direction.UP) {
                        Gdx.app.log("GameScreen", "Swipe DOWN - sending direction DOWN");
                        GameApi.sendDirection(Direction.DOWN);
                    }
                }
                return true;
            }
        });

        // Combine gesture + stage input
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gestureDetector);
        multiplexer.addProcessor(stage); // This allows your buttons/UI to still work
        Gdx.input.setInputProcessor(multiplexer);
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
                                    eatenNormal++;
                                    normalLabel.setText("(10): " + eatenNormal);
                                    break;
                                case SPECIAL:
                                    game.specialFoodSound.play(game.getSfxVolume());
                                    eatenSpecial++;
                                    specialLabel.setText("(20): " + eatenSpecial);
                                    break;
                                case GOLDEN:
                                    game.goldenFoodSound.play(game.getSfxVolume());
                                    eatenGolden++;
                                    goldenLabel.setText("(30): " + eatenGolden);
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

                if (gameState.gameOver) {
                    isPaused = true;
                    endTime = LocalDateTime.now();
                    System.out.println("START PLAY TIME: " + startTime);
                    System.out.println("END PLAY TIME: " + endTime);
                    totalScores = gameState.score;

                    if (!hasSavedScore) {
                        saveScores();
                    }

                    long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
                    long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
                    long hours = ChronoUnit.HOURS.between(startTime, endTime);
                    playTime = hours * 3600 + minutes * 60 + seconds;

                    if (!hasSavedMatch) {
                        saveMatchDetails();
                    }

                    gameOverDialog.show(stage);
                    scoreMessage.setText("Total scores: " + gameState.score);
                    playtimeMessage.setText("Playing time: " + hours + "h: " + minutes + "m: " + seconds + "s");
                }
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error updating game", t);
                t.printStackTrace();
            }
        });
    }

    private void handleInput() {

        // Only process direction changes if cooldown is 0 and game is not paused
        if (directionChangeCooldown <= 0 && !isPaused && currentGameState != null && !currentGameState.gameOver) {
            Direction currentDirection = null;
            if (currentGameState.snakeBody != null && currentGameState.snakeBody.size() >= 2) {
                GameStateDTO.PositionDTO head = currentGameState.snakeBody.get(0);
                GameStateDTO.PositionDTO neck = currentGameState.snakeBody.get(1);
                currentDirection = getDirectionFromPositions(head, neck);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && currentDirection != Direction.DOWN) {
                Gdx.app.log("GameScreen", "UP key pressed - sending direction UP");
            GameApi.sendDirection(Direction.UP);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && currentDirection != Direction.UP) {
                Gdx.app.log("GameScreen", "DOWN key pressed - sending direction DOWN");
            GameApi.sendDirection(Direction.DOWN);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && currentDirection != Direction.RIGHT) {
                Gdx.app.log("GameScreen", "LEFT key pressed - sending direction LEFT");
            GameApi.sendDirection(Direction.LEFT);
                directionChangeCooldown = DIRECTION_CHANGE_COOLDOWN;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && currentDirection != Direction.LEFT) {
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
    }

    private Direction getDirectionFromPositions(GameStateDTO.PositionDTO from, GameStateDTO.PositionDTO to) {
        int dx = from.x - to.x;
        int dy = from.y - to.y;

        // Handle wrapping cases
        dx = normalizeDelta(dx, cols);
        dy = normalizeDelta(dy, rows);

        if (dx > 0) return Direction.RIGHT;
        if (dx < 0) return Direction.LEFT;
        if (dy > 0) return Direction.UP;
        if (dy < 0) return Direction.DOWN;
        return null;
    }

    private void drawGameObjects() {
        if (currentGameState == null) {
            Gdx.app.log("GameScreen", "Current game state is null");
            return;
        }

        // Don't render if game is paused or over
        if (isPaused || currentGameState.gameOver) {
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

    private int normalizeDelta(int d, int size) {
        if (d > 1) return d - size;
        if (d < -1) return d + size;
        return d;
    }

    private boolean isCorner(GameStateDTO.PositionDTO prev, GameStateDTO.PositionDTO current, GameStateDTO.PositionDTO next) {
        // Check if the snake is making a turn
        int dx1 = current.x - prev.x;
        int dy1 = current.y - prev.y;
        int dx2 = next.x - current.x;
        int dy2 = next.y - current.y;

        // Handle wrapping cases
        dx1 = normalizeDelta(current.x - prev.x, cols);
        dy1 = normalizeDelta(current.y - prev.y, rows);
        dx2 = normalizeDelta(next.x - current.x, cols);
        dy2 = normalizeDelta(next.y - current.y, rows);


        // If both movements are not in the same direction, it's a corner
        return (dx1 != dx2) || (dy1 != dy2);
    }

    private float calculateCornerRotation(GameStateDTO.PositionDTO prev, GameStateDTO.PositionDTO current, GameStateDTO.PositionDTO next) {
        int dx1 = current.x - prev.x;
        int dy1 = current.y - prev.y;
        int dx2 = next.x - current.x;
        int dy2 = next.y - current.y;

        // Handle wrapping cases
        dx1 = normalizeDelta(current.x - prev.x, cols);
        dy1 = normalizeDelta(current.y - prev.y, rows);
        dx2 = normalizeDelta(next.x - current.x, cols);
        dy2 = normalizeDelta(next.y - current.y, rows);

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

    private void saveScores(){
        // Only save scores if user is logged in
        if (!game.getLoggedIn()) {
            System.out.println("User not logged in, skipping score save");
            return;
        }

        GameApi.saveUserScore(game.getUsername(), totalScores, new GameApi.GameScoreCallback() {
            @Override
            public void onSuccess() {
                hasSavedScore = true;
                System.out.println("SAVED DATA (saveScores method): username - " + game.getUsername() + ", score - " + totalScores);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR: " + t);
                hasSavedScore = false;
            }
        });
    }

    private void saveMatchDetails(){
        MatchDTO matchDetails = new MatchDTO();
        matchDetails.setTotalScore(totalScores);
        int playingTime = (int) playTime;
        System.out.println("PLAY TIME (save match method): " + playingTime);
        matchDetails.setPlayTime(playingTime);
        String normalFood = selectedFoods.get(0).name() + " - " + eatenNormal;
        String specialFood = selectedFoods.get(1).name() + " - " + eatenSpecial;
        String goldenFood = selectedFoods.get(2).name() + " - " + eatenGolden;
        matchDetails.setNormalFoodCount(normalFood);
        matchDetails.setSpecialFoodCount(specialFood);
        matchDetails.setGoldenFoodCount(goldenFood);

        GameApi.saveGameMatch(matchDetails, new GameApi.MatchDetailCallback() {
            @Override
            public void onSuccess(String message) {
                hasSavedMatch = true;
                System.out.println("SAVED DATA (saveMatch method): " + matchDetails.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR: " + t);
                hasSavedMatch = false;
            }
        });
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
        // Reset game before disposing
        GameApi.resetGame(new GameApi.GameStateCallback() {
            @Override
            public void onSuccess(GameStateDTO gameState) {
                Gdx.app.log("GameScreen", "Game reset on dispose successful");
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.error("GameScreen", "Error resetting game on dispose", t);
            }
        });

        resetTexture.dispose();
        soundOffTexture.dispose();
        soundOnTexture.dispose();
        pauseTexture.dispose();
        playTexture.dispose();
        backgroundTexture.dispose();
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
