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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PrizeScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture background, achievement, board, record, detail;
    private Image backgroundImage, achievementLogo, boardImage;
    private ImageButton backButton, recordButton, detailButton;
    private Label.LabelStyle customLabel, customLabel2;
    private Skin skin;
    private Table rowsTable;
    private boolean isLoggedIn;
    private Dialog matchDetailsDialog;
    private TextureRegionDrawable dialogDrawble;
    private int veryTotalScores, deadTimes, playTimes;
    private Label totalScores, totalDie, totalPlaytime;

    public PrizeScreen(SnakeGame game){
        this.game = game;
        this.isLoggedIn = game.getLoggedIn();

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Montserrat-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 32;
        param.color = Color.BLACK;
        BitmapFont myFont = generator.generateFont(param);
        generator.dispose();

        Texture dialogTex = new Texture("backgrounds\\table.png");
        dialogDrawble = new TextureRegionDrawable(new TextureRegion(dialogTex));

        customLabel = new Label.LabelStyle();
        customLabel.font = game.theBigFont;
        customLabel2 = new Label.LabelStyle();
        customLabel2.font = game.theSmallFont;

        veryTotalScores = deadTimes = playTimes = 0;

        rowsTable = new Table();
        Table myScoresTable = new Table();
        Stack boardStack = new Stack();
        Table scrollContainer = new Table();

        //region Background
        background = new Texture("backgrounds\\background.jpg");
        backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);

        board = new Texture("backgrounds\\emptybg_square.png");
        boardImage = new Image(new TextureRegionDrawable(new TextureRegion(board)));
        boardImage.setPosition(backgroundImage.getX() + 80, backgroundImage.getY() + 600);
        stage.addActor(boardImage);
        //endregion

        //region Back button
        backButton = game.activateBackButton(backgroundImage);
        game.buttonAnimation(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(game.getSfxVolume());
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(backButton);
        //endregion

        //region Scores Table
        float widthh = boardImage.getWidth();
        float heightt = boardImage.getHeight();

        ScrollPane scrollPane = declareScrollPane(rowsTable, widthh - 100, heightt - 100);
        scrollContainer.add(scrollPane).size(widthh - 100, heightt - 100).pad(0, 10, 50, 20);
        boardStack.setSize(widthh - 100, heightt - 100);
        boardStack.add(scrollContainer);

        myScoresTable.add(boardStack).pad(10, 10, 10, 5);
        myScoresTable.pack();
        myScoresTable.setPosition(backgroundImage.getX() + 100, backgroundImage.getY() + 600);
        stage.addActor(myScoresTable);
        //endregion

        //region Achivement logo
        achievement = new Texture("logos\\achievement.png");
        achievementLogo = new Image(new TextureRegionDrawable(new TextureRegion(achievement)));
        achievementLogo.setPosition(backgroundImage.getX() + 150, backgroundImage.getY() + 1090);
        stage.addActor(achievementLogo);
        //endregion

        //region Your Records button
        record = new Texture("buttons\\record.png");
        recordButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(record)));
        recordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                loadMyScores();
            }
        });
        recordButton.setPosition(backButton.getX() + 400, backgroundImage.getY() + 450);
        stage.addActor(recordButton);
        //endregion

        loadMatchDetail();

        //region Match details button
        detail = new Texture("buttons\\detail.png");
        detailButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(detail)));
        detailButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                loadMatchDetail();
            }
        });
        detailButton.setPosition(backgroundImage.getX() + 100, backgroundImage.getY() + 450);
        stage.addActor(detailButton);
        //endregion

        //region Filter Achivement Button
        Texture filter = new Texture("buttons\\filter.png");
        ImageButton filterBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(filter)));
        filterBtn.setPosition(backgroundImage.getX() + 300, backgroundImage.getY() + 40);
        filterBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(game.getSfxVolume());
                loadMatchDetail();
            }
        });
        stage.addActor(filterBtn);
        //endregion

        //region Summary
        Texture frame = new Texture("avatars\\flowerframe.png");
        Image frameImage = new Image(new TextureRegionDrawable(new TextureRegion(frame)));
        frameImage.setSize(400, 500);
        frameImage.setPosition(backgroundImage.getX() + 180, backgroundImage.getY() + 200);
        //stage.addActor(frameImage);

        totalDie = new Label("Total Dead: " + deadTimes, customLabel);
        totalScores = new Label("Total Scores: " + veryTotalScores, customLabel);
        totalPlaytime = new Label("Total Playing times: " + playTimes, customLabel);
        Table summaryTable = new Table();
        summaryTable.add(totalDie).row();
        summaryTable.add(totalScores).row();
        summaryTable.add(totalPlaytime).row();
        summaryTable.pack();
        summaryTable.setPosition(backgroundImage.getX() + 130, backgroundImage.getY() + 220);
        stage.addActor(summaryTable);
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

        // region Match Details Dialog
        matchDetailsDialog = new Dialog("MATCH DETAILS", skin) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                        matchDetailsDialog.hide();
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

        matchDetailsDialog.getContentTable().setBackground(dialogDrawble);
        Label message = new Label("MATCH DETAILS!",customLabel);
        Label scoreMessage = new Label("Total Score: 0", customLabel);
        Label playtimeMessage = new Label("Playing time: ", customLabel);
        Label food1Message = new Label ("Normal Food: 0 Apple", customLabel);
        Label food2Message = new Label ("Special Food: 0 Grape", customLabel);
        Label food3Message = new Label ("Golden Food: 0 Strawberry", customLabel);

        matchDetailsDialog.text(message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(scoreMessage).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(playtimeMessage).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food1Message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food2Message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food3Message).center();

        matchDetailsDialog.button("Close", true);
        //endregion

        // endregion

    }

    public ScrollPane declareScrollPane(Table table, float width, float height){
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // only allow vertical scroll
        scrollPane.setSize(width, height);
        return scrollPane;
    }

    private void loadMyScores(){
        rowsTable.clear();

        GameApi.getUserScores(new GameApi.MyScoreCallback() {
            @Override
            public void onSuccess(ScoreDTO[] myScores) {
                if (myScores.length == 0) {
                    Label emptyLabel = new Label("You have no scores yet!", customLabel);
                    rowsTable.add(emptyLabel);
                    return;
                }

                for (int i = 0; i < myScores.length; i++){
                    ScoreDTO score = myScores[i];
                    rowsTable.row();
                    rowsTable.add(createUserRow(i+1, score.getScore(), score.getTime())).width(280).height(50).padBottom(20).row();
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR LOADING THE SCORES: " + t.getMessage());
            }
        });

    }

    private void loadMatchDetail(){
        rowsTable.clear();

        GameApi.getGameMatch(new GameApi.GetMatchCallback() {
            @Override
            public void onSuccess(MatchDTO[] matchDetails) {
                if (matchDetails.length == 0) {
                    Label emptyLabel = new Label("You have no match details yet!", customLabel);
                    rowsTable.add(emptyLabel);
                    return;
                }

                for (int i = 0; i < matchDetails.length; i++){
                    MatchDTO match = matchDetails[i];

                    veryTotalScores += match.getTotalScore();
                    deadTimes += 1;
                    playTimes += match.getPlayTime();

                    totalDie.setText("Total Dead: " + deadTimes);
                    totalScores.setText("Total Scores: " + veryTotalScores);
                    totalPlaytime.setText("Total Playing times: " + playTimes);

                    rowsTable.row();
                    rowsTable.add(createUserRow(i+1, match)).width(280).height(50).left().row();
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERROR LOADING THE MATCH DETAILS " + t.getMessage());
            }
        });
    }

    public Table createUserRow(int rank, MatchDTO match) {
        Table row = new Table();
        row.defaults().pad(5).height(60);

        Color bgColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.2f);
        row.setBackground(skin.newDrawable("white", bgColor));

        Label rankLabel = new Label(String.valueOf(rank), customLabel);
        rankLabel.setAlignment(Align.left);

        Label scoreLabel = new Label(String.valueOf(match.getTotalScore()), customLabel);
        scoreLabel.setAlignment(Align.left);

        Label timeLabel = new Label(String.valueOf(match.getPlayTime()), customLabel);
        timeLabel.setAlignment(Align.left);

        // Add double-click listener
        row.addListener(new InputListener() {
            private long lastClickTime = 0;
            private final long DOUBLE_CLICK_INTERVAL = 400; // milliseconds

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                long currentTime = TimeUtils.millis();
                if (currentTime - lastClickTime < DOUBLE_CLICK_INTERVAL) {
                    System.out.println("Double-clicked row: rank=" + rank);
                    showMatchDetailsDialog(match);
                }
                lastClickTime = currentTime;
                return true;
            }
        });


        row.add(rankLabel).width(50).pad(10,0,20,20);
        row.add(timeLabel).width(100).pad(10,10,10,20);
        row.add(scoreLabel).width(200).pad(10,10,10,10);
        return row;
    }

    private void showMatchDetailsDialog(MatchDTO match) {
        matchDetailsDialog  = new Dialog("MATCH DETAILS", skin);
        matchDetailsDialog.getContentTable().setBackground(dialogDrawble);

        Label message = new Label("MATCH DETAILS", customLabel2);
        Label scoreMessage = new Label("Total Score: " + match.getTotalScore(), customLabel2);
        Label playtimeMessage = new Label("Playing time: " + match.getPlayTime() + "s", customLabel2);
        Label food1Message = new Label("Normal Food: " + match.getNormalFoodCount(), customLabel2);
        Label food2Message = new Label("Special Food: " + match.getSpecialFoodCount(), customLabel2);
        Label food3Message = new Label("Golden Food: " + match.getGoldenFoodCount(), customLabel2);

        matchDetailsDialog.text(message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(scoreMessage).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(playtimeMessage).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food1Message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food2Message).center();
        matchDetailsDialog.getContentTable().row();
        matchDetailsDialog.text(food3Message).center();
        matchDetailsDialog.button("Close", true);

        matchDetailsDialog.show(stage);
    }

    public Table createUserRow(int rank, int score, String time) {
        Table row = new Table();
        row.defaults().pad(5).height(60);

        Color bgColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0.2f);
        row.setBackground(skin.newDrawable("white", bgColor));

        Label rankLabel = new Label(String.valueOf(rank), customLabel2);
        rankLabel.setAlignment(Align.left);

        Label scoreLabel = new Label(String.valueOf(score), customLabel2);
        scoreLabel.setAlignment(Align.left);

        Label timeLabel = new Label(time, customLabel2);
        timeLabel.setAlignment(Align.left);

        row.add(rankLabel).width(30).pad(10,10,10,20);
        row.add(timeLabel).width(300).pad(10,10,10,0);
        row.add(scoreLabel).width(80).pad(10,10,10,10);
        return row;
    }

    private void loadScoresByWeek(){
        rowsTable.clear();

        GameApi.getMyScoresByWeek(game.getUsername(), new GameApi.MyScoreFilterCallback() {
            @Override
            public void onSuccess(ScoreDTO[] scores) {
                if (scores.length == 0) {
                    Label emptyLabel = new Label("No scores this week yet!", customLabel);
                    rowsTable.add(emptyLabel).pad(20);
                    return;
                }

                for (int i = 0; i < scores.length; i++) {
                    ScoreDTO score = scores[i];
                    rowsTable.row();
                    rowsTable.add(createUserRow(i+1, score.getScore(), score.getTime()))
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

        GameApi.getMyScoresByMonth(game.getUsername(), new GameApi.MyScoreFilterCallback() {
            @Override
            public void onSuccess(ScoreDTO[] scores) {
                if (scores.length == 0) {
                    Label emptyLabel = new Label("No scores this month yet!", customLabel);
                    rowsTable.add(emptyLabel).pad(20);
                    return;
                }

                for (int i = 0; i < scores.length; i++) {
                    ScoreDTO score = scores[i];
                    rowsTable.row();
                    rowsTable.add(createUserRow(i+1, score.getScore(), score.getTime()))
                        .width(280).height(50).padBottom(20);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Failed to load monthly scores: " + t.getMessage());
            }
        });
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
        board.dispose();
        achievement.dispose();
        record.dispose();
        detail.dispose();
        background.dispose();
        stage.dispose();
    }
}
