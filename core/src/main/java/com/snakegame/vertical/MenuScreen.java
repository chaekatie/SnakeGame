package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {
    private Texture mainBg, logout;
    private Image baseBg, avatarImage;
    private ImageButton play, leader, prize, setting, about, logoutBtn;
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture playButton, prizeButton, leaderButton, settingButton, aboutButton, avatarTexture;

    public MenuScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        mainBg = new Texture("backgrounds\\menubg.jpg");
        baseBg = new Image(mainBg);
        game.appearTransition(baseBg);
        stage.addActor(baseBg);

        //region Play button
        playButton = new Texture("buttons\\play2.png");
        TextureRegionDrawable playDraw = new TextureRegionDrawable(new TextureRegion(playButton));
        play = new ImageButton(playDraw);
        play.setPosition(
            baseBg.getX() + 130,
            baseBg.getY() + 885
        );
        game.buttonAnimation(play);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                baseBg.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> leader.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> prize.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> setting.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> about.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> play.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new GameScreen(game));
                    })
                ));
            }
        });
        //endregion

        //region Prize Button
        prizeButton = new Texture("buttons\\prize.png");
        TextureRegionDrawable prizeDraw = new TextureRegionDrawable(new TextureRegion(prizeButton));
        prize = new ImageButton(prizeDraw);
        prize.setPosition(
            baseBg.getX() + 130,
            baseBg.getY() + 710
        );
        game.buttonAnimation(prize);
        prize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                baseBg.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> leader.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> play.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> setting.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> about.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> prize.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new PrizeScreen(game));
                    })
                ));
            }
        });
        //endregion

        //region Leader Button
        leaderButton = new Texture("buttons\\leader.png");
        TextureRegionDrawable leaderDraw = new TextureRegionDrawable(new TextureRegion(leaderButton));
        leader = new ImageButton(leaderDraw);
        leader.setPosition(
            baseBg.getX() + 520,
            baseBg.getY() + 540
        );
        game.buttonAnimation(leader);
        leader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                baseBg.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> play.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> prize.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> setting.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> about.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> leader.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new LeaderScreen(game));
                    })
                ));
            }
        });
        //endregion

        //region Setting Button
        settingButton = new Texture("buttons\\setting.png");
        TextureRegionDrawable settingDraw = new TextureRegionDrawable(new TextureRegion(settingButton));
        setting = new ImageButton(settingDraw);
        setting.setPosition(
            baseBg.getX() + 520,
            baseBg.getY() + 380
        );
        game.buttonAnimation(setting);
        setting.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                baseBg.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> leader.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> prize.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> play.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> about.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> setting.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new SettingScreen(game));
                    })
                ));
            }
        });
        //endregion

        //region About Button
        aboutButton = new Texture("buttons\\about.png");
        TextureRegionDrawable aboutDraw = new TextureRegionDrawable(new TextureRegion(aboutButton));
        about = new ImageButton(aboutDraw);
        about.setPosition(
            baseBg.getX() + 520,
            baseBg.getY() + 200
        );
        game.buttonAnimation(about);
        about.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                baseBg.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> leader.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> prize.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> play.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> setting.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> about.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new AboutScreen(game));
                    })
                ));
            }
        });
        //endregion

        //region Logout buttons
        logout = new Texture("buttons\\logout.png");
        logoutBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(logout)));
        game.buttonAnimation(logoutBtn);
        logoutBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new FirstScreen(game));
            }
        });
        logoutBtn.setPosition(
            baseBg.getX() + 580,
            baseBg.getY()
        );
        stage.addActor(logoutBtn);

//        nextBtn = game.activateNextButton(baseBg);
//        game.buttonAnimation(nextBtn);
//        nextBtn.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.clicking.play(2f);
//                game.setScreen(new MenuScreen(game));
//            }
//        });
//        stage.addActor(nextBtn);
        //endregion

        //region Avatar Image
        //endregion

        stage.addActor(play);
        stage.addActor(prize);
        stage.addActor(leader);
        stage.addActor(setting);
        stage.addActor(about);
    }

    public void setAvatarImage(String image){
        avatarTexture = new Texture(image);
        TextureRegionDrawable avatarDrawable = new TextureRegionDrawable(new TextureRegion(avatarTexture));
        avatarImage = new Image(avatarDrawable);
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
        stage.dispose();
        mainBg.dispose();
        playButton.dispose();
        prizeButton.dispose();
        leaderButton.dispose();
        settingButton.dispose();
        aboutButton.dispose();
    }
}
