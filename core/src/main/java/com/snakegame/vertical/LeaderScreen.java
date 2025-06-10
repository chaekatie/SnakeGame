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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LeaderScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture background, leader, board;
    private Image backgroundImage, leaderLogo, boardImage;
    private ImageButton backButton, globalButton;
    private Label.LabelStyle customLabel;
    private Skin skin;
    private Table rowsTable;
    private boolean isLoggedIn;
    private Texture[] avatarsTexture;

    public LeaderScreen(SnakeGame game){
        this.game = game;
        this.isLoggedIn = game.getLoggedIn();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        param.color = Color.WHITE;
        BitmapFont myFont = generator.generateFont(param);
        generator.dispose();

        customLabel = new Label.LabelStyle();
        customLabel.font = myFont;
        avatarsTexture = game.avatarsTexture;

        rowsTable = new Table();
        Table allScoresTable = new Table();
        Stack boardStack = new Stack();
        Table scrollContainer = new Table();

        //region Background
        background = new Texture("backgrounds\\background.jpg");
        backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);

        board = new Texture("backgrounds\\bgempty.png");
        boardImage = new Image(new TextureRegionDrawable(new TextureRegion(board)));
        //endregion

        //region Back button
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
        //endregion

        //region Scores Table for leaderboard
        float widthh = boardImage.getPrefWidth();
        float heightt = boardImage.getPrefHeight();

        ScrollPane scrollPane = declareScrollPane(rowsTable, widthh - 40, heightt - 150);
        scrollContainer.add(scrollPane).size(widthh - 40, heightt - 150).pad(0, -30, 50, 20);

        boardStack.add(boardImage);
        boardStack.setSize(widthh - 40, heightt - 120);
        boardStack.add(scrollContainer);

        allScoresTable.setFillParent(true);
        allScoresTable.add(boardStack).pad(0, 10, 10, 5);
        stage.addActor(allScoresTable);
        allScoresTable.setY(allScoresTable.getY() + 50);
        //endregion

        loadAllScores();

        //region Leaderboard logo
        leader = new Texture("logos\\leaderboard2.png");
        leaderLogo = new Image(new TextureRegionDrawable(new TextureRegion(leader)));
        leaderLogo.setPosition(backgroundImage.getX() + 150, backgroundImage.getY() + 1000);
        //stage.addActor(leaderLogo);
        //endregion

        //region All scores Button
        Texture allScores = new Texture("buttons\\global.png");
        globalButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(allScores)));
        globalButton.setPosition(backgroundImage.getX() + 200, backgroundImage.getY() + 150);
        stage.addActor(globalButton);

        globalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                loadAllScores();
            }
        });
        //endregion

        //region Filter Leaderboard Button
        Texture filter = new Texture("buttons\\filter.png");
        ImageButton filterBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(filter)));
        filterBtn.setPosition(backgroundImage.getX() + 300, backgroundImage.getY() + 40);
        stage.addActor(filterBtn);
        //endregion

        //region Choose Filter
        CheckBox weekBox = new CheckBox("By Week", skin);
        CheckBox monthBox = new CheckBox("By Month", skin);

        game.styleCheckbox(weekBox);
        game.styleCheckbox(monthBox);

        Table mapTable = new Table();
        mapTable.setPosition(600, 100);
        mapTable.defaults().padRight(10);
        mapTable.add(weekBox).row();
        mapTable.add(monthBox);
        stage.addActor(mapTable);

        weekBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (weekBox.isChecked()) {
                    monthBox.setChecked(false);
                    loadScoresByWeek();
                }
            }
        });
        monthBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (monthBox.isChecked()) {
                    weekBox.setChecked(false);
                    loadScoresByMonth();
                }
            }
        });
        //endregion

    }

    public Texture filterTexture(UserHighScoreDTO score){
        Texture avatarTexture;
        if(game.getUsername().equals(score.getUsername())){
            avatarTexture = game.getChosenAvatar();
        } else {
            avatarTexture = avatarsTexture[MathUtils.random(avatarsTexture.length - 1)];
        }
        return avatarTexture;
    }

    public ScrollPane declareScrollPane(Table table, float width, float height){
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // only allow vertical scroll
        scrollPane.setSize(width, height);
        return scrollPane;
    }

    private void loadAllScores(){
        rowsTable.clear();

        GameApi.getUserHighScores(new GameApi.UserHighScoreCallback() {
            @Override
            public void onSuccess(UserHighScoreDTO[] scores) {
                if (scores.length == 0) {
                    Label emptyLabel = new Label("No scores yet!", customLabel);
                    rowsTable.add(emptyLabel).pad(20);
                    return;
                }

                for (int i = 0; i < scores.length; i++){
                    UserHighScoreDTO score = scores[i];
                    rowsTable.row();
                    Texture avatarTexture = filterTexture(score);
                    rowsTable.add(createPlayerRow(i+1, score.getUsername(), score.getHighScore(), avatarTexture))
                        .width(280).height(50).padBottom(20);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR LOADING THE SCORES: " + t.getMessage());
            }
        });
    }

    private void loadScoresByWeek(){
        rowsTable.clear();

        GameApi.getAllScoresByTime("week", new GameApi.ScoreFilterCallback() {
            @Override
            public void onSuccess(UserHighScoreDTO[] scores) {
                if (scores.length == 0) {
                    Label emptyLabel = new Label("No scores yet!", customLabel);
                    rowsTable.add(emptyLabel).pad(20);
                    return;
                }

                for (int i = 0; i < scores.length; i++) {
                    UserHighScoreDTO score = scores[i];
                    rowsTable.row();
                    Texture avatarTexture = filterTexture(score);
                    rowsTable.add(createPlayerRow(i+1, score.getUsername(), score.getHighScore(), avatarTexture))
                        .width(280).height(50).padBottom(20);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Failed to load weekly scores: " + t.getMessage());
            }
        });
    }

    private void loadScoresByMonth(){
        rowsTable.clear();

        GameApi.getAllScoresByTime("month", new GameApi.ScoreFilterCallback() {
            @Override
            public void onSuccess(UserHighScoreDTO[] scores) {
                if (scores.length == 0) {
                    Label emptyLabel = new Label("No scores yet!", customLabel);
                    rowsTable.add(emptyLabel).pad(20);
                    return;
                }

                for (int i = 0; i < scores.length; i++) {
                    UserHighScoreDTO score = scores[i];
                    rowsTable.row();
                    Texture avatarTexture = filterTexture(score);
                    rowsTable.add(createPlayerRow(i+1, score.getUsername(), score.getHighScore(), avatarTexture))
                        .width(280).height(50).padBottom(20);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Failed to load weekly scores: " + t.getMessage());
            }
        });
    }
    @Override
    public void show() {
    }

    private String formatRank(int rank) {
        if (rank == 1) return "1st";
        if (rank == 2) return "2nd";
        if (rank == 3) return "3rd";
        return rank + "th";
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

        Label rankLabel = new Label(formatRank(rank), customLabel);
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

    public Table createUserRow(int rank, int score, String time) {
        Table row = new Table();
        row.defaults().pad(5).height(60);

        Color bgColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.2f);
        row.setBackground(skin.newDrawable("white", bgColor));

        Label rankLabel = new Label(String.valueOf(rank), customLabel);
        rankLabel.setAlignment(Align.right);

        Label scoreLabel = new Label(String.valueOf(score), customLabel);
        scoreLabel.setAlignment(Align.left);

        Label timeLabel = new Label(time, customLabel);
        timeLabel.setAlignment(Align.left);

        row.add(rankLabel).width(30).pad(10,0,20,20);
        row.add(timeLabel).width(300).pad(10,10,10,30);
        row.add(scoreLabel).width(80).pad(10,30,10,10);
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
