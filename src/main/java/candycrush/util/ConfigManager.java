package candycrush.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages application configuration.
 * Loads, saves, and provides access to application properties.
 * Implements the Singleton pattern to ensure only one configuration
 * manager exists throughout the application.
 */
public class ConfigManager {

    private static final Logger LOGGER = Logger.getLogger(
        ConfigManager.class.getName()
    );
    private static final String CONFIG_FILE =
        "src/main/resources/config.properties";
    private static final String USER_CONFIG_FILE =
        System.getProperty("user.home") + "/.candycrush/user.properties";

    private static ConfigManager instance;
    private Properties config;
    private Properties userConfig;

    /**
     * Private constructor to enforce Singleton pattern
     */
    private ConfigManager() {
        config = new Properties();
        userConfig = new Properties();
    }

    /**
     * Returns the singleton instance of ConfigManager
     *
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Loads configuration from default locations
     */
    public void loadConfiguration() {
        try {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                config.load(fis);
                LOGGER.info("Default configuration loaded");
            } catch (IOException e) {
                LOGGER.log(
                    Level.WARNING,
                    "Could not load default configuration",
                    e
                );
            }

            try (FileInputStream fis = new FileInputStream(USER_CONFIG_FILE)) {
                userConfig.load(fis);
                LOGGER.info("User configuration loaded");
            } catch (IOException e) {
                LOGGER.log(
                    Level.INFO,
                    "No user configuration found, using defaults"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration", e);
        }
    }

    /**
     * Save user configuration to default location
     */
    public void saveUserConfiguration() {
        try {
            try (
                FileOutputStream fos = new FileOutputStream(USER_CONFIG_FILE)
            ) {
                userConfig.store(fos, "User configuration for Candy Crush");
                LOGGER.info("User configuration saved");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not save user configuration", e);
        }
    }

    /**
     * Gets a property value, prioritizing user configuration over default
     *
     * @param key Property key
     * @param defaultValue Default value if property is not found
     * @return Property value
     */
    public String getProperty(String key, String defaultValue) {
        return userConfig.getProperty(
            key,
            config.getProperty(key, defaultValue)
        );
    }

    /**
     * Gets a property as an integer value
     *
     * @param key Property key
     * @param defaultValue Default value if property is not found or invalid
     * @return Property value as integer
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(
                getProperty(key, String.valueOf(defaultValue))
            );
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid integer property: " + key);
            return defaultValue;
        }
    }

    /**
     * Gets a property as a boolean value
     *
     * @param key Property key
     * @param defaultValue Default value if property is not found
     * @return Property value as boolean
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(
            getProperty(key, String.valueOf(defaultValue))
        );
    }

    /**
     * Gets a property as a double value
     *
     * @param key Property key
     * @param defaultValue Default value if property is not found or invalid
     * @return Property value as double
     */
    public double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(
                getProperty(key, String.valueOf(defaultValue))
            );
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid double property: " + key);
            return defaultValue;
        }
    }

    /**
     * Sets a user property
     *
     * @param key Property key
     * @param value Property value
     */
    public void setUserProperty(String key, String value) {
        userConfig.setProperty(key, value);
    }

    /**
     * Sets a user property as integer
     *
     * @param key Property key
     * @param value Integer value
     */
    public void setUserIntProperty(String key, int value) {
        setUserProperty(key, String.valueOf(value));
    }

    /**
     * Sets a user property as boolean
     *
     * @param key Property key
     * @param value Boolean value
     */
    public void setUserBooleanProperty(String key, boolean value) {
        setUserProperty(key, String.valueOf(value));
    }

    /**
     * Sets a user property as double
     *
     * @param key Property key
     * @param value Double value
     */
    public void setUserDoubleProperty(String key, double value) {
        setUserProperty(key, String.valueOf(value));
    }
}
