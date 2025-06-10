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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.text.View;

public class AboutScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Texture background, bubble, bubble1, bubble2, play, team, gamee, about;
    private Image theBackground, bubbleLogo, bubble1Logo, bubble2Logo, playLogo, teamLogo, gameLogo, aboutLogo;
    private Viewport viewport;
    private ImageButton backButton;
    private Label playLabel, builtLabel, developerLabel, thanksLabel;
    private OrthographicCamera camera;
    private boolean isLoggedIn;

    public AboutScreen(SnakeGame game){
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        this.isLoggedIn = game.getLoggedIn();

        //region Background and Main logo
        background = new Texture("backgrounds\\emptybg.png");
        theBackground = new Image(background);
        game.appearTransition(theBackground);
        stage.addActor(theBackground);

        backButton = game.activateBackButton(theBackground);
        game.buttonAnimation(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(game.getSfxVolume());
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(backButton);

        about = new Texture("logos\\aboutlogo.png");
        TextureRegionDrawable aboutlogo = new TextureRegionDrawable(new TextureRegion(about));
        aboutLogo = new Image(aboutlogo);
        aboutLogo.setPosition(
            theBackground.getX() + 280,
            theBackground.getY() + 920
        );
        game.imageAnimation(aboutLogo);
        stage.addActor(aboutLogo);
        //endregion

        //region Custom Font and Label
        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;
        customLabel.fontColor = Color.BLACK;
        //endregion

        //region How to play
        bubble = new Texture("randoms\\bubble.png");
        TextureRegionDrawable bubblelogo = new TextureRegionDrawable(new TextureRegion(bubble));
        bubbleLogo = new Image(bubblelogo);
        bubbleLogo.setPosition(
            theBackground.getX() + 180,
            theBackground.getY() + 650
        );
        game.imageAnimation(bubbleLogo);
        stage.addActor(bubbleLogo);

        play = new Texture("logos\\gamehandle.png");
        TextureRegionDrawable playlogo = new TextureRegionDrawable(new TextureRegion(play));
        playLogo = new Image(playlogo);
        playLogo.setPosition(
            theBackground.getX() + 500,
            theBackground.getY() + 850
        );
        game.imageAnimation(playLogo);
        stage.addActor(playLogo);

        playLabel = new Label("Use arrow keys\n or swipe to move.\n"
            + "Eat the food to grow.\n" + "Don't run into walls\n or your own tail!", customLabel);
        playLabel.setPosition(
            game.V_WIDTH/3,
            game.V_HEIGHT/1.65f
        );
        playLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(playLabel);

        //endregion

        //region Built with
        bubble2 = new Texture("randoms\\bubble2.png");
        TextureRegionDrawable bubble2logo = new TextureRegionDrawable(new TextureRegion(bubble2));
        bubble2Logo = new Image(bubble2logo);
        bubble2Logo.setPosition(
            theBackground.getX() + 50,
            theBackground.getY() + 80
        );
        game.imageAnimation(bubble2Logo);
        stage.addActor(bubble2Logo);

        builtLabel = new Label("LibGDX, " + "Java,\n" + "Pixel Art Graphics\n" +
            "Platform: Android,\n   Desktop", customLabel);
        builtLabel.setPosition(
            theBackground.getX() + 80,
            theBackground.getY() + 170
        );
        builtLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(builtLabel);

        gamee = new Texture("logos\\gameicon.png");
        TextureRegionDrawable gamelogo = new TextureRegionDrawable(new TextureRegion(gamee));
        gameLogo = new Image(gamelogo);
        gameLogo.setPosition(
            theBackground.getX() + 330,
            theBackground.getY() + 260
        );
        game.imageAnimation(gameLogo);
        stage.addActor(gameLogo);
        //endregion

        //region Developer
        bubble1 = new Texture("randoms\\bubble1.png");
        TextureRegionDrawable bubble1logo = new TextureRegionDrawable(new TextureRegion(bubble1));
        bubble1Logo = new Image(bubble1logo);
        bubble1Logo.setPosition(
            theBackground.getX() + 60,
            theBackground.getY() + 360
        );
        game.imageAnimation(bubble1Logo);
        stage.addActor(bubble1Logo);

        developerLabel = new Label("Team: Snakit Girls.\nAnh Thu, Ngoc Han and\n" +
            " the Sis.\n Github link: blabla.\nContact: abc@gmail.com", customLabel);
        developerLabel.setPosition(
            theBackground.getX() + 120,
            theBackground.getY() + 470
        );
        developerLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(developerLabel);

        team = new Texture("logos\\teamicon.png");
        TextureRegionDrawable teamlogo = new TextureRegionDrawable(new TextureRegion(team));
        teamLogo = new Image(teamlogo);
        teamLogo.setPosition(
            theBackground.getX() + 460,
            theBackground.getY() + 500
        );
        game.imageAnimation(teamLogo);
        stage.addActor(teamLogo);
        //endregion

        //region Thanks
//        thanksLabel = new Label("Thanks to the open-source community and all who supported development.", customLabel);
//        thanksLabel.setPosition(
//            theBackground.getX() + 100,
//            theBackground.getY() + 200
//        );
//        stage.addActor(thanksLabel);
        //endregion

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
        stage.getViewport().update(i, i1, true);
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
        bubble.dispose();
        bubble1.dispose();
        bubble2.dispose();
        play.dispose();
        team.dispose();
        gamee.dispose();
        about.dispose();
        stage.dispose();
    }
}
