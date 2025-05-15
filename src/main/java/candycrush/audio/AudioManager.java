package candycrush.audio;

import candycrush.util.ConfigManager;
import candycrush.util.ResourceLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Manages game audio including sound effects and background music.
 * Implements Singleton pattern for global access.
 */
public class AudioManager {

    private static final Logger LOGGER = Logger.getLogger(
        AudioManager.class.getName()
    );

    private static AudioManager instance;

    private final Map<String, Clip> soundEffects;
    private Clip backgroundMusic;

    private boolean soundEnabled;
    private boolean musicEnabled;
    private float soundVolume;
    private float musicVolume;

    private final ExecutorService audioThreadPool;

    public static final String SOUND_SELECT = "select.wav";
    public static final String SOUND_MATCH = "match.wav";
    public static final String SOUND_SPECIAL = "special_candy.wav";
    public static final String SOUND_LEVEL_COMPLETE = "level_complete.wav";
    public static final String SOUND_GAME_OVER = "game_over.wav";
    public static final String SOUND_BUTTON_CLICK = "button_click.wav";
    public static final String MUSIC_BACKGROUND = "background_music.wav";

    /**
     * Private constructor to enforce Singleton pattern
     */
    private AudioManager() {
        soundEffects = new HashMap<>();
        audioThreadPool = Executors.newFixedThreadPool(2);

        ConfigManager config = ConfigManager.getInstance();
        soundEnabled = config.getBooleanProperty("ui.sound.enabled", true);
        musicEnabled = config.getBooleanProperty("ui.music.enabled", true);
        soundVolume = (float) config.getDoubleProperty("ui.sound.volume", 0.8);
        musicVolume = (float) config.getDoubleProperty("ui.music.volume", 0.6);
    }

    /**
     * Returns the singleton instance
     *
     * @return AudioManager instance
     */
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Initialize the audio system
     */
    public void initialize() {
        LOGGER.info("Initializing audio system");
        try {
            preloadSoundEffects();

            loadBackgroundMusic();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize audio system", e);
        }
    }

    /**
     * Preload common sound effects
     */
    private void preloadSoundEffects() {
        try {
            ResourceLoader loader = ResourceLoader.getInstance();

            loadSound(SOUND_SELECT);
            loadSound(SOUND_MATCH);
            loadSound(SOUND_SPECIAL);
            loadSound(SOUND_LEVEL_COMPLETE);
            loadSound(SOUND_GAME_OVER);
            loadSound(SOUND_BUTTON_CLICK);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to preload sound effects", e);
        }
    }

    /**
     * Load background music
     */
    private void loadBackgroundMusic() {
        try {
            backgroundMusic = ResourceLoader.getInstance()
                .loadSound(MUSIC_BACKGROUND);
            if (backgroundMusic != null) {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                setMusicVolume(musicVolume);

                if (musicEnabled) {
                    startBackgroundMusic();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load background music", e);
        }
    }

    /**
     * Load a sound effect
     *
     * @param soundName Sound file name
     */
    private void loadSound(String soundName) {
        try {
            Clip clip = ResourceLoader.getInstance().loadSound(soundName);
            if (clip != null) {
                soundEffects.put(soundName, clip);
            }
        } catch (Exception e) {
            LOGGER.log(
                Level.WARNING,
                "Failed to load sound effect: " + soundName,
                e
            );
        }
    }

    /**
     * Play a sound effect
     *
     * @param soundName Sound file name
     */
    public void playSound(String soundName) {
        if (!soundEnabled) {
            return;
        }

        audioThreadPool.execute(() -> {
            try {
                Clip clip = getSound(soundName);
                if (clip != null) {
                    clip.setFramePosition(0);
                    setSoundVolume(clip, soundVolume);
                    clip.start();
                }
            } catch (Exception e) {
                LOGGER.log(
                    Level.WARNING,
                    "Failed to play sound: " + soundName,
                    e
                );
            }
        });
    }

    /**
     * Get a sound clip
     *
     * @param soundName Sound file name
     * @return Sound clip
     */
    private Clip getSound(String soundName) {
        if (soundEffects.containsKey(soundName)) {
            return soundEffects.get(soundName);
        } else {
            try {
                loadSound(soundName);
                return soundEffects.get(soundName);
            } catch (Exception e) {
                LOGGER.log(
                    Level.WARNING,
                    "Failed to get sound: " + soundName,
                    e
                );
                return null;
            }
        }
    }

    /**
     * Start background music
     */
    public void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null) {
            return;
        }

        audioThreadPool.execute(() -> {
            try {
                backgroundMusic.setFramePosition(0);
                setMusicVolume(musicVolume);
                backgroundMusic.start();
            } catch (Exception e) {
                LOGGER.log(
                    Level.WARNING,
                    "Failed to start background music",
                    e
                );
            }
        });
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (
            musicEnabled &&
            backgroundMusic != null &&
            !backgroundMusic.isRunning()
        ) {
            backgroundMusic.start();
        }
    }

    /**
     * Set sound effect volume
     *
     * @param clip Sound clip
     * @param volume Volume level (0.0 to 1.0)
     */
    private void setSoundVolume(Clip clip, float volume) {
        if (
            clip != null &&
            clip.isControlSupported(FloatControl.Type.MASTER_GAIN)
        ) {
            FloatControl gainControl = (FloatControl) clip.getControl(
                FloatControl.Type.MASTER_GAIN
            );
            float dB = (float) ((Math.log(Math.max(0.0001, volume)) /
                    Math.log(10.0)) *
                20.0);
            gainControl.setValue(
                Math.max(
                    gainControl.getMinimum(),
                    Math.min(gainControl.getMaximum(), dB)
                )
            );
        }
    }

    /**
     * Set music volume
     *
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (
            backgroundMusic != null &&
            backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)
        ) {
            FloatControl gainControl =
                (FloatControl) backgroundMusic.getControl(
                    FloatControl.Type.MASTER_GAIN
                );
            float dB = (float) ((Math.log(Math.max(0.0001, volume)) /
                    Math.log(10.0)) *
                20.0);
            gainControl.setValue(
                Math.max(
                    gainControl.getMinimum(),
                    Math.min(gainControl.getMaximum(), dB)
                )
            );
        }

        ConfigManager.getInstance()
            .setUserDoubleProperty("ui.music.volume", volume);
    }

    /**
     * Set sound effects volume
     *
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setSoundVolume(float volume) {
        soundVolume = volume;

        for (Clip clip : soundEffects.values()) {
            setSoundVolume(clip, volume);
        }

        ConfigManager.getInstance()
            .setUserDoubleProperty("ui.sound.volume", volume);
    }

    /**
     * Enable/disable sound effects
     *
     * @param enabled true to enable, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        ConfigManager.getInstance()
            .setUserBooleanProperty("ui.sound.enabled", enabled);
    }

    /**
     * Enable/disable background music
     *
     * @param enabled true to enable, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;

        if (enabled) {
            startBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }

        ConfigManager.getInstance()
            .setUserBooleanProperty("ui.music.enabled", enabled);
    }

    /**
     * Check if sound effects are enabled
     *
     * @return true if enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Check if background music is enabled
     *
     * @return true if enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Get sound volume
     *
     * @return Sound volume (0.0 to 1.0)
     */
    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Get music volume
     *
     * @return Music volume (0.0 to 1.0)
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Clean up resources
     */
    public void cleanup() {
        stopBackgroundMusic();

        for (Clip clip : soundEffects.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }

        soundEffects.clear();

        audioThreadPool.shutdown();
    }
}
