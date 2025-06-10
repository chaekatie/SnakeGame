package com.snakegame.vertical;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.time.format.DateTimeFormatter;
import java.util.Random;

//java -jar gdx-liftoff-1.13.1.3.jar
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SnakeGame extends Game {
    public SpriteBatch batch;
    public Viewport viewport;
    public static final int V_WIDTH = 720;
    public static final int V_HEIGHT = 1280;

    public Music backgroundMusic;
    public Sound snakeHiss;
    public Sound clicking;
    public Sound normalFoodSound;
    public Sound specialFoodSound;
    public Sound goldenFoodSound;

    public Texture backButton;
    public Texture nextButton;

    public BitmapFont theBigFont;
    public BitmapFont theSmallFont;
    public Slider.SliderStyle customSliderStyle;
    public DateTimeFormatter formatter;

    private String username;
    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return this.username; }

    private boolean isLoggedIn = false;
    public boolean getLoggedIn(){ return this.isLoggedIn; }
    public void setLoggedIn(boolean isLoggedIn) { this.isLoggedIn = isLoggedIn; }

    // Store current volume levels
    private float sfxVolume;
    private float musicVolume;
    public long hissLoopId = -1;

    public Texture[] avatarsTexture;
    private Texture chosenAvatar;
    public void setChosenAvatar(Texture chosenAvatar) { this.chosenAvatar = chosenAvatar; }
    public Texture getChosenAvatar() { return this.chosenAvatar; }

    // region Add difficulty-related fields
    public enum Difficulty {
        EASY(0.2f),
        MEDIUM(0.15f),
        HARD(0.1f);

        public final float updateInterval;

        Difficulty(float updateInterval) {
            this.updateInterval = updateInterval;
        }
    }

    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
    public void setCurrentDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }
    //endregion

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT);

        // Load preferences first
        Preferences prefs = Gdx.app.getPreferences("MySnakeGamePreferences");
        sfxVolume = prefs.getFloat("sfxVolume", 2f);
        musicVolume = prefs.getFloat("musicVolume", 0.2f);

        // Initialize music with loaded volume
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds\\bgmusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(musicVolume);
        backgroundMusic.play();

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        avatarsTexture = new Texture[]{
            new Texture("avatars\\ava1.png"),
            new Texture("avatars\\ava2.png"),
            new Texture("avatars\\ava3.png"),
            new Texture("avatars\\ava4.png"),
            new Texture("avatars\\ava5.png"),
            new Texture("avatars\\ava6.png"),
            new Texture("avatars\\ava7.png"),
            new Texture("avatars\\ava8.png"),
            new Texture("avatars\\ava9.png"),
            new Texture("avatars\\ava10.png"),
            new Texture("avatars\\ava11.png"),
            new Texture("avatars\\ava12.png")
        };
        chosenAvatar = avatarsTexture[MathUtils.random(avatarsTexture.length - 1)];
        setChosenAvatar(chosenAvatar);

        //Load sounds
        snakeHiss = Gdx.audio.newSound(Gdx.files.internal("sounds\\hissing.mp3"));
        hissLoopId = snakeHiss.loop(sfxVolume);  // Start with current SFX volume
        clicking = Gdx.audio.newSound(Gdx.files.internal("sounds\\clicking.mp3"));
        clicking.play(sfxVolume);

        // Load food eating sounds
        normalFoodSound = Gdx.audio.newSound(Gdx.files.internal("sounds\\normal_food.mp3"));
        specialFoodSound = Gdx.audio.newSound(Gdx.files.internal("sounds\\special_food.mp3"));
        goldenFoodSound = Gdx.audio.newSound(Gdx.files.internal("sounds\\golden_food.mp3"));

        //Loads buttons
        backButton = new Texture("buttons\\back.png");
        nextButton = new Texture("buttons\\next.png");

        //Loads custom font
        try {
            System.out.println("Before creating font...");
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("chilanka.ttf"));
            System.out.println("Font loaded!");
            System.out.println(Gdx.files.internal("chilanka.ttf").exists());
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = (int) (V_HEIGHT * 0.035f);
            parameter.borderWidth = 3f;
            parameter.color = Color.GOLDENROD;
            theBigFont = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            e.printStackTrace(); // Logs error to console
            theBigFont = new BitmapFont(); // fallback font
        }

        try {
            System.out.println("Before creating font...");
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("chilanka.ttf"));
            System.out.println("Font loaded!");
            System.out.println(Gdx.files.internal("chilanka.ttf").exists());
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = (int)(V_WIDTH * 0.04f);
            parameter.borderWidth = 1f;
            parameter.color = Color.BLACK;
            theSmallFont = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            e.printStackTrace(); // Logs error to console
            theSmallFont = new BitmapFont(); // fallback font
        }

        // Load your custom textures
        Texture sliderBgTexture = new Texture(Gdx.files.internal("randoms\\sliderbg.png"));
        Texture sliderKnobTexture = new Texture(Gdx.files.internal("randoms\\sliderknob.png"));

        NinePatch sliderBgPatch = new NinePatch(sliderBgTexture, 10, 10, 10, 10);
        NinePatchDrawable sliderBgDrawable = new NinePatchDrawable(sliderBgPatch);
        TextureRegionDrawable sliderKnobDrawable = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));

        customSliderStyle = new Slider.SliderStyle();
        customSliderStyle.background = sliderBgDrawable;
        customSliderStyle.knob = sliderKnobDrawable;

        setScreen(new FirstScreen(this));
    }

    public void styleCheckbox(CheckBox checkbox){
        checkbox.getImage().setScaling(Scaling.fill);
        checkbox.getImageCell().size(40, 40);
        checkbox.getLabel().setFontScale(1.5f);
    }

    public void imageAnimation (Image randomImage)
    {
        randomImage.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
    }

    public void buttonAnimation (ImageButton randomButton)
    {
        randomButton.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
    }

    public void appearTransition(Image bg) {
        bg.setSize(this.V_WIDTH, this.V_HEIGHT);
        bg.setPosition(0, 0);
        bg.setColor(1, 1, 1, 0); // transparent
        bg.setScale(1.2f);
        bg.setOrigin(Align.center);

        bg.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(0.5f),
                Actions.scaleTo(1f, 1f, 0.5f)
            ),
            Actions.forever(
                Actions.sequence(
//                    Actions.color(Color.valueOf("a8d8ff"), 3f),
//                    Actions.color(Color.WHITE, 3f),
                    Actions.scaleTo(1.02f, 1.02f, 2f),
                    Actions.scaleTo(1.0f, 1.0f, 2f)
                )
            )
        ));
    }

    public ImageButton activateBackButton(Image background){
        System.out.println("backButton is " + (backButton == null ? "null" : "loaded"));
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backButton));
        ImageButton backBtn = new ImageButton(backDrawable);
        backBtn.setPosition(
            background.getX() + 20,
            background.getY()
        );
        return backBtn;
    }

    public ImageButton activateNextButton(Image background){
        TextureRegionDrawable nextDrawable = new TextureRegionDrawable(new TextureRegion(nextButton));
        ImageButton nextBtn = new ImageButton(nextDrawable);
        nextBtn.setPosition(
            background.getX() + 580,
            background.getY()
        );
        nextBtn.addAction(Actions.forever(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.4f),
                Actions.moveBy(0, -10, 0.4f)
            )
        ));
        return nextBtn;
    }

    // Method to play any SFX with current volume
    public void playSound(Sound sound) {
        if (sound != null) sound.play(sfxVolume);
    }

    // Method to update music volume
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (backgroundMusic != null) backgroundMusic.setVolume(volume);
        
        Preferences prefs = Gdx.app.getPreferences("MySnakeGamePreferences");
        prefs.putFloat("musicVolume", volume);
        prefs.flush();
    }

    // Method to update sfx volume
    public void setSfxVolume(float volume) {
        sfxVolume = volume;
        if (snakeHiss != null && hissLoopId != -1) {
            snakeHiss.setVolume(hissLoopId, volume);
        }
        
        Preferences prefs = Gdx.app.getPreferences("MySnakeGamePreferences");
        prefs.putFloat("sfxVolume", volume);
        prefs.flush();
    }

    // Getter for sfx volume
    public float getSfxVolume() {
        return sfxVolume;
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
    public void render() {
        super.render();
    }
    public void dispose(){
        batch.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (snakeHiss != null) {
            snakeHiss.stop();
            snakeHiss.dispose();
            hissLoopId = -1;
        }
    }
}
