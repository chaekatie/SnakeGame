package com.snakegame.vertical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SettingScreen implements Screen {
    private SnakeGame game;
    private Stage stage;
    private Skin skin;
    private Preferences preferences;
    private Viewport viewport;
    private Texture mainBg, setting, musicText, soundText, selectAvaTexture, avaTexture;
    private Image background, settingLogo, musicVolume, soundVolume, selectAva, avatarImage;
    private ImageButton backButton;
    private final Slider musicSlider, sfxSlider;
    private Texture[] avatarsTexture;
    private Label selected;
    private boolean isLoggedin;

    public SettingScreen(SnakeGame game){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        this.isLoggedin = game.getLoggedIn();
        this.avatarsTexture = game.avatarsTexture;

        mainBg = new Texture("backgrounds\\emptybg.png");
        background = new Image(mainBg);
        game.appearTransition(background);
        stage.addActor(background);

        Label.LabelStyle customLabel = new Label.LabelStyle();
        customLabel.font = game.theSmallFont;
        customLabel.fontColor = Color.BLACK;

        //region Avatar Logo
        selected = new Label("You chose this avatar!", customLabel);
        selected.setPosition(
            game.V_WIDTH/12,
            game.V_HEIGHT/3f
        );
        selected.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(selected);

        selectAvaTexture = new Texture("logos\\chooseavatar.png");
        TextureRegionDrawable avaDrawble = new TextureRegionDrawable(new TextureRegion(selectAvaTexture));
        selectAva = new Image(avaDrawble);
        game.imageAnimation(selectAva);
        selectAva.setPosition(
            game.V_WIDTH/12,
            game.V_HEIGHT/3
        );
        stage.addActor(selectAva);
        //endregion

        //region Avatar Selection
        Table avatarTable = new Table();
        Array<Image> avatarImages = new Array<>();
        Array<Integer> clickCounts = new Array<>();
        Texture currentChosenAvatar = game.getChosenAvatar();

        for (int i = 0; i < avatarsTexture.length; i++) {
            final int index = i;
            clickCounts.add(0);
            Texture chosenAva = avatarsTexture[i];
            final Image avatarImage = new Image(new TextureRegionDrawable(new TextureRegion(chosenAva)));
            avatarImage.setSize(300, 300);
            // Set initial scale based on whether this is the currently chosen avatar
            if (chosenAva == currentChosenAvatar) {
                avatarImage.setScale(1.2f);
            }
            avatarImages.add(avatarImage);

            avatarImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // If clicking the currently selected avatar, deselect it
                    if (chosenAva == game.getChosenAvatar()) {
                        game.setChosenAvatar(null);
                        avatarImage.setScale(1.0f);
                        return;
                    }

                    // If there was a previously selected avatar, reset its scale
                    if (game.getChosenAvatar() != null) {
                        for (int j = 0; j < avatarsTexture.length; j++) {
                            if (avatarsTexture[j] == game.getChosenAvatar()) {
                                avatarImages.get(j).setScale(1.0f);
                                break;
                            }
                        }
                    }

                    // Select the new avatar
                    game.setChosenAvatar(chosenAva);
                    avatarImage.setScale(1.2f);
                    System.out.println("AVATAR CHOSEN: " + chosenAva);
                }
            });

            avatarTable.add(avatarImage).pad(20);
        }
        avatarTable.pack();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(); // optional
        ScrollPane scrollPane = new ScrollPane(avatarTable, scrollStyle);
        scrollPane.setScrollingDisabled(false, true); // enable X scroll only
        scrollPane.setFlingTime(0.5f);
        scrollPane.setOverscroll(false, false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setSize(game.V_WIDTH/3 , game.V_HEIGHT/3);
        scrollPane.setPosition(game.V_WIDTH/50, game.V_HEIGHT/9f);

        Table borderWrapper = new Table();
        borderWrapper.setSize(game.V_WIDTH/2.5f, game.V_HEIGHT/5f); // slightly bigger than ScrollPane
        borderWrapper.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("avatars\\borderframe.png"))));
        borderWrapper.add(scrollPane).size(game.V_WIDTH/3, game.V_HEIGHT/3).center();
        borderWrapper.setPosition(game.V_WIDTH/2.2f, game.V_HEIGHT/3);
        borderWrapper.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        stage.addActor(borderWrapper);
        //endregion

        //region Logos
        setting = new Texture("logos\\settinglogo.png");
        settingLogo = new Image(setting);
        settingLogo.setPosition(
            background.getX() + 240,
            background.getY() + 800
        );
        settingLogo.setOrigin(Align.center);
        game.imageAnimation(settingLogo);
        stage.addActor(settingLogo);

        musicText = new Texture("logos\\musicvolume.png");
        musicVolume = new Image(musicText);
        musicVolume.setPosition(
            background.getX() + 180,
            background.getY() + 750
        );
        musicVolume.setOrigin(Align.center);
        game.imageAnimation(musicVolume);
        stage.addActor(musicVolume);

        soundText = new Texture("logos\\soundvolume.png");
        soundVolume = new Image(soundText);
        soundVolume.setPosition(
            background.getX() + 350,
            background.getY() + 600
        );
        game.imageAnimation(soundVolume);
        stage.addActor(soundVolume);
        //endregion

        //region Buttons
        backButton = game.activateBackButton(background);
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

        //region Sliders
        musicSlider = new Slider(0f, 1f, 0.01f, false, game.customSliderStyle);
        musicSlider.setValue(Gdx.app.getPreferences("MySnakeGamePreferences").getFloat("musicVolume", 0.2f));
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = musicSlider.getValue();
                game.setMusicVolume(value);
                // Save the music volume preference
                Gdx.app.getPreferences("MySnakeGamePreferences").putFloat("musicVolume", value).flush();
            }
        });
        musicSlider.setPosition(
            backButton.getX() + 400,
            background.getY() + 870
        );
        musicSlider.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.5f),
                Actions.moveBy(0, -10, 0.5f)
            )
        ));
        stage.addActor(musicSlider);

        sfxSlider = new Slider(0f, 1f, 0.01f, false, game.customSliderStyle);
        sfxSlider.setValue(Gdx.app.getPreferences("MySnakeGamePreferences").getFloat("sfxVolume", 2f));
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                float value = sfxSlider.getValue();
                game.setSfxVolume(value);
                // Save the SFX volume preference
                Gdx.app.getPreferences("MySnakeGamePreferences").putFloat("sfxVolume", value).flush();
                // Play click sound when adjusting volume
                game.clicking.play(value);
            }
        });
        sfxSlider.setPosition(
            backButton.getX() + 150,
            background.getY() + 730
        );
        sfxSlider.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.5f),
                Actions.moveBy(0, -10, 0.5f)
            )
        ));
        stage.addActor(sfxSlider);
        //endregion

//        // ----- Language Selection (Dropdown) -----
//        Label languageLabel = new Label("Language", skin);
//        final SelectBox<String> languageSelect = new SelectBox<String>(skin);
//        // Define a list of languages.
//        String[] languages = {"English", "Spanish", "French", "German"};
//        languageSelect.setItems(languages);
//        // Set the initially selected value from preferences (default to English)
//        languageSelect.setSelected(preferences.getString("language", "English"));
//        languageSelect.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                String lang = languageSelect.getSelected();
//                preferences.putString("language", lang);
//                preferences.flush();
//                // Optionally update locale-dependent code here
//            }
//        });
//        stage.addActor(languageLabel);
//        stage.addActor(languageSelect);

        /*
        // ----- Reset Data Button -----
        TextButton resetButton = new TextButton("Reset Data", skin);
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.clear();
                prefs.flush();
                // Optionally, reset sliders to default values
                musicSlider.setValue(0.5f);
                sfxSlider.setValue(0.5f);
                sensitivitySlider.setValue(0.5f);
                languageSelect.setSelected("English");
            }
        });
         */
    }

//    public Image getAvatarImage(){
//        return this.theAvatar;
//    }
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
        mainBg.dispose();
        stage.dispose();
    }
}
