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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SignInUpScreen implements Screen {
    private SnakeGame game;
    private Viewport viewport;
    private Stage stage;
    private Texture mainBg;
    private Image baseBg;
    private boolean isLogin;
    private Skin skin;
    private TextField username, password, email;
    private TextButton loginButton, signupButton;
    private ImageButton backBtn, nextBtn;

    public SignInUpScreen(SnakeGame game, boolean isLogin){
        this.game = game;
        this.isLogin = isLogin;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        BitmapFont font = game.theBigFont;

//        loading = new Image(new Texture("snakeloading.png"));
//        loading.setOrigin(Align.center);
//        loading.addAction(Actions.forever(Actions.rotateBy(360f, 1.5f)));

        //region Background
        if(isLogin)
        {
            mainBg = new Texture("backgrounds\\loginbg.jpg");
        }
        else { mainBg = new Texture("backgrounds\\signupbg.png"); }
        baseBg = new Image(mainBg);
        game.appearTransition(baseBg);
        stage.addActor(baseBg);
        //endregion

        //region Textboxes
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = null;
        buttonStyle.down = null;
        buttonStyle.fontColor = Color.CLEAR;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.valueOf("D2691E");
        textFieldStyle.background = null;
        textFieldStyle.cursor = skin.getDrawable("cursor");

        username = new TextField("", textFieldStyle);
        password = new TextField("", textFieldStyle);
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

        if(!isLogin){
            email = new TextField("", textFieldStyle);
            email.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
            email.setPosition(
                baseBg.getX() + 180,
                baseBg.getY() + 825
            );
            email.addAction(Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            ));
            email.setStyle(textFieldStyle);
            stage.addActor(email);

            username.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
            username.setPosition(
                baseBg.getX() + 180 ,
                baseBg.getY() + 635
            );

            password.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
            password.setPosition(
                baseBg.getX() + 180,
                baseBg.getY() + 445
            );

            signupButton = new TextButton("", buttonStyle);
            signupButton.setSize(game.V_WIDTH/3, game.V_HEIGHT/12);
            signupButton.setPosition(
                baseBg.getX() + 250,
                baseBg.getY() + 300
            );
            signupButton.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.clicking.play(2f);
                }
            });
            signupButton.setStyle(buttonStyle);
            stage.addActor(signupButton);
        }

        if(isLogin){
            username.setSize(game.V_WIDTH/2.2f, game.V_HEIGHT/16);
            username.setPosition(
                baseBg.getX() + 190 ,
                baseBg.getY() + 750
            );

            password.setSize(game.V_WIDTH/2.2f, game.V_HEIGHT/16);
            password.setPosition(
                baseBg.getX() + 190,
                baseBg.getY() + 530
            );

            loginButton = new TextButton("", buttonStyle);
            loginButton.setSize(game.V_WIDTH/10, game.V_HEIGHT/14);
            loginButton.setPosition(
                baseBg.getX() + 240,
                baseBg.getY() + 380
            );
            loginButton.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.clicking.play(2f);
                }
            });
            loginButton.addAction(
                Actions.sequence(
                    Actions.moveBy(0, 10, 0.4f),
                    Actions.moveBy(0, -10, 0.4f)
                )
            );
            loginButton.setStyle(buttonStyle);
            stage.addActor(loginButton);
        }

        username.addAction(
            Actions.sequence(
                Actions.scaleTo(1.02f, 1.02f, 2f),
                Actions.scaleTo(1.0f, 1.0f, 2f)
            )
        );
        password.addAction(
            Actions.sequence(
                Actions.scaleTo(1.02f, 1.02f, 2f),
                Actions.scaleTo(1.0f, 1.0f, 2f)
            )
        );

        username.setStyle(textFieldStyle);
        stage.addActor(username);
        password.setStyle(textFieldStyle);
        stage.addActor(password);
        //endregion

        //region Buttons
        backBtn = game.activateBackButton(baseBg);
        game.buttonAnimation(backBtn);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new FirstScreen(game));
            }
        });
        stage.addActor(backBtn);

        nextBtn = game.activateNextButton(baseBg);
        game.buttonAnimation(nextBtn);
        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new MenuScreen(game));
            }
        });
        stage.addActor(nextBtn);
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
        stage.dispose();
        mainBg.dispose();
    }
}
