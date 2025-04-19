package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LeaderScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture background, leader, board;
    private Image backgroundImage, leaderLogo, boardImage;
    private ImageButton backButton;

    public LeaderScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        //background = new Texture("backgrounds\\bgmenu.png");
        background = new Texture("backgrounds\\emptyboard.jpg");
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        backgroundImage = new Image(backgroundDrawable);
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

        leader = new Texture("logos\\leaderboard.png");
        TextureRegionDrawable leaderlogo = new TextureRegionDrawable(new TextureRegion(leader));
        leaderLogo = new Image(leaderlogo);
        game.imageAnimation(leaderLogo);
        leaderLogo.setPosition(
            backgroundImage.getX() + 230,
            backgroundImage.getY() + 900
        );
        stage.addActor(leaderLogo);

//        board = new Texture("backgrounds\\bgempty.png");
//        TextureRegionDrawable boardDrawable = new TextureRegionDrawable(new TextureRegion(board));
//        boardImage = new Image(boardDrawable);
//        game.imageAnimation(boardImage);
//        stage.addActor(boardImage);
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

    }
}
