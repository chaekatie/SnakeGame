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

import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.Align; // Added import for Align

import com.badlogic.gdx.utils.Timer;

import com.badlogic.gdx.utils.viewport.FitViewport;

import com.badlogic.gdx.utils.viewport.Viewport;



public class ResetPasswordScreen implements Screen {

    private SnakeGame game;

    private Stage stage;

    private Viewport viewport;

    private Texture background, board, sendBtn, textbox;

    private Image backgroundImage, boardImage; // textBoxImage sẽ được tạo lại hoặc xử lý lại

    private ImageButton backButton, sendButton, sendButton2, sendButton3;

    private Skin skin;

    private Dialog checkEmail_OTPDialog; // Đổi tên để rõ ràng hơn

    private Dialog resetPassDialog; // Dialog cho reset password

// Biến trạng thái để điều khiển UI

    private boolean sendEmailSuccess = false; // Đã gửi email OTP thành công

    private boolean verifyOtpSuccess = false; // Đã xác thực OTP thành công



    private TextField emailField, confirmField, passwordField, otpField;



// Biến để lưu email của người dùng xuyên suốt quá trình

    private String userEmail = "";



// Label cho dialog

    private Label messageLabel, message2Label; // Labels for checkEmail_OTPDialog

    private Label message3Label, message4Label; // Labels for resetPassDialog



    public ResetPasswordScreen(SnakeGame game) { // Constructor được sửa đổi

        this.game = game;

        OrthographicCamera camera = new OrthographicCamera();

        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);

        stage = new Stage(viewport, game.batch);

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));



// Khởi tạo các label cho dialog

        Label.LabelStyle customLabelStyle = new Label.LabelStyle();

        customLabelStyle.font = game.theSmallFont;

        customLabelStyle.fontColor = Color.WHITE;



        messageLabel = new Label("", customLabelStyle);

        message2Label = new Label("Please try again!", customLabelStyle);

        dialogTextAnimation(messageLabel, true);

        dialogTextAnimation(message2Label, true);



        message3Label = new Label("", customLabelStyle);

        message4Label = new Label("", customLabelStyle);

        dialogTextAnimation(message3Label, true);

        dialogTextAnimation(message4Label, true);

    }



// Bạn có thể dùng constructor này để truyền trạng thái từ màn hình khác

    public ResetPasswordScreen(SnakeGame game, boolean sendEmailSuccess, boolean verifyOtpSuccess, String userEmail) {

        this(game); // Gọi constructor mặc định

        this.sendEmailSuccess = sendEmailSuccess;

        this.verifyOtpSuccess = verifyOtpSuccess;

        this.userEmail = userEmail;

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

        TextureRegionDrawable dialogDrawable = new TextureRegionDrawable(new TextureRegion(dialogTex));



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



//region Textbox Images

        textbox = new Texture("logos\\empty_tb.png");

        Image textBoxImage = new Image(new TextureRegionDrawable(new TextureRegion(textbox)));

        textBoxImage.setHeight(150);

        game.imageAnimation(textBoxImage);

        stage.addActor(textBoxImage); // Add once and adjust position



        Image textBoxImage2 = null; // Only needed for password reset screen

        if (verifyOtpSuccess) { // Chỉ tạo textbox thứ hai khi ở màn hình đặt lại mật khẩu

            textBoxImage2 = new Image(new TextureRegionDrawable(new TextureRegion(textbox)));

            textBoxImage2.setHeight(150);

            game.imageAnimation(textBoxImage2);

            stage.addActor(textBoxImage2);

        }

//endregion



//region Layout & Content based on state

        sendBtn = new Texture("buttons\\send.png");

        sendButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(sendBtn)));

        sendButton2 = new ImageButton(new TextureRegionDrawable(new TextureRegion(sendBtn)));

        sendButton3 = new ImageButton(new TextureRegionDrawable(new TextureRegion(sendBtn)));



        if (!sendEmailSuccess) { // Màn hình nhập Email

            textBoxImage.setPosition(

                backgroundImage.getX() + 100,

                backgroundImage.getY() + 650

            );



            Label theEmail = new Label("Type your email: ", customLabel);

            theEmail.setPosition(

                backgroundImage.getX() + 130,

                backgroundImage.getY() + 830

            );

            stage.addActor(theEmail);



            emailField = new TextField(userEmail, textFieldStyle); // Sử dụng userEmail nếu có sẵn

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



        } else if (!verifyOtpSuccess) { // Màn hình nhập OTP

            textBoxImage.setPosition(

                backgroundImage.getX() + 100,

                backgroundImage.getY() + 650

            );



            Label theOTP = new Label("Enter OTP code: ", customLabel);

            theOTP.setPosition(

                backgroundImage.getX() + 130,

                backgroundImage.getY() + 830

            );

            stage.addActor(theOTP);



            otpField = new TextField("", textFieldStyle);

            otpField.setSize(440, 100);

            otpField.setPosition(

                backgroundImage.getX() + 130,

                backgroundImage.getY() + 680

            );

            stage.addActor(otpField);



            sendButton3.setPosition(

                backgroundImage.getX() + 400,

                backgroundImage.getY() + 480

            );

            stage.addActor(sendButton3);



        } else { // Màn hình đặt lại mật khẩu mới

            textBoxImage.setPosition(

                backgroundImage.getX() + 100,

                backgroundImage.getY() + 740

            );

            if(textBoxImage2 != null) { // Đảm bảo textBoxImage2 đã được khởi tạo

                textBoxImage2.setPosition(

                    backgroundImage.getX() + 100,

                    backgroundImage.getY() + 560

                );

            }



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



//region Back Button (luôn hiển thị)

        backButton = game.activateBackButton(backgroundImage);

        game.buttonAnimation(backButton);

        backButton.addListener(new ClickListener() {

            @Override

            public void clicked(InputEvent event, float x, float y) {

                game.clicking.play(2f);

// Quay lại màn hình đăng nhập hoặc màn hình trước đó

                game.setScreen(new SignInScreen(game));

            }

        });

        stage.addActor(backButton);

//endregion



//region Dialogs Setup (Sử dụng các Label đã khởi tạo ở constructor)

        checkEmail_OTPDialog = new Dialog("", skin);

        checkEmail_OTPDialog.getContentTable().setBackground(dialogDrawable); // Sử dụng dialogDrawable

        checkEmail_OTPDialog.getContentTable().add(messageLabel).center().row();

        checkEmail_OTPDialog.getContentTable().add(message2Label).center().row();

// checkEmail_OTPDialog.getButtonTable().add(new TextButton("OK", skin)).pad(10); // Có thể thêm nút OK



        resetPassDialog = new Dialog("", skin);

        resetPassDialog.getContentTable().setBackground(dialogDrawable); // Sử dụng dialogDrawable

        resetPassDialog.getContentTable().add(message3Label).center().row();

        resetPassDialog.getContentTable().add(message4Label).center().row();

// resetPassDialog.getButtonTable().add(new TextButton("OK", skin)).pad(10); // Có thể thêm nút OK

//endregion



//region Listeners

// Listener cho nút gửi email

        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Send button (Email) clicked!");
                game.clicking.play(2f);

                userEmail = emailField.getText().trim();
                if (userEmail.isEmpty() || !userEmail.contains("@") || !userEmail.contains(".")) {
                    showDialog(checkEmail_OTPDialog, messageLabel, "Invalid email address!", message2Label, "Please try again.");
                    return;
                }
                GameApi.requestPasswordResetOtp(userEmail, new GameApi.SendEmailCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gdx.app.postRunnable(() -> {
                            System.out.println("Request OTP Success: " + response);

                            ResetPasswordScreen.this.sendEmailSuccess = true;
                            showDialog(checkEmail_OTPDialog, messageLabel, "Please check your mailbox!", message2Label, "OTP has been sent.");

                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    checkEmail_OTPDialog.hide();
                                    game.setScreen(new ResetPasswordScreen(game, ResetPasswordScreen.this.sendEmailSuccess, ResetPasswordScreen.this.verifyOtpSuccess, userEmail));
                                }
                            }, 1f);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Gdx.app.postRunnable(() -> {
                            System.err.println("Request OTP Error: " + error);
                            ResetPasswordScreen.this.sendEmailSuccess = false;
                            String errorMessage = error.contains("Error:") ? error.split("Error:")[1].trim() : "Failed to send OTP.";
                            showDialog(checkEmail_OTPDialog, messageLabel, errorMessage, message2Label, "Please try again.");
                        });
                    }
                });
            }
        });


// Listener cho nút gửi OTP

        sendButton3.addListener(new ClickListener() {

            @Override

            public void clicked(InputEvent event, float x, float y) {

                System.out.println("Send button (OTP) clicked!");

                game.clicking.play(2f);



                String otp = otpField.getText();

                if (otp.isEmpty()) {

                    showDialog(checkEmail_OTPDialog, messageLabel, "Please fill in the OTP code!", message2Label, "Please try again.");

                    return;

                }



                GameApi.verifyOtp(userEmail, otp, new GameApi.SendEmailCallback() {

                    @Override

                    public void onSuccess(String response) {

                        Gdx.app.postRunnable(() -> {

                            System.out.println("Verify OTP Success: " + response);

                            verifyOtpSuccess = true;

                            showDialog(checkEmail_OTPDialog, messageLabel, "OTP verified successfully!", message2Label, "Now set new password.");

                            Timer.schedule(new Timer.Task() {

                                @Override

                                public void run() {

                                    checkEmail_OTPDialog.hide();

                                    game.setScreen(new ResetPasswordScreen(game, sendEmailSuccess, verifyOtpSuccess, userEmail));

                                }

                            }, 1f);

                        });

                    }
                    @Override
                    public void onError(String error) {
                        Gdx.app.postRunnable(() -> {
                            System.err.println("Verify OTP Error: " + error);
                            verifyOtpSuccess = false;
                            String errorMessage = error.contains("Error:") ? error.split("Error:")[1].trim() : "Invalid OTP.";
                            showDialog(checkEmail_OTPDialog, messageLabel, errorMessage, message2Label, "Please try again.");
                        });
                    }
                });
            }
        });



// Listener cho nút gửi mật khẩu mới

        sendButton2.addListener(new ClickListener() {

            @Override

            public void clicked(InputEvent event, float x, float y) {

                System.out.println("Send button (reset password) clicked!");

                game.clicking.play(2f);



                String password = passwordField.getText();

                String confirm = confirmField.getText();



                if (password.isEmpty() || confirm.isEmpty()) {

                    showDialog(resetPassDialog, message3Label, "Fill the required values.", message4Label, "Please try again.");

                    return;

                }

                if (!confirm.equals(password)) {

                    showDialog(resetPassDialog, message3Label, "Confirm password must match the new one.", message4Label, "Please try again.");

                    return;

                }



                GameApi.resetPasswordWithOtp(userEmail, password, new GameApi.SendEmailCallback() {

                    @Override

                    public void onSuccess(String response) {

                        Gdx.app.postRunnable(() -> {

                            System.out.println("Reset Password Success: " + response);

                            resetPassDialog.show(stage);

                            message3Label.setText("Password changed successfully!");

                            message4Label.setText("You can now sign in.");

                            dialogTextAnimation(message3Label, false);

                            dialogTextAnimation(message4Label, false);

// Chuyển sang màn hình đăng nhập sau khi reset thành công

                            Timer.schedule(new Timer.Task() {

                                @Override

                                public void run() {

                                    resetPassDialog.hide();

                                    game.setScreen(new SignInScreen(game));

                                }

                            }, 1f);

                        });

                    }



                    @Override

                    public void onError(String error) {

                        Gdx.app.postRunnable(() -> {

                            System.err.println("Reset Password Error: " + error);

                            String errorMessage = error.contains("Error:") ? error.split("Error:")[1].trim() : "Failed to reset password.";

                            showDialog(resetPassDialog, message3Label, errorMessage, message4Label, "Please try again.");

                        });

                    }

                });

            }

        });

//endregion

    }



// Phương thức trợ giúp để hiển thị dialog

    private void showDialog(Dialog dialog, Label msg1, String text1, Label msg2, String text2) {

        msg1.setText(text1);

        msg2.setText(text2);

        dialogTextAnimation(msg1, false);

        dialogTextAnimation(msg2, false);

        dialog.show(stage);

        Timer.schedule(new Timer.Task() {

            @Override

            public void run() {

                dialog.hide();

            }

        }, 1f);

    }



    public void dialogTextAnimation(Label text, boolean before) {

        if (before) {

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

        stage.dispose();

        skin.dispose();

        background.dispose();

        board.dispose();

        sendBtn.dispose();

        textbox.dispose();

    }

}
