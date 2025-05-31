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

public class SignUpScreen implements Screen {
    private SnakeGame game;
    private Viewport viewport;
    private Stage stage;
    private Texture mainBg, dialogTex;
    private Image baseBg;
    private boolean signupSuccessful;
    private Skin skin;
    private TextField username, password, email;
    private TextButton signupButton;
    private ImageButton backBtn;
    private Dialog successDialog, warningDialog;

    public SignUpScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        BitmapFont font = game.theBigFont;
        signupSuccessful = false;

        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;
        customLabel.fontColor = Color.BLACK;

        dialogTex = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialog = new TextureRegionDrawable(new TextureRegion(dialogTex));

        //region Success Dialog
        successDialog = new Dialog("", skin);
        successDialog.getContentTable().setBackground(dialog);
        Table content = successDialog.getContentTable();
        Label successMessage = new Label("Sign Up Successful!",customLabel);
        Label moveMessage = new Label("You will move back to Sign in.", customLabel);
        dialogTextAnimation(successMessage, false);
        dialogTextAnimation(moveMessage, false);
        content.add(successMessage).center().row();
        content.add(moveMessage).center();
        //endregion

        //region Warning Dialog
        warningDialog = new Dialog("", skin);
        warningDialog.getContentTable().setBackground(dialog);
        Table content2 = warningDialog.getContentTable();
        Label warningMessage = new Label("",customLabel);
        Label warningMessage2 = new Label("Please try again!", customLabel);
        dialogTextAnimation(warningMessage, false);
        dialogTextAnimation(warningMessage2, false);
        content2.add(warningMessage).center().row();
        content2.add(warningMessage2).center();
        //endregion

        //region Background
        mainBg = new Texture("backgrounds\\signupbg.png");
        baseBg = new Image(new TextureRegionDrawable(new TextureRegion(mainBg)));
        game.appearTransition(baseBg);
        stage.addActor(baseBg);
        //endregion

        //region Textboxes
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

        username = new TextField("", textFieldStyle);
        password = new TextField("", textFieldStyle);
        email = new TextField("", textFieldStyle);

        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

        //EMAIL
        email.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
        email.setPosition(
            baseBg.getX() + 180,
            baseBg.getY() + 825
        );
        email.addAction(Actions.sequence(
            Actions.moveBy(0, 10, 0.4f),
            Actions.moveBy(0, -10, 0.4f)
        ));

        //USERNAME
        username.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
        username.setPosition(
            baseBg.getX() + 180 ,
            baseBg.getY() + 635
        );
        username.addAction(
            Actions.sequence(
                Actions.scaleTo(1.02f, 1.02f, 2f),
                Actions.scaleTo(1.0f, 1.0f, 2f)
            )
        );

        //PASSWORD
        password.setSize(game.V_WIDTH/2.1f, game.V_HEIGHT/18);
        password.setPosition(
            baseBg.getX() + 180,
            baseBg.getY() + 445
        );
        password.addAction(
            Actions.sequence(
                Actions.scaleTo(1.02f, 1.02f, 2f),
                Actions.scaleTo(1.0f, 1.0f, 2f)
            )
        );

        stage.addActor(username);
        stage.addActor(password);
        stage.addActor(email);
        //endregion

        //region Sign Up Button
        signupButton = new TextButton("SIGNUP", buttonStyle);
        signupButton.setSize(game.V_WIDTH/3, game.V_HEIGHT/12);
        signupButton.setPosition(
            baseBg.getX() + 250,
            baseBg.getY() + 300
        );
        signupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Sign up button clicked!");
                game.clicking.play(2f);

                String user = username.getText();
                String pass = password.getText();
                String Email = email.getText();
                System.out.println("Username: " + user + ", Password: " + pass + ", Email: " + Email);

                GameApi.register(user, pass, Email, new GameApi.LoginCallback() {
                    @Override
                    public void onSuccess(String token) {
                        signupSuccessful = true;
                        successDialog.show(stage);
                        System.out.println(token);
                        Gdx.app.postRunnable(() -> {
                            dialogTextAnimation(successMessage, false);
                            dialogTextAnimation(moveMessage, false);
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    successDialog.hide();
                                    game.setScreen(new SignInScreen(game));
                                }
                            }, 1f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println(error);
                        signupSuccessful = false;
                        Gdx.app.postRunnable(() -> {
                            warningMessage.setText(error);
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

        signupButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        );
        stage.addActor(signupButton);
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
    }

    public void dialogTextAnimation(Label text, boolean before){
        if(before){
            text.setFontScale(1.1f);
            text.setVisible(false);
        } else {
            text.setVisible(true);
            text.getColor().a = 0;
            text.addAction(Actions.fadeIn(0.5f));
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

    }
}
