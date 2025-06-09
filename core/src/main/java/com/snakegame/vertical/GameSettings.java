package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.*;
import java.util.Arrays;

public class GameSettings implements Screen {
    private SnakeGame game;
    private Texture background, playBtn, selectSpeed, selectFood, selectLayout, selectMap;
    private Texture[] foodsTexture;
    private Image theBackground;
    private ImageButton chooseFood, chooseLayout, backButton, playButton, chooseSpeed, chooseMap;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Label.LabelStyle customLabel;
    private boolean isLoggedIn, hasChoseDifficulty, hasChoseMap;
    private Dialog warningDialog, difficultyDialog;
    private final Array<FoodType> selectedFoods = new Array<>();
    private LayoutType selectedLayout;

    public GameSettings(SnakeGame game){
        this.game = game;
        this.isLoggedIn = game.getLoggedIn();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        hasChoseDifficulty = false;
        hasChoseMap = false;  // Initialize map choice flag

        // Set default mode (with border)
        GameApi.setBorderlessMode(false);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        param.color = Color.GOLDENROD;
        BitmapFont myFont = generator.generateFont(param);
        generator.dispose();

        customLabel = new Label.LabelStyle();
        customLabel.font = myFont;

        Texture dialogTex = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialog = new TextureRegionDrawable(new TextureRegion(dialogTex));

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = myFont;

        //region Background
        background = new Texture("backgrounds\\background.jpg");
        theBackground = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(theBackground);
        stage.addActor(theBackground);
        //endregion

        //region Inform Label
        Label greeting = new Label("Feel free to choose your favorite food,\nsnake type or the layout you want here!", customLabel);
        Label greeting2 = new Label("Or continue with the default set up by \nclicking the Play button down there!", customLabel);
        greeting.setPosition(theBackground.getX() + 50, theBackground.getY() + 1130);
        greeting2.setPosition(theBackground.getX() + 50, theBackground.getY() + 1030);
        greeting2.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        greeting.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(greeting);
        stage.addActor(greeting2);
        //endregion

        //region Back button
        backButton = game.activateBackButton(theBackground);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(backButton);
        //endregion

        //region Warning Dialog
        warningDialog = new Dialog("", skin);
        warningDialog.getContentTable().setBackground(dialog);
        Table content = warningDialog.getContentTable();
        Label message = new Label("",customLabel);
        game.dialogTextAnimation(message, true);
        content.add(message).center().row();
        //endregion

        //region Food Logo
        selectFood = new Texture("logos\\food.png");
        chooseFood = new ImageButton(new TextureRegionDrawable(new TextureRegion(selectFood)));
        chooseFood.setPosition(theBackground.getX() + 50, theBackground.getY() + 820);
        stage.addActor(chooseFood);
        //endregion

        //region Food Selection
        FoodType[] foodTypes = FoodType.values();
        foodsTexture = Arrays.stream(FoodType.values())
            .map(food -> new Texture(food.texturePath))
            .toArray(Texture[]::new);

        Table foodsTable = new Table();
        Array<Image> foodsImage = new Array<>(); // keep references
        Array<Integer> clickCounts = new Array<>();

        for (int i = 0; i < foodsTexture.length; i++) {
            final int index = i;
            final FoodType food = foodTypes[i];
            clickCounts.add(0);

            final Image foodImage = new Image(new TextureRegionDrawable(new TextureRegion(foodsTexture[i])));
            foodImage.setSize(300, 300);
            foodImage.setScale(1.3f);
            foodsImage.add(foodImage);
            foodImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int currentClicks = clickCounts.get(index) + 1;
                    clickCounts.set(index, currentClicks);

                    System.out.println("FOOD CHOSEN: " + food);
                    if(currentClicks == 1){
                        if (selectedFoods.size < 3) {
                            selectedFoods.add(food);
                            System.out.println("SELECTED FOODS: " + selectedFoods);
                            foodImage.setScale(2f);
                        } else {
                            System.out.println("You can only choose up to 3 foods.");
                            message.setText("You need to choose only\n 3 types of foods!");
                            game.dialogTextAnimation(message, false);
                            warningDialog.show(stage);
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    warningDialog.hide();
                                }
                            }, 1f);
                            return;
                        }
                    } else if(currentClicks == 2){
                        selectedFoods.removeValue(food, false);
                        foodImage.setScale(1.3f);
                    } else { System.out.println("Multiple clicks (" + currentClicks + ") on " + food); }
                }
            });
            foodsTable.add(foodImage).pad(20);
        }
        foodsTable.pack();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(); // optional
        ScrollPane scrollPane = new ScrollPane(foodsTable, scrollStyle);
        scrollPane.setScrollingDisabled(false, true); // enable X scroll only
        scrollPane.setFlingTime(0.5f);
        scrollPane.setOverscroll(false, false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setSize(200 , 200);
        scrollPane.setPosition(300, 750);
        scrollPane.setDebug(true);

        Table borderWrapper = new Table();
        borderWrapper.setSize(300, 200); // slightly bigger than ScrollPane
        borderWrapper.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("avatars\\borderframe.png"))));
        borderWrapper.add(scrollPane).size(200 , 200).center();
        borderWrapper.setPosition(300, 770);
        borderWrapper.setDebug(true);
        stage.addActor(borderWrapper);
        //endregion

        //region Layout Logo
        selectLayout = new Texture("logos\\layout.png");
        chooseLayout = new ImageButton(new TextureRegionDrawable(new TextureRegion(selectLayout)));
        chooseLayout.setPosition(theBackground.getX() + 50, theBackground.getY() + 570);
        stage.addActor(chooseLayout);
        //endregion

        //region Layout Selection
        LayoutType[] layoutTypes = LayoutType.values();
        Texture[] layoutTextures = Arrays.stream(LayoutType.values())
            .map(layout -> new Texture(layout.texturePath1)) // only use the first texture
            .toArray(Texture[]::new);

        Table layoutsTable = new Table();
        Array<Image> layoutsImage = new Array<>(); // keep references
        Array<Integer> clickCounts2 = new Array<>();

        for (int i = 0; i < layoutTextures.length; i++) {
            final int index = i;
            final LayoutType layout = layoutTypes[i];
            clickCounts2.add(0);

            final Image layoutImage = new Image(new TextureRegionDrawable(new TextureRegion(layoutTextures[i])));
            layoutImage.setSize(300, 300);
            layoutImage.setScale(1.3f);
            layoutsImage.add(layoutImage);
            layoutImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int currentClicks = clickCounts2.get(index) + 1;
                    clickCounts2.set(index, currentClicks);

                    System.out.println("LAYOUT CHOSEN: " + layout);
                    if(currentClicks == 1){
                        selectedLayout = layout;
                        layoutImage.setScale(2f);
                    } else if(currentClicks == 2){
                        layoutImage.setScale(1.3f);
                    } else { System.out.println("Multiple clicks (" + currentClicks + ") on " + layout); }
                }
            });
            layoutsTable.add(layoutImage).pad(20);
        }
        layoutsTable.pack();

        ScrollPane scrollPane2 = new ScrollPane(layoutsTable, scrollStyle);
        scrollPane2.setScrollingDisabled(false, true); // enable X scroll only
        scrollPane2.setFlingTime(0.5f);
        scrollPane2.setOverscroll(false, false);
        scrollPane2.setSmoothScrolling(true);
        scrollPane2.setSize(200 , 200);
        scrollPane2.setPosition(300, 520);
        scrollPane2.setDebug(true);

        Table borderWrapper2 = new Table();
        borderWrapper2.setSize(300, 200); // slightly bigger than ScrollPane
        borderWrapper2.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("avatars\\borderframe.png"))));
        borderWrapper2.add(scrollPane2).size(200 , 200).center();
        borderWrapper2.setPosition(300, 540);
        borderWrapper2.setDebug(true);
        stage.addActor(borderWrapper2);
        //endregion

        //region Level logo
        selectSpeed = new Texture("logos\\level.png");
        chooseSpeed = new ImageButton(new TextureRegionDrawable(new TextureRegion(selectSpeed)));
        chooseSpeed.setPosition(theBackground.getX() + 50, theBackground.getY() + 370);
        chooseSpeed.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                //createDifficultyDialog();
            }
        });
        stage.addActor(chooseSpeed);
        //endregion

        //region Select Speed
        CheckBox easyBox = new CheckBox("Easy", skin);
        CheckBox mediumBox = new CheckBox("Medium", skin);
        CheckBox hardBox = new CheckBox("Hard", skin);

        styleCheckbox(easyBox);
        styleCheckbox(mediumBox);
        styleCheckbox(hardBox);

        Table difficultyTable = new Table();
        difficultyTable.setPosition(500, 440); // Adjust as needed
        difficultyTable.defaults().padRight(10);
        difficultyTable.add(easyBox).padRight(10);
        difficultyTable.add(mediumBox).padRight(10);
        difficultyTable.add(hardBox);
        stage.addActor(difficultyTable);

        easyBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (easyBox.isChecked()) {
                    mediumBox.setChecked(false);
                    hardBox.setChecked(false);
                    hasChoseDifficulty = true;
                    game.setCurrentDifficulty(SnakeGame.Difficulty.EASY);
                }
            }
        });
        mediumBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mediumBox.isChecked()) {
                    easyBox.setChecked(false);
                    hardBox.setChecked(false);
                    hasChoseDifficulty = true;
                    game.setCurrentDifficulty(SnakeGame.Difficulty.MEDIUM);
                }
            }
        });
        hardBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (hardBox.isChecked()) {
                    easyBox.setChecked(false);
                    mediumBox.setChecked(false);
                    hasChoseDifficulty = true;
                    game.setCurrentDifficulty(SnakeGame.Difficulty.HARD);
                }
            }
        });

        //endregion

        //region Choose Map
        selectMap = new Texture("buttons\\map.png");
        chooseMap = new ImageButton(new TextureRegionDrawable(new TextureRegion(selectMap)));
        chooseMap.setPosition(theBackground.getX() + 70, theBackground.getY() + 220);
        stage.addActor(chooseMap);

        CheckBox borderBox = new CheckBox("With border", skin);
        CheckBox noBorderBox = new CheckBox("Without border", skin);

        styleCheckbox(borderBox);
        borderBox.padLeft(10).center();
        styleCheckbox(noBorderBox);
        noBorderBox.padLeft(10).center();

        Table mapTable = new Table();
        mapTable.setPosition(500, 280); // Adjust as needed
        mapTable.defaults().padRight(10);
        mapTable.add(borderBox).row();
        mapTable.add(noBorderBox);
        stage.addActor(mapTable);

        borderBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (borderBox.isChecked()) {
                    noBorderBox.setChecked(false);
                    hasChoseMap = true;
                    // Set bordered mode
                    GameApi.setBorderlessMode(false);
                }
            }
        });

        noBorderBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (noBorderBox.isChecked()) {
                    borderBox.setChecked(false);
                    hasChoseMap = true;
                    // Set borderless mode
                    GameApi.setBorderlessMode(true);
                }
            }
        });
        //endregion

        //region Play Button
        playBtn = new Texture("buttons\\play2.png");
        playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playBtn)));
        playButton.setPosition(
            theBackground.getX() + 300,
            theBackground.getY() + 80
        );
        game.buttonAnimation(playButton);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                System.out.println("SELECTED FOOD: " + selectedFoods);
                if(selectedFoods.size != 3 && selectedFoods.size > 0 ){
                    System.out.println("You need to choose 3 foods.");
                    message.setText("You need to choose only\n 3 types of foods!");
                    game.dialogTextAnimation(message, false);
                    warningDialog.show(stage);
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            warningDialog.hide();
                        }
                    }, 1f);
                    return;
                } else if(selectedFoods.size == 0){
                    selectedFoods.add(FoodType.APPLE);
                    selectedFoods.add(FoodType.BANANA);
                    selectedFoods.add(FoodType.GRAPE);
                }

                if(selectedLayout == null){
                    selectedLayout = LayoutType.BASIC;
                }
                if(!hasChoseDifficulty){
                    game.setCurrentDifficulty(SnakeGame.Difficulty.MEDIUM);
                }
                if(!hasChoseMap){
                    // Set default to bordered mode if no choice was made
                    GameApi.setBorderlessMode(false);
                }
                game.setScreen(new GameScreen(game, selectedFoods, selectedLayout));
            }
        });
        stage.addActor(playButton);
        //endregion
    }

    private void createDifficultyDialog() {
        String[] options = {"EASY", "MEDIUM", "HARD"};
        int choice = JOptionPane.showOptionDialog(null,
            "Select Difficulty",
            "Difficulty Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]); // Default to MEDIUM

        if (choice != JOptionPane.CLOSED_OPTION) {
            game.setCurrentDifficulty(SnakeGame.Difficulty.valueOf(options[choice]));
        } else {
            // If user closes dialog, default to MEDIUM
            game.setCurrentDifficulty(SnakeGame.Difficulty.MEDIUM);
        }
    }

    private void styleCheckbox(CheckBox checkbox){
        checkbox.getImage().setScaling(Scaling.fill);
        checkbox.getImageCell().size(40, 40);
        checkbox.getLabel().setFontScale(2f);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        background.dispose();
        playBtn.dispose();
        selectFood.dispose();
        selectLayout.dispose();
        selectSpeed.dispose();
        selectMap.dispose();
        skin.dispose();
        stage.dispose();
    }
}
