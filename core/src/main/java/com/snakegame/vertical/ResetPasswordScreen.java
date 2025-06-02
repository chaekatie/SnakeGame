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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ResetPasswordScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Viewport viewport;
    private Texture background, board, sendBtn, textbox;
    private Image backgroundImage, boardImage, textBoxImage;
    private ImageButton backButton, sendButton, sendButton2;
    private Skin skin;
    private Dialog resetPassDialog, sendEmailDialog;
    private boolean sendEmailOK, resetPassOK;
    private TextField emailField, confirmField, passwordField;

    public ResetPasswordScreen (SnakeGame game, boolean sendEmailOK){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.sendEmailOK = sendEmailOK;
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
        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theBigFont;
        customLabel.fontColor = Color.WHITE;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = game.theBigFont;
        textFieldStyle.fontColor = Color.valueOf("D2691E");
        textFieldStyle.background = null;
        textFieldStyle.cursor = skin.getDrawable("cursor");

        Texture dialogTex = new Texture("backgrounds\\table.png");
        TextureRegionDrawable dialog = new TextureRegionDrawable(new TextureRegion(dialogTex));

        //region Background
        background = new Texture("backgrounds\\background.jpg");
        backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        game.appearTransition(backgroundImage);
        stage.addActor(backgroundImage);
        //endregion

        //region Board
        board = new Texture("backgrounds\\emptybg_square.png");
        boardImage = new Image(new TextureRegionDrawable(new TextureRegion(board)));
        game.imageAnimation(boardImage);
        boardImage.setPosition(
            backgroundImage.getX() + 80,
            backgroundImage.getY() + 450
        );
        stage.addActor(boardImage);
        //endregion

        //region Textbox Image
        textbox = new Texture("logos\\empty_tb.png");
        textBoxImage = new Image(new TextureRegionDrawable(new TextureRegion(textbox)));
        textBoxImage.setHeight(150);

        Image textBoxImage2 = new Image(new TextureRegionDrawable(new TextureRegion(textbox)));
        textBoxImage2.setHeight(150);

        if(!sendEmailOK){
            textBoxImage.setPosition(
                backgroundImage.getX() + 100,
                backgroundImage.getY() + 650
            );
        } else {
            textBoxImage.setPosition(
                backgroundImage.getX() + 100,
                backgroundImage.getY() + 740
            );

            textBoxImage2.setPosition(
                backgroundImage.getX() + 100,
                backgroundImage.getY() + 560
            );
            game.imageAnimation(textBoxImage2);
            stage.addActor(textBoxImage2);
        }
        game.imageAnimation(textBoxImage);
        stage.addActor(textBoxImage);
        //endregion

        //region Content
        // lúc nhập email để gửi
        sendBtn = new Texture("buttons\\send.png");
        sendButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(sendBtn)));
        sendButton2 = new ImageButton(new TextureRegionDrawable(new TextureRegion(sendBtn)));

        if(!sendEmailOK){
            Label theEmail = new Label("Type your email: ", customLabel);
            theEmail.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 830
            );
            stage.addActor(theEmail);

            emailField = new TextField("", textFieldStyle);
            emailField.setSize(440, 100);
            emailField.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 680
            );
            stage.addActor(emailField);

            sendButton.setPosition(
                backgroundImage.getX() + 400,
                backgroundImage.getY() + 480
            );
            stage.addActor(sendButton);
        } else {
            // lúc reset lại mk mới
            Label theToken = new Label("New password: ", customLabel);
            theToken.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 880
            );
            stage.addActor(theToken);

            Label thePassword = new Label("Confirm password: ", customLabel);
            thePassword.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 700
            );
            stage.addActor(thePassword);

            sendButton2.setPosition(
                backgroundImage.getX() + 420,
                backgroundImage.getY() + 420
            );
            stage.addActor(sendButton2);

            passwordField = new TextField("", textFieldStyle);
            passwordField.setSize(420, 90);
            passwordField.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 770
            );
            stage.addActor(passwordField);

            confirmField = new TextField("", textFieldStyle);
            confirmField.setSize(420, 90);
            confirmField.setPosition(
                backgroundImage.getX() + 130,
                backgroundImage.getY() + 590
            );
            stage.addActor(confirmField);
        }
        //endregion

        //region Send Email Dialog
        sendEmailDialog = new Dialog("", skin);
        sendEmailDialog.getContentTable().setBackground(dialog);
        Table content = sendEmailDialog.getContentTable();

        customLabel.font = game.theSmallFont;
        Label message = new Label("", customLabel);
        Label message2 = new Label("Please try again!", customLabel);

        dialogTextAnimation(message, false);
        dialogTextAnimation(message2, false);

        content.add(message).center().row();
        content.add(message2).center().row();
        //endregion

        //region Reset Password Dialog
        resetPassDialog = new Dialog("", skin);
        resetPassDialog.getContentTable().setBackground(dialog);
        Table content1 = resetPassDialog.getContentTable();

        Label message3 = new Label("", customLabel);
        Label message4 = new Label("", customLabel);

        dialogTextAnimation(message3, false);
        dialogTextAnimation(message4, false);

        content1.add(message3).center().row();
        content1.add(message4).center().row();
        //endregion

        sendButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Send button clicked!"); // ✅ Step 1: check this logs
                game.clicking.play(2f);

                String email = emailField.getText();
                System.out.println("Email: " + email);
                if(email.isEmpty()){
                    sendEmailOK = false;
                    Gdx.app.postRunnable(()-> {
                        message.setText("Please fill in your email!");
                        dialogTextAnimation(message, true);
                        dialogTextAnimation(message2, true);
                        sendEmailDialog.show(stage);
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                sendEmailDialog.hide();
                            }
                        }, 1f);
                    });
                    return;
                }

                GameApi.sendEmail(email, new GameApi.SendEmailCallback() {
                    @Override
                    public void onSuccess(String response) {
                        sendEmailOK = true;
                        sendEmailDialog.show(stage);
                        System.out.println(response);

                        Gdx.app.postRunnable(() -> {
                            message.setText("Please check your mailbox!");
                            message2.setText("Link has been sent.");
                            dialogTextAnimation(message, false);
                            dialogTextAnimation(message2, false);
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    sendEmailDialog.hide();
                                    game.setScreen(new ResetPasswordScreen(game, true));
                                }
                            }, 1f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("ERROR: " + error);
                        sendEmailOK = false;

                        Gdx.app.postRunnable(() -> {
                            message.setText(error);
                            dialogTextAnimation(message, false);
                            dialogTextAnimation(message2, false);
                            sendEmailDialog.show(stage);
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    sendEmailDialog.hide();
                                }
                            }, 1f);
                        });
                    }
                });
            }
        });

        sendButton2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Send button of reset dialog clicked!"); // ✅ Step 1: check this logs
                game.clicking.play(2f);

                String password = passwordField.getText();
                String confirm = confirmField.getText();
                System.out.println("Password: " + password);
                boolean empty = false, different = false;
                if(password.isEmpty() || confirm.isEmpty()) {
                    empty = true;
                    message3.setText("Fill the required values.");
                }
                if(!confirm.equals(password)) {
                    different = true;
                    message3.setText("Confirm password must match the new one.");
                }

                if(empty || different){
                    sendEmailOK = false;
                    Gdx.app.postRunnable(()-> {
                        message4.setText("Please try again.");
                        dialogTextAnimation(message3, true);
                        dialogTextAnimation(message4, true);
                        resetPassDialog.show(stage);
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                resetPassDialog.hide();
                            }
                        }, 1f);
                    });
                    return;
                }

//                GameApi.sendEmail(password, new GameApi.resetPassword(token, password, new GameApi.SendEmailCallback() {
//                    @Override
//                    public void onSuccess(String response) {
//                        sendEmailOK = true;
//                        sendEmailDialog.show(stage);
//                        System.out.println(response);
//
//                        Gdx.app.postRunnable(() -> {
//                            message.setText("Please check your mailbox!");
//                            message2.setText("Link has been sent.");
//                            dialogTextAnimation(message, false);
//                            dialogTextAnimation(message2, false);
//                            Timer.schedule(new Timer.Task() {
//                                @Override
//                                public void run() {
//                                    sendEmailDialog.hide();
//                                    game.setScreen(new ResetPasswordScreen(game, true));
//                                }
//                            }, 1f);
//                        });
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        System.out.println("ERROR: " + error);
//                        sendEmailOK = false;
//
//                        Gdx.app.postRunnable(() -> {
//                            message.setText(error);
//                            dialogTextAnimation(message, false);
//                            dialogTextAnimation(message2, false);
//                            sendEmailDialog.show(stage);
//                            Timer.schedule(new Timer.Task() {
//                                @Override
//                                public void run() {
//                                    sendEmailDialog.hide();
//                                }
//                            }, 1f);
//                        });
//                    }
//                });
            }
        });

        backButton = game.activateBackButton(backgroundImage);
        game.buttonAnimation(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.clicking.play(2f);
                game.setScreen(new SignInScreen(game));
            }
        });
        stage.addActor(backButton);

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
