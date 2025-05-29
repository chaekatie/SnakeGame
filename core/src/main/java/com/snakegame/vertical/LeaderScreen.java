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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LeaderScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture background, leader, board;
    private Image backgroundImage, leaderLogo, boardImage;
    private ImageButton backButton;
    private BitmapFont myFont;
    private Label.LabelStyle customLabel;
    private Skin skin;

    public LeaderScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
//        OrthographicCamera camera = new OrthographicCamera();
//        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
//        stage = new Stage(viewport, game.batch);
//        Gdx.input.setInputProcessor(stage);
//
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("chilanka.ttf"));
//        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        param.size = 24;
//        param.color = Color.WHITE;
//        myFont = generator.generateFont(param);
//        generator.dispose();
//        customLabel.font = myFont;
//
//        //background = new Texture("backgrounds\\bgmenu.png");
//        background = new Texture("backgrounds\\background.jpg");
//        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
//        backgroundImage = new Image(backgroundDrawable);
//        game.appearTransition(backgroundImage);
//        stage.addActor(backgroundImage);
//
//        backButton = game.activateBackButton(backgroundImage);
//        game.buttonAnimation(backButton);
//        backButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.clicking.play(2f);
//                game.setScreen(new MenuScreen(game));
//            }
//        });
//        stage.addActor(backButton);
//
//        board = new Texture("backgrounds\\bgempty.png");
//        TextureRegionDrawable boardTexture = new TextureRegionDrawable(new TextureRegion(board));
//        boardImage = new Image(boardTexture);
//        game.imageAnimation(boardImage);
//
//        Table rowsTable = new Table();
//        rowsTable.top().padTop(60);
//        // Example data
//        for (int i = 0; i < 8; i++) {
//            rowsTable.row();
//            rowsTable.add(createPlayerRow(i + 1, "USERNAME0" + (i + 1), (int)(Math.random() * 40000), new Texture("avatar.png")))
//                .width(280).height(50).padBottom(8);
//        }
//
//        Stack boardStack = new Stack();
//        boardStack.add(boardImage);
//        boardStack.setSize(boardImage.getPrefWidth(), boardImage.getPrefHeight());
//        boardStack.add(rowsTable);
        //boardStack.add(leaderLogo);
        //.pad(top, left, bottom, right)
//        Table table = new Table();
//        table.setFillParent(true); // so it resizes with screen
//        stage.addActor(table);
//        table.add(boardStack).pad(10, 10, 10, 5);
//
//        leader = new Texture("logos\\leaderboardicon.png");
//        TextureRegionDrawable leaderlogo = new TextureRegionDrawable(new TextureRegion(leader));
//        leaderLogo = new Image(leaderlogo);
//        game.imageAnimation(leaderLogo);
//        leaderLogo.setPosition(
//            backgroundImage.getX() + 110,
//            backgroundImage.getY() + 700
//        );
//        stage.addActor(leaderLogo);

//        board = new Texture("backgrounds\\bgempty.png");
//        TextureRegionDrawable boardDrawable = new TextureRegionDrawable(new TextureRegion(board));
//        boardImage = new Image(boardDrawable);
//        game.imageAnimation(boardImage);
//        stage.addActor(boardImage);

    }

    @Override
    public void show() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        param.color = Color.WHITE;
        myFont = generator.generateFont(param);
        generator.dispose();
        customLabel = new Label.LabelStyle();
        customLabel.font = myFont;

        background = new Texture("backgrounds\\background.jpg");
        backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);

        backButton = game.activateBackButton(backgroundImage);
        game.buttonAnimation(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(backButton);

        board = new Texture("backgrounds\\bgempty.png");
        boardImage = new Image(new TextureRegionDrawable(new TextureRegion(board)));
        //game.imageAnimation(boardImage);

        Table rowsTable = new Table();
        rowsTable.top();

        for (int i = 0; i < 20 ; i++) {
            rowsTable.row();
            rowsTable.add(createPlayerRow(i + 1, "Player 0" + (i + 1), (int)(Math.random() * 40000), new Texture("logos\\face.png")))
                .width(280).height(50).padBottom(20);
        }

        ScrollPane scrollPane = new ScrollPane(rowsTable);
        scrollPane.setFadeScrollBars(false); // optional: always show scroll bar
        scrollPane.setScrollingDisabled(true, false); // only allow vertical scroll
        scrollPane.setSize(boardImage.getPrefWidth() - 40, boardImage.getPrefHeight() - 150);

        Table scrollContainer = new Table();
        scrollContainer.add(scrollPane).size(boardImage.getPrefWidth() - 40, boardImage.getPrefHeight() - 150).pad(80, -30, 50, 20);
        //scrollContainer.debug();

        Stack boardStack = new Stack();
        boardStack.add(boardImage);
        boardStack.setSize(boardImage.getPrefWidth() - 40, boardImage.getPrefHeight() - 120);
        //boardStack.add(rowsTable);
        boardStack.add(scrollContainer);
        //boardStack.debug();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.add(boardStack).pad(10, 10, 10, 5);

        leader = new Texture("logos\\leaderboard2.png");
        leaderLogo = new Image(new TextureRegionDrawable(new TextureRegion(leader)));
        game.imageAnimation(leaderLogo);
        leaderLogo.setPosition(backgroundImage.getX() + 150, backgroundImage.getY() + 770);
        stage.addActor(leaderLogo);
    }

    public Table createPlayerRow(int rank, String username, int score, Texture avatarTexture) {
        Table row = new Table();
        row.defaults().pad(5).height(60);

        Color bgColor;
        if (rank == 1) bgColor = Color.GOLD;
        else if (rank == 2) bgColor = new Color(0.75f, 0.75f, 0.75f, 1); //silver
        else if (rank == 3) bgColor = new Color(0.8f, 0.5f, 0.2f, 1); // bronze
        else bgColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.2f);
        row.setBackground(skin.newDrawable("white", bgColor));

        Label rankLabel = new Label(String.valueOf(rank), customLabel);
        rankLabel.setAlignment(Align.center);

        Image avatar = new Image(avatarTexture);

        Label nameLabel = new Label(username, customLabel);
        nameLabel.setAlignment(Align.left);

        Label scoreLabel = new Label(String.valueOf(score), customLabel);
        scoreLabel.setAlignment(Align.left);

        row.add(rankLabel).width(30).pad(10,10,10,20);
        row.add(avatar).size(64, 64).pad(10,10,10, 30);
        row.add(nameLabel).width(200).pad(10,10,10,20);
        row.add(scoreLabel).width(80).pad(10,10,10,10);

        //row.debug();
        return row;
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

    }
}
