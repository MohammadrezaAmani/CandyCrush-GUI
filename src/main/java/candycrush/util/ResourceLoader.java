package candycrush.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

/**
 * Manages loading and caching of game resources (images, sounds, fonts).
 * Implements Singleton pattern for global access.
 */
public class ResourceLoader {

    private static final Logger LOGGER = Logger.getLogger(
        ResourceLoader.class.getName()
    );

    private static ResourceLoader instance;

    private final Map<String, BufferedImage> imageCache;
    private final Map<String, Clip> soundCache;
    private final Map<String, Font> fontCache;

    private String imagePath;
    private String soundPath;
    private String fontPath;

    /**
     * Private constructor to enforce Singleton pattern
     */
    private ResourceLoader() {
        imageCache = new HashMap<>();
        soundCache = new HashMap<>();
        fontCache = new HashMap<>();

        ConfigManager config = ConfigManager.getInstance();
        imagePath = config.getProperty(
            "path.images",
            "src/main/resources/images/"
        );
        soundPath = config.getProperty(
            "path.sounds",
            "src/main/resources/sounds/"
        );
        fontPath = config.getProperty(
            "path.fonts",
            "src/main/resources/fonts/"
        );
    }

    /**
     * Returns the singleton instance
     *
     * @return ResourceLoader instance
     */
    public static synchronized ResourceLoader getInstance() {
        if (instance == null) {
            instance = new ResourceLoader();
        }
        return instance;
    }

    /**
     * Preloads commonly used resources
     */
    public void preloadResources() {
        LOGGER.info("Preloading resources...");

        try {
            loadImage("background.png");
            loadImage("logo.png");

            String[] candyTypes = { "simple", "row", "column", "radial" };
            String[] candyColors = { "red", "blue", "green", "yellow" };

            for (String type : candyTypes) {
                for (String color : candyColors) {
                    loadImage("candy_" + type + "_" + color + ".png");
                }
            }

            loadImage("button_normal.png");
            loadImage("button_hover.png");
            loadImage("button_pressed.png");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error preloading images", e);
        }

        try {
            loadSound("select.wav");
            loadSound("match.wav");
            loadSound("special_candy.wav");
            loadSound("game_over.wav");
            loadSound("level_complete.wav");
            loadSound("background_music.wav");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error preloading sounds", e);
        }

        try {
            loadFont("candy_crush.ttf");
            loadFont("game_font.ttf");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error preloading fonts", e);
        }

        LOGGER.info("Resource preloading completed");
    }

    /**
     * Loads an image from the resources
     *
     * @param filename Image filename
     * @return BufferedImage object or null if loading failed
     * @throws IOException If image cannot be loaded
     */
    public BufferedImage loadImage(String filename) throws IOException {
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }

        BufferedImage image = null;
        try {
            File file = new File(imagePath + filename);
            image = ImageIO.read(file);
            imageCache.put(filename, image);
            LOGGER.fine("Loaded image: " + filename);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load image: " + filename, e);
            throw e;
        }

        return image;
    }

    /**
     * Gets an image icon of specified size
     *
     * @param filename Image filename
     * @param width Desired width
     * @param height Desired height
     * @return ImageIcon object or null if loading failed
     */
    public ImageIcon getImageIcon(String filename, int width, int height) {
        try {
            BufferedImage img = loadImage(filename);
            if (img != null) {
                Image scaledImage = img.getScaledInstance(
                    width,
                    height,
                    Image.SCALE_SMOOTH
                );
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            LOGGER.log(
                Level.WARNING,
                "Failed to create image icon: " + filename,
                e
            );
        }
        return null;
    }

    /**
     * Loads a sound clip from resources
     *
     * @param filename Sound filename
     * @return Clip object or null if loading failed
     * @throws UnsupportedAudioFileException If audio format is not supported
     * @throws IOException If sound cannot be loaded
     */
    public Clip loadSound(String filename)
        throws UnsupportedAudioFileException, IOException {
        if (soundCache.containsKey(filename)) {
            return soundCache.get(filename);
        }

        Clip clip = null;
        try {
            File soundFile = new File(soundPath + filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                soundFile
            );
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundCache.put(filename, clip);
            LOGGER.fine("Loaded sound: " + filename);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load sound: " + filename, e);
            throw new IOException("Failed to load sound: " + filename, e);
        }

        return clip;
    }

    /**
     * Loads a font from resources
     *
     * @param filename Font filename
     * @return Font object or null if loading failed
     * @throws FontFormatException If font format is not supported
     * @throws IOException If font cannot be loaded
     */
    public Font loadFont(String filename)
        throws FontFormatException, IOException {
        if (fontCache.containsKey(filename)) {
            return fontCache.get(filename);
        }

        Font font = null;
        try {
            File fontFile = new File(fontPath + filename);
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            fontCache.put(filename, font);
            LOGGER.fine("Loaded font: " + filename);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load font: " + filename, e);
            throw e;
        }

        return font;
    }

    /**
     * Gets a font with specified size
     *
     * @param filename Font filename
     * @param style Font style
     * @param size Font size
     * @return Font object or null if loading failed
     */
    public Font getFont(String filename, int style, float size) {
        try {
            Font baseFont = loadFont(filename);
            if (baseFont != null) {
                return baseFont.deriveFont(style, size);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get font: " + filename, e);
        }
        return null;
    }

    /**
     * Clears all cached resources
     */
    public void clearCache() {
        imageCache.clear();

        for (Clip clip : soundCache.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
        soundCache.clear();

        fontCache.clear();
        LOGGER.info("Resource cache cleared");
    }

    /**
     * Loads a text file from resources as a string
     *
     * @param filename Text file name
     * @return String content or null if loading failed
     */
    public String loadTextFile(String filename) {
        try (
            InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(filename)
        ) {
            if (is != null) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                return new String(buffer);
            }
        } catch (Exception e) {
            LOGGER.log(
                Level.WARNING,
                "Failed to load text file: " + filename,
                e
            );
        }
        return null;
    }
}
