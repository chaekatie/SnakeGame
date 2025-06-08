package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import javax.swing.JOptionPane;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture backgroundTexture, dialogTexture;
    private Texture logoTexture, logooTexture;
    private Texture loginTexture, anoymousTexture;
    private Image background;
    private Dialog warningDialog;
    private Dialog difficultyDialog;
    private Skin skin;
    private boolean isLoggedIn;

    public FirstScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        isLoggedIn = GameApi.getLoginFlag();

        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;
        customLabel.fontColor = Color.BLACK;

        // Create difficulty selection dialog
        //createDifficultyDialog();

        //region Background
        backgroundTexture = new Texture("backgrounds\\mainbg.png");
        background = new Image(backgroundTexture);
        game.appearTransition(background);
        stage.addActor(background);
        //endregion

        //region Buttons
        loginTexture = new Texture("buttons\\login.png");
        anoymousTexture = new Texture("buttons\\nologin.png");
        TextureRegionDrawable loginDrawable = new TextureRegionDrawable(new TextureRegion(loginTexture));
        TextureRegionDrawable anoymousDrawable = new TextureRegionDrawable(new TextureRegion(anoymousTexture));

        ImageButton loginButton = new ImageButton(loginDrawable);
        loginButton.setPosition(
            background.getX() + 280,
            background.getY() + 480
        );
        ImageButton anoymousButton = new ImageButton(anoymousDrawable);
        anoymousButton.setPosition(
            background.getX() + 280,
            background.getY() + 280
        );

        game.buttonAnimation(loginButton);
        game.buttonAnimation(anoymousButton);
        //endregion

        //region Logos
        logoTexture = new Texture("backgrounds\\logo.png");
        Image logo = new Image(logoTexture);
        logo.setPosition(
            background.getX() + 120,
            background.getY() + 600
        );
        logo.setOrigin(Align.center);
        game.imageAnimation(logo);

        logooTexture = new Texture("backgrounds\\logo1.png");
        Image logoo = new Image(logooTexture);
        logoo.setPosition(
            background.getX() + 380,
            background.getY() - 60
        );
        logoo.setOrigin(Align.center);
        game.imageAnimation(logoo);
        stage.addActor(logo);
        stage.addActor(logoo);

        //endregion

        //region Login Button
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                background.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.5f),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f),
                        Actions.run(() -> logo.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> logoo.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> anoymousButton.addAction(Actions.fadeOut(0.5f))),
                        Actions.run(() -> loginButton.addAction(Actions.moveBy(500, 0, 0.5f)))
                    ),
                    Actions.run(() -> {
                        game.setScreen(new SignInScreen(game));
                    })
                ));
            }
        });
        stage.addActor(loginButton);
        //endregion

        //region Warning Dialog
        dialogTexture = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialog = new TextureRegionDrawable(new TextureRegion(dialogTexture));

        warningDialog = new Dialog("", skin) {
            @Override
            protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    System.out.println("User chose to stay anonymously");
                    warningDialog.hide();
                    GameApi.clearAuthToken();
                    isLoggedIn = false;
                    background.addAction(Actions.sequence(
                        Actions.parallel(
                            Actions.fadeOut(0.5f),
                            Actions.scaleTo(1.2f, 1.2f, 0.5f),
                            Actions.run(() -> logo.addAction(Actions.fadeOut(0.5f))),
                            Actions.run(() -> logoo.addAction(Actions.fadeOut(0.5f))),
                            Actions.run(() -> loginButton.addAction(Actions.fadeOut(0.5f))),
                            Actions.run(() -> anoymousButton.addAction(Actions.moveBy(500, 0, 0.5f)))
                        ),
                        Actions.run(() -> {
                            game.setScreen(new MenuScreen(game, isLoggedIn));
                        })
                    ));
                } else {
                    System.out.println("User cancelled anonymous, back to options");
                }
            }
        };
        warningDialog.getContentTable().setBackground(dialog);

        Label message1 = new Label("Continue without saving data?", customLabel);
        Label message2 = new Label("You can sign up later to keep your progress.", customLabel);
        dialogTextAnimation(message1, false);
        dialogTextAnimation(message2, false);
        warningDialog.text(message1);
        warningDialog.getContentTable().row();
        warningDialog.text(message2);

        warningDialog.button("Yes", true);
        warningDialog.button("No", false);
        //endregion

        //region Anoymous Button
        anoymousButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                warningDialog.show(stage);
            }
        });
        stage.addActor(anoymousButton);
        //endregion
    }
/*
    private void createDifficultyDialog() {
        String[] options = {"EASY", "MEDIUM", "HARD"};
        int choice = JOptionPane.showOptionDialog(null,
            "Select Difficulty",
            "Difficulty Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]); // Default to MEDIUM

        if (choice != JOptionPane.CLOSED_OPTION) {
            game.setCurrentDifficulty(SnakeGame.Difficulty.valueOf(options[choice]));
        } else {
            // If user closes dialog, default to MEDIUM
            game.setCurrentDifficulty(SnakeGame.Difficulty.MEDIUM);
        }
    }
*/
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
// Show difficulty dialog when screen is shown
//            if (difficultyDialog != null) {
//                difficultyDialog.show(stage);
//        }
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        backgroundTexture.dispose();
        logooTexture.dispose();
        logoTexture.dispose();
        loginTexture.dispose();
        anoymousTexture.dispose();
    }
}
