package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SignInScreen implements Screen {
    private SnakeGame game;
    private Viewport viewport;
    private Stage stage;
    private Texture mainBg, forgotPassLogo, signupTex, dialogTex;
    private Image baseBg;
    private boolean loginSuccessful;
    private Skin skin;
    private TextField username, password;
    private TextButton loginButton;
    private ImageButton backBtn, signupBtn, forgotPassBtn;
    private Dialog successDialog, warningDialog;

    public SignInScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        BitmapFont font = game.theBigFont;
        loginSuccessful = false;

//        loading = new Image(new Texture("snakeloading.png"));
//        loading.setOrigin(Align.center);
//        loading.addAction(Actions.forever(Actions.rotateBy(360f, 1.5f)));

        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;
        customLabel.fontColor = Color.BLACK;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = null;
        buttonStyle.down = null;
        buttonStyle.fontColor = Color.BLACK;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.valueOf("D2691E");
        textFieldStyle.background = null;
        textFieldStyle.cursor = skin.getDrawable("cursor");

        dialogTex = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialog = new TextureRegionDrawable(new TextureRegion(dialogTex));

        //region Success Dialog
        successDialog = new Dialog("", skin);
        successDialog.getContentTable().setBackground(dialog);
        Table content = successDialog.getContentTable();
        Label successMessage = new Label("Login Successful!",customLabel);
        Label moveMessage = new Label("You gonna move to menu screen.", customLabel);
        dialogTextAnimation(successMessage, true);
        dialogTextAnimation(moveMessage, true);
        content.add(successMessage).center().row();
        content.add(moveMessage).center();
        //endregion

        //region Warning Dialog
        warningDialog = new Dialog("", skin);
        warningDialog.getContentTable().setBackground(dialog);
        Table content2 = warningDialog.getContentTable();
        Label warningMessage = new Label("Wrong username/password!", customLabel);
        Label warningMessage2 = new Label("Please try again!", customLabel);
        dialogTextAnimation(warningMessage, true);
        dialogTextAnimation(warningMessage2, true);
        content2.add(warningMessage).center().row();
        content2.add(warningMessage2).center();
        //endregion

        //region Background
        mainBg = new Texture("backgrounds\\loginbg.jpg");
        baseBg = new Image(new TextureRegionDrawable(new TextureRegion(mainBg)));
        game.appearTransition(baseBg);
        stage.addActor(baseBg);
        //endregion

        //region Textboxes
        username = new TextField("", textFieldStyle);
        password = new TextField("", textFieldStyle);
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

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

        stage.addActor(username);
        stage.addActor(password);
        //endregion

        //region Login Button
        loginButton = new TextButton("", buttonStyle);
        loginButton.setSize(250, 90);
        loginButton.setPosition(
            baseBg.getX() + 240,
            baseBg.getY() + 380
        );

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Login button clicked!"); // ✅ Step 1: check this logs
                game.clicking.play(2f);

                String user = username.getText();
                String pass = password.getText();
                System.out.println("Username: " + user + ", Password: " + pass);

                GameApi.login(user, pass, new GameApi.LoginCallback() {
                    @Override
                    public void onSuccess(String token) {
                        loginSuccessful = true;
                        successDialog.show(stage);
                        System.out.println("Login success. Token: " + token);
                        Gdx.app.postRunnable(() -> {
                            dialogTextAnimation(successMessage, false);
                            dialogTextAnimation(moveMessage, false);
                            // Delay before going to menu
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    successDialog.hide();
                                    game.setScreen(new MenuScreen(game));
                                }
                            }, 1f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println(error);
                        loginSuccessful = false;
                        Gdx.app.postRunnable(() -> {
                            dialogTextAnimation(warningMessage, false);
                            dialogTextAnimation(warningMessage2, false);
                            successDialog.hide();
                            warningDialog.show(stage);
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    successDialog.hide();
                                    warningDialog.hide();
                                }
                            }, 1f);
                        });
                    }
                });
            }
        });

        loginButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        );
        stage.addActor(loginButton);
        //endregion

        //region Back Button
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
        //endregion

        //region Sign Up Button
        signupTex = new Texture("buttons\\signup.png");
        signupBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(signupTex)));
        signupBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new SignUpScreen(game));
            }
        });
        signupBtn.setPosition(
            baseBg.getX() + 460,
            baseBg.getY() + 40
        );
        stage.addActor(signupBtn);
        //endregion

        //region Forgot password Logo
        forgotPassLogo = new Texture("logos//forgotpass.png");
        TextureRegion region = new TextureRegion(forgotPassLogo);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        forgotPassBtn = new ImageButton(drawable);
        forgotPassBtn.setSize(region.getRegionWidth(), region.getRegionHeight());

        forgotPassBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new ResetPasswordScreen(game, false));
            }
        });
        forgotPassBtn.setPosition(
            baseBg.getX() + 450,
            baseBg.getY() + 180
        );
        stage.addActor(forgotPassBtn);
        //endregion
    }

    public void dialogTextAnimation(Label text, boolean before){
        if(before){
            text.setFontScale(1.1f);
            text.setVisible(false);
        } else {
            text.setVisible(true);
            text.getColor().a = 0;
            text.addAction(Actions.fadeIn(0.4f));
        }
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
        forgotPassLogo.dispose();
        signupTex.dispose();
    }
}
