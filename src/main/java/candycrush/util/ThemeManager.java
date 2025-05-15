package candycrush.util;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 * Manages application themes and UI styling.
 * Supports switching between different visual themes (light mode, dark mode, etc.)
 * and applying consistent styling throughout the application.
 */
public class ThemeManager {

    private static final Logger LOGGER = Logger.getLogger(
        ThemeManager.class.getName()
    );

    private static ThemeManager instance;

    private final Map<String, Color> colorMap;
    private final Map<String, Font> fontMap;
    private String currentTheme;

    public static final String THEME_LIGHT = "LIGHT";
    public static final String THEME_DARK = "DARK";
    public static final String THEME_CANDY = "CANDY";

    /**
     * Private constructor to enforce Singleton pattern
     */
    private ThemeManager() {
        colorMap = new HashMap<>();
        fontMap = new HashMap<>();
        currentTheme = THEME_LIGHT;
        initializeThemes();
    }

    /**
     * Returns the singleton instance
     *
     * @return ThemeManager instance
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Initialize theme definitions
     */
    private void initializeThemes() {
        fontMap.put("TITLE", new Font("Arial", Font.BOLD, 24));
        fontMap.put("HEADER", new Font("Arial", Font.BOLD, 18));
        fontMap.put("BUTTON", new Font("Arial", Font.BOLD, 14));
        fontMap.put("TEXT", new Font("Arial", Font.PLAIN, 14));

        try {
            Font candyFont = ResourceLoader.getInstance()
                .getFont("candy_crush.ttf", Font.BOLD, 24);
            if (candyFont != null) {
                fontMap.put("CANDY_TITLE", candyFont);
                fontMap.put(
                    "CANDY_HEADER",
                    candyFont.deriveFont(Font.BOLD, 18)
                );
                fontMap.put(
                    "CANDY_BUTTON",
                    candyFont.deriveFont(Font.BOLD, 14)
                );
            }
        } catch (Exception e) {
            LOGGER.log(
                Level.WARNING,
                "Failed to load custom fonts, using defaults",
                e
            );
        }

        defineThemeColors(
            THEME_LIGHT,
            new Color(240, 240, 240),
            new Color(255, 255, 255),
            new Color(180, 180, 180),
            new Color(50, 50, 50),
            new Color(220, 70, 120),
            new Color(50, 120, 220),
            new Color(240, 50, 50),
            new Color(70, 180, 70)
        );

        defineThemeColors(
            THEME_DARK,
            new Color(40, 40, 40),
            new Color(60, 60, 60),
            new Color(80, 80, 80),
            new Color(220, 220, 220),
            new Color(180, 50, 100),
            new Color(50, 100, 180),
            new Color(240, 70, 70),
            new Color(50, 160, 50)
        );

        defineThemeColors(
            THEME_CANDY,
            new Color(255, 230, 250),
            new Color(255, 240, 255),
            new Color(230, 180, 220),
            new Color(80, 40, 60),
            new Color(240, 90, 140),
            new Color(140, 70, 200),
            new Color(255, 120, 50),
            new Color(50, 200, 120)
        );
    }

    /**
     * Define colors for a specific theme
     */
    private void defineThemeColors(
        String theme,
        Color background,
        Color componentBackground,
        Color border,
        Color text,
        Color primary,
        Color secondary,
        Color accent,
        Color success
    ) {
        String prefix = theme + "_";
        colorMap.put(prefix + "BACKGROUND", background);
        colorMap.put(prefix + "COMPONENT_BACKGROUND", componentBackground);
        colorMap.put(prefix + "BORDER", border);
        colorMap.put(prefix + "TEXT", text);
        colorMap.put(prefix + "PRIMARY", primary);
        colorMap.put(prefix + "SECONDARY", secondary);
        colorMap.put(prefix + "ACCENT", accent);
        colorMap.put(prefix + "SUCCESS", success);
    }

    /**
     * Apply a theme to the application
     *
     * @param themeName Name of the theme to apply
     */
    public void applyTheme(String themeName) {
        if (!colorMap.containsKey(themeName + "_BACKGROUND")) {
            LOGGER.warning(
                "Unknown theme: " + themeName + ", falling back to light theme"
            );
            themeName = THEME_LIGHT;
        }

        currentTheme = themeName;
        LOGGER.info("Applying theme: " + themeName);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            updateUIManagerColors(themeName);

            updateUIManagerFonts();
        } catch (
            UnsupportedLookAndFeelException
            | ClassNotFoundException
            | InstantiationException
            | IllegalAccessException e
        ) {
            LOGGER.log(Level.WARNING, "Failed to apply theme", e);
        }
    }

    /**
     * Update UI Manager colors based on theme
     *
     * @param themeName Theme name
     */
    private void updateUIManagerColors(String themeName) {
        String prefix = themeName + "_";

        Color background = getColor(prefix + "BACKGROUND");
        Color componentBg = getColor(prefix + "COMPONENT_BACKGROUND");
        Color border = getColor(prefix + "BORDER");
        Color text = getColor(prefix + "TEXT");
        Color primary = getColor(prefix + "PRIMARY");

        UIManager.put("Panel.background", new ColorUIResource(background));
        UIManager.put("OptionPane.background", new ColorUIResource(background));
        UIManager.put("Button.background", new ColorUIResource(componentBg));
        UIManager.put("Button.foreground", new ColorUIResource(text));
        UIManager.put("Button.select", new ColorUIResource(primary));
        UIManager.put("Button.border", new ColorUIResource(border));
        UIManager.put("Label.foreground", new ColorUIResource(text));
        UIManager.put("TextField.background", new ColorUIResource(componentBg));
        UIManager.put("TextField.foreground", new ColorUIResource(text));
        UIManager.put("TextField.caretForeground", new ColorUIResource(text));
        UIManager.put("TextArea.background", new ColorUIResource(componentBg));
        UIManager.put("TextArea.foreground", new ColorUIResource(text));
        UIManager.put("ComboBox.background", new ColorUIResource(componentBg));
        UIManager.put("ComboBox.foreground", new ColorUIResource(text));
        UIManager.put("Menu.background", new ColorUIResource(background));
        UIManager.put("Menu.foreground", new ColorUIResource(text));
        UIManager.put("MenuItem.background", new ColorUIResource(background));
        UIManager.put("MenuItem.foreground", new ColorUIResource(text));
        UIManager.put("MenuBar.background", new ColorUIResource(background));
        UIManager.put("List.background", new ColorUIResource(componentBg));
        UIManager.put("List.foreground", new ColorUIResource(text));
    }

    /**
     * Update UI Manager fonts
     */
    private void updateUIManagerFonts() {
        Font buttonFont = getFont("BUTTON");
        Font textFont = getFont("TEXT");
        Font titleFont = getFont("TITLE");

        if (
            currentTheme.equals(THEME_CANDY) &&
            fontMap.containsKey("CANDY_BUTTON")
        ) {
            buttonFont = getFont("CANDY_BUTTON");
            titleFont = getFont("CANDY_TITLE");
        }

        UIManager.put("Button.font", new FontUIResource(buttonFont));
        UIManager.put("Label.font", new FontUIResource(textFont));
        UIManager.put("TextField.font", new FontUIResource(textFont));
        UIManager.put("TextArea.font", new FontUIResource(textFont));
        UIManager.put("ComboBox.font", new FontUIResource(textFont));
        UIManager.put("MenuItem.font", new FontUIResource(textFont));
        UIManager.put("TitledBorder.font", new FontUIResource(titleFont));
    }

    /**
     * Gets a color from the current theme
     *
     * @param key Color key
     * @return Color object
     */
    public Color getColor(String key) {
        String themeKey = key;
        if (!key.startsWith(currentTheme)) {
            themeKey = currentTheme + "_" + key;
        }

        if (colorMap.containsKey(themeKey)) {
            return colorMap.get(themeKey);
        } else if (colorMap.containsKey(key)) {
            return colorMap.get(key);
        } else {
            LOGGER.warning("Unknown color key: " + key);
            return Color.GRAY;
        }
    }

    /**
     * Gets a font
     *
     * @param key Font key
     * @return Font object
     */
    public Font getFont(String key) {
        if (fontMap.containsKey(key)) {
            return fontMap.get(key);
        } else {
            LOGGER.warning("Unknown font key: " + key);
            return new Font("Arial", Font.PLAIN, 12);
        }
    }

    /**
     * Gets the current theme name
     *
     * @return Current theme name
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Checks if the current theme is dark mode
     *
     * @return true if dark mode is active
     */
    public boolean isDarkMode() {
        return currentTheme.equals(THEME_DARK);
    }

    /**
     * Gets background color for components
     *
     * @return Background color
     */
    public Color getBackgroundColor() {
        return getColor("BACKGROUND");
    }

    /**
     * Gets component background color
     *
     * @return Component background color
     */
    public Color getComponentBackgroundColor() {
        return getColor("COMPONENT_BACKGROUND");
    }

    /**
     * Gets text color
     *
     * @return Text color
     */
    public Color getTextColor() {
        return getColor("TEXT");
    }

    /**
     * Gets primary color
     *
     * @return Primary color
     */
    public Color getPrimaryColor() {
        return getColor("PRIMARY");
    }
}
