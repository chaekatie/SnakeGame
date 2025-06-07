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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;

public class GameSettings implements Screen {
    private SnakeGame game;
    private Texture background, board, playBtn, level, selectFood, selectLayout, selectSnake;
    private Texture[] foodsTexture;
    private Image theBackground, boardImage;
    private ImageButton chooseFood, chooseLayout, choosePattern, backButton, playButton, levelButton;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Label.LabelStyle customLabel;
    private boolean isLoggedIn;
    private final Array<FoodType> selectedFoods = new Array<>();

    public GameSettings(SnakeGame game, boolean isLoggedIn){
        this.game = game;
        this.isLoggedIn = isLoggedIn;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        selectedFoods.add(FoodType.APPLE);
        selectedFoods.add(FoodType.BANANA);
        selectedFoods.add(FoodType.KIWI);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        param.color = Color.GOLDENROD;
        BitmapFont myFont = generator.generateFont(param);
        generator.dispose();

        customLabel = new Label.LabelStyle();
        customLabel.font = myFont;

        //region Background
        background = new Texture("backgrounds\\background.jpg");
        theBackground = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(theBackground);
        stage.addActor(theBackground);

        board = new Texture("backgrounds\\bgempty.png");
        boardImage = new Image(new TextureRegionDrawable(new TextureRegion(board)));
        //stage.addActor(boardImage);
        //endregion

        //region Back button
        backButton = game.activateBackButton(theBackground);
        game.buttonAnimation(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new MenuScreen(game, isLoggedIn));
            }
        });
        stage.addActor(backButton);
        //endregion

        //region Play Button
        playBtn = new Texture("buttons\\play2.png");
        playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playBtn)));
        playButton.setPosition(
            theBackground.getX() + 300,
            theBackground.getY() + 300
        );
        game.buttonAnimation(playButton);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new GameScreen(game, selectedFoods));
            }
        });
        stage.addActor(playButton);
        //endregion

        //region Food Logo
        selectFood = new Texture("logos\\food.png");
        chooseFood = new ImageButton(new TextureRegionDrawable(new TextureRegion(selectFood)));
        chooseFood.setPosition(theBackground.getX() + 50, theBackground.getY() + 780);
        game.buttonAnimation(chooseFood);
        stage.addActor(chooseFood);
        //endregion

        //region Food Selection
        FoodType[] foodTypes = FoodType.values();
        foodsTexture = Arrays.stream(FoodType.values())
            .map(food -> new Texture(food.texturePath))
            .toArray(Texture[]::new);

        Table foodsTable = new Table();
        Array<Image> foodsImage = new Array<>(); // keep references

        for (int i = 0; i < foodsTexture.length; i++) {
            final int index = i;
            final FoodType food = foodTypes[i];

            final Image foodImage = new Image(new TextureRegionDrawable(new TextureRegion(foodsTexture[i])));
            foodImage.setSize(300, 300);
            foodsImage.add(foodImage);

            foodImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedFoods.contains(food, false)) {
                        selectedFoods.removeValue(food, false);
                        foodImage.setScale(1f); // unhighlight
                    } else {
                        if (selectedFoods.size != 3) {
                            selectedFoods.add(food);
                            foodImage.setScale(1.5f); // highlight
                        } else {
                            System.out.println("You need to choose 3 foods.");
                        }
                    }
                    System.out.println("SELECTED FOOD: " + selectedFoods);
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
        scrollPane.setPosition(300, 780);
        scrollPane.setDebug(true);

        Table borderWrapper = new Table();
        borderWrapper.setSize(300, 200); // slightly bigger than ScrollPane
        borderWrapper.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("avatars\\borderframe.png"))));
        borderWrapper.add(scrollPane).size(200 , 200).center();
        borderWrapper.setPosition(300, 750);
        borderWrapper.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        borderWrapper.setDebug(true);
        stage.addActor(borderWrapper);
        //endregion

        //region Level logo
        level = new Texture("logos\\level.png");
        levelButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(level)));
        game.buttonAnimation(levelButton);
        levelButton.setPosition(theBackground.getX() + 50, theBackground.getY() + 600);
        stage.addActor(levelButton);
        //endregion

        //region Inform Label
        Label greeting = new Label("Feel free to choose your favorite food,\nsnake type or the layout you want here!", customLabel);
        Label greeting2 = new Label("Or continue with the default set up by \nclicking the Play button down there!", customLabel);
        greeting.setPosition(theBackground.getX() + 50, theBackground.getY() + 1100);
        greeting2.setPosition(theBackground.getX() + 50, theBackground.getY() + 1000);
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

    }
}
