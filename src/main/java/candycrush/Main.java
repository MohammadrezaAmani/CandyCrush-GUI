package candycrush;

import candycrush.audio.AudioManager;
import candycrush.util.ConfigManager;
import candycrush.util.ResourceLoader;
import candycrush.util.ThemeManager;
import candycrush.view.screens.SplashScreen;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry point for the Candy Crush application.
 * Initializes resources, configuration, and launches the UI.
 *
 * @author Mohammadreza Amani
 * @version 2.0
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Application entry point
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            LOGGER.info("Starting Candy Crush application");
            initialize();

            SwingUtilities.invokeLater(() -> {
                SplashScreen splashScreen = new SplashScreen();
                splashScreen.setVisible(true);
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            System.exit(1);
        }
    }

    /**
     * Initialize application resources and components
     */
    private static void initialize() {
        ConfigManager.getInstance().loadConfiguration();

        ResourceLoader.getInstance().preloadResources();

        AudioManager.getInstance().initialize();

        ThemeManager.getInstance()
            .applyTheme(
                ConfigManager.getInstance().getProperty("ui.theme", "LIGHT")
            );

        registerCustomFonts();

        LOGGER.info("Application initialized successfully");
    }

    /**
     * Register custom fonts for the application
     */
    private static void registerCustomFonts() {
        try {
            Font candyCrushFont = ResourceLoader.getInstance()
                .loadFont("candy_crush.ttf");
            Font gameFont = ResourceLoader.getInstance()
                .loadFont("game_font.ttf");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load custom fonts", e);
        }
    }
}
