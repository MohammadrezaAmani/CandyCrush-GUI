package candycrush.view.screens;

import candycrush.audio.AudioManager;
import candycrush.util.ConfigManager;
import candycrush.util.ResourceLoader;
import candycrush.util.ThemeManager;
import candycrush.view.components.FancyButton;
import candycrush.view.components.GlassPanel;
import candycrush.view.dialogs.HighScoreDialog;
import candycrush.view.game.GameScreen;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Main menu screen for the Candy Crush game.
 * Provides access to game modes, settings, high scores, and more.
 */
public class MainMenuScreen extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(
        MainMenuScreen.class.getName()
    );
    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;

    private final ThemeManager themeManager;
    private final Timer animationTimer;
    private final Random random;
    private final List<CandyParticle> particles;

    private JPanel mainMenuPanel;
    private JPanel gameModesPanel;
    private JPanel settingsPanel;
    private JPanel profilePanel;

    private class CandyParticle {

        float x, y;
        float speedX, speedY;
        float rotation;
        float rotationSpeed;
        int size;
        Color color;
        int shape;

        CandyParticle() {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
            reset();
        }

        void reset() {
            x = -20 + random.nextInt(40);
            y = -20;
            speedX = -1 + random.nextFloat() * 2;
            speedY = 1 + random.nextFloat() * 2;
            rotation = random.nextFloat() * 360;
            rotationSpeed = -2 + random.nextFloat() * 4;
            size = 10 + random.nextInt(30);

            switch (random.nextInt(4)) {
                case 0:
                    color = new Color(220, 20, 60, 180);
                    break;
                case 1:
                    color = new Color(30, 144, 255, 180);
                    break;
                case 2:
                    color = new Color(50, 205, 50, 180);
                    break;
                default:
                    color = new Color(255, 215, 0, 180);
                    break;
            }

            shape = random.nextInt(4);
        }

        void update() {
            x += speedX;
            y += speedY;
            rotation += rotationSpeed;

            if (y > HEIGHT + 50 || x < -50 || x > WIDTH + 50) {
                reset();
                y = -size;
                x = random.nextInt(WIDTH);
            }
        }

        void draw(Graphics2D g2d) {
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(x, y);
            g2d.rotate(Math.toRadians(rotation), size / 2, size / 2);
            g2d.setColor(color);

            switch (shape) {
                case 0:
                    g2d.fillOval(0, 0, size, size);
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawOval(0, 0, size, size);
                    break;
                case 1:
                    g2d.fillRect(0, 0, size, size);
                    g2d.setColor(new Color(255, 255, 255, 100));
                    for (int i = 0; i < size; i += 4) {
                        g2d.drawLine(0, i, size, i);
                    }
                    break;
                case 2:
                    g2d.fillRoundRect(0, 0, size, size, size / 3, size / 3);
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawRoundRect(0, 0, size, size, size / 3, size / 3);
                    g2d.drawRoundRect(
                        size / 6,
                        size / 6,
                        (2 * size) / 3,
                        (2 * size) / 3,
                        size / 6,
                        size / 6
                    );
                    break;
                case 3:
                    g2d.fillOval(0, 0, size, size);
                    g2d.setColor(new Color(255, 255, 255, 150));
                    g2d.drawLine(0, size / 2, size, size / 2);
                    g2d.drawLine(size / 2, 0, size / 2, size);
                    g2d.drawOval(0, 0, size, size);
                    break;
            }

            g2d.setTransform(oldTransform);
        }
    }

    /**
     * Create a new main menu screen
     */
    public MainMenuScreen() {
        setTitle("Candy Crush - Main Menu");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        themeManager = ThemeManager.getInstance();
        random = new Random();
        particles = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            particles.add(new CandyParticle());
        }

        initializeUI();

        animationTimer = new Timer(
            16,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (CandyParticle particle : particles) {
                        particle.update();
                    }

                    repaint();
                }
            }
        );
        animationTimer.start();

        AudioManager.getInstance().startBackgroundMusic();
    }

    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                GradientPaint gradient = new GradientPaint(
                    0,
                    0,
                    new Color(255, 182, 193),
                    0,
                    HEIGHT,
                    new Color(176, 224, 230)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, WIDTH, HEIGHT);

                for (CandyParticle particle : particles) {
                    particle.draw(g2d);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        mainMenuPanel = createMainMenuPanel();

        gameModesPanel = createGameModesPanel();
        settingsPanel = createSettingsPanel();
        profilePanel = createProfilePanel();

        contentPanel.add(mainMenuPanel, "MAIN");
        contentPanel.add(gameModesPanel, "GAME_MODES");
        contentPanel.add(settingsPanel, "SETTINGS");
        contentPanel.add(profilePanel, "PROFILE");

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Create the main menu panel with logo and primary buttons
     */
    private JPanel createMainMenuPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = ResourceLoader.getInstance()
                .getImageIcon("logo.png", 400, 200);
            if (logoIcon != null) {
                logoLabel.setIcon(logoIcon);
            } else {
                logoLabel.setText("CANDY CRUSH");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
                logoLabel.setForeground(new Color(220, 20, 60));
            }
        } catch (Exception e) {
            logoLabel.setText("CANDY CRUSH");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
            logoLabel.setForeground(new Color(220, 20, 60));
        }
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 0, 20));
        buttonsPanel.setOpaque(false);

        FancyButton playButton = new FancyButton("PLAY GAME");
        FancyButton highScoresButton = new FancyButton("HIGH SCORES");
        FancyButton settingsButton = new FancyButton("SETTINGS");
        FancyButton profileButton = new FancyButton("PROFILE");
        FancyButton exitButton = new FancyButton("EXIT GAME");

        playButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "GAME_MODES");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        highScoresButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            showHighScores();
        });

        settingsButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "SETTINGS");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        profileButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "PROFILE");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        exitButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            confirmExit();
        });

        buttonsPanel.add(playButton);
        buttonsPanel.add(highScoresButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(profileButton);
        buttonsPanel.add(exitButton);

        panel.add(logoLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        JLabel versionLabel = new JLabel(
            "Version " +
            ConfigManager.getInstance().getProperty("app.version", "2.0.0")
        );
        versionLabel.setForeground(new Color(100, 100, 100));
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(versionLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the game modes panel with different play options
     */
    private JPanel createGameModesPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("GAME MODES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 20, 60));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel modesPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        modesPanel.setOpaque(false);

        JPanel classicPanel = createGameModePanel(
            "Classic Mode",
            "Match candies to reach the target score within the available moves",
            "mode_classic.png",
            e -> startGame("CLASSIC")
        );

        JPanel timedPanel = createGameModePanel(
            "Timed Mode",
            "Race against the clock to score as many points as possible",
            "mode_timed.png",
            e -> startGame("TIMED")
        );

        JPanel puzzlePanel = createGameModePanel(
            "Puzzle Mode",
            "Complete specific candy matching challenges and missions",
            "mode_puzzle.png",
            e -> startGame("PUZZLE")
        );

        JPanel multiplayerPanel = createGameModePanel(
            "Multiplayer Mode",
            "Challenge a friend in a 2-player competitive match",
            "mode_multiplayer.png",
            e -> startGame("MULTIPLAYER")
        );

        modesPanel.add(classicPanel);
        modesPanel.add(timedPanel);
        modesPanel.add(puzzlePanel);
        modesPanel.add(multiplayerPanel);

        FancyButton backButton = new FancyButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "MAIN");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(modesPanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create an individual game mode panel with icon, title, and description
     */
    private JPanel createGameModePanel(
        String title,
        String description,
        String iconFile,
        ActionListener action
    ) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 20, 60, 150),
                    2,
                    true
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = ResourceLoader.getInstance()
                .getImageIcon(iconFile, 80, 80);
            if (icon != null) {
                iconLabel.setIcon(icon);
            }
        } catch (Exception e) {
            iconLabel.setText("?");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 48));
        }
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setForeground(Color.BLACK);

        JButton playButton = new JButton("PLAY");
        playButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            if (action != null) {
                action.actionPerformed(e);
            }
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(descArea, BorderLayout.CENTER);
        panel.add(playButton, BorderLayout.SOUTH);

        panel.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBorder(
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                new Color(220, 20, 60),
                                3,
                                true
                            ),
                            BorderFactory.createEmptyBorder(9, 9, 9, 9)
                        )
                    );
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBorder(
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                new Color(220, 20, 60, 150),
                                2,
                                true
                            ),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                        )
                    );
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    AudioManager.getInstance()
                        .playSound(AudioManager.SOUND_BUTTON_CLICK);
                    if (action != null) {
                        action.actionPerformed(
                            new ActionEvent(
                                playButton,
                                ActionEvent.ACTION_PERFORMED,
                                "play"
                            )
                        );
                    }
                }
            }
        );

        return panel;
    }

    /**
     * Create the settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("SETTINGS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 20, 60));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel settingsContent = new JPanel(new GridLayout(5, 1, 10, 20));
        settingsContent.setOpaque(false);

        JPanel audioPanel = new JPanel(new BorderLayout(10, 0));
        audioPanel.setOpaque(false);
        JLabel audioLabel = new JLabel("Audio Settings");
        audioLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel audioControls = new JPanel(new GridLayout(2, 2, 10, 10));
        audioControls.setOpaque(false);

        JCheckBox soundToggle = new JCheckBox("Sound Effects");
        soundToggle.setSelected(AudioManager.getInstance().isSoundEnabled());
        soundToggle.setOpaque(false);
        soundToggle.addActionListener(e -> {
            AudioManager.getInstance()
                .setSoundEnabled(soundToggle.isSelected());
            if (soundToggle.isSelected()) {
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
            }
        });

        JCheckBox musicToggle = new JCheckBox("Background Music");
        musicToggle.setSelected(AudioManager.getInstance().isMusicEnabled());
        musicToggle.setOpaque(false);
        musicToggle.addActionListener(e -> {
            AudioManager.getInstance()
                .setMusicEnabled(musicToggle.isSelected());
        });

        JLabel soundVolumeLabel = new JLabel("Sound Volume");
        JSlider soundVolumeSlider = new JSlider(
            0,
            100,
            (int) (AudioManager.getInstance().getSoundVolume() * 100)
        );
        soundVolumeSlider.setOpaque(false);
        soundVolumeSlider.addChangeListener(e -> {
            float volume = soundVolumeSlider.getValue() / 100f;
            AudioManager.getInstance().setSoundVolume(volume);
            if (!soundVolumeSlider.getValueIsAdjusting()) {
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
            }
        });

        JLabel musicVolumeLabel = new JLabel("Music Volume");
        JSlider musicVolumeSlider = new JSlider(
            0,
            100,
            (int) (AudioManager.getInstance().getMusicVolume() * 100)
        );
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.addChangeListener(e -> {
            float volume = musicVolumeSlider.getValue() / 100f;
            AudioManager.getInstance().setMusicVolume(volume);
        });

        audioControls.add(soundToggle);
        audioControls.add(soundVolumeLabel);
        audioControls.add(musicToggle);
        audioControls.add(musicVolumeLabel);

        JPanel volumePanel = new JPanel(new GridLayout(2, 1, 0, 10));
        volumePanel.setOpaque(false);
        volumePanel.add(soundVolumeSlider);
        volumePanel.add(musicVolumeSlider);

        audioPanel.add(audioLabel, BorderLayout.NORTH);
        audioPanel.add(audioControls, BorderLayout.CENTER);
        audioPanel.add(volumePanel, BorderLayout.SOUTH);

        JPanel visualPanel = new JPanel(new BorderLayout(10, 0));
        visualPanel.setOpaque(false);
        JLabel visualLabel = new JLabel("Visual Settings");
        visualLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel visualControls = new JPanel(new GridLayout(2, 2, 10, 10));
        visualControls.setOpaque(false);

        JLabel themeLabel = new JLabel("Theme:");
        String[] themes = { "Light", "Dark", "Candy" };
        JComboBox<String> themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem(
            themeManager.getCurrentTheme().equals(ThemeManager.THEME_DARK)
                ? "Dark"
                : themeManager
                        .getCurrentTheme()
                        .equals(ThemeManager.THEME_CANDY)
                    ? "Candy"
                    : "Light"
        );
        themeComboBox.addActionListener(e -> {
            String selected = (String) themeComboBox.getSelectedItem();
            String theme = selected.equals("Dark")
                ? ThemeManager.THEME_DARK
                : selected.equals("Candy")
                    ? ThemeManager.THEME_CANDY
                    : ThemeManager.THEME_LIGHT;
            themeManager.applyTheme(theme);
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);

            ConfigManager.getInstance().setUserProperty("ui.theme", theme);
            ConfigManager.getInstance().saveUserConfiguration();

            JOptionPane.showMessageDialog(
                this,
                "Theme changes will take full effect after restarting the game.",
                "Theme Changed",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        JLabel animationLabel = new JLabel("Animation Speed:");
        String[] speeds = { "Slow", "Normal", "Fast" };
        JComboBox<String> animationComboBox = new JComboBox<>(speeds);
        animationComboBox.setSelectedIndex(1);
        animationComboBox.addActionListener(e -> {
            String selected = (String) animationComboBox.getSelectedItem();
            ConfigManager.getInstance()
                .setUserProperty("ui.animation.speed", selected.toUpperCase());
            ConfigManager.getInstance().saveUserConfiguration();
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        JCheckBox fullscreenToggle = new JCheckBox("Fullscreen Mode");
        fullscreenToggle.setOpaque(false);
        fullscreenToggle.setSelected(
            ConfigManager.getInstance()
                .getBooleanProperty("ui.fullscreen", false)
        );
        fullscreenToggle.addActionListener(e -> {
            boolean fullscreen = fullscreenToggle.isSelected();
            ConfigManager.getInstance()
                .setUserBooleanProperty("ui.fullscreen", fullscreen);
            ConfigManager.getInstance().saveUserConfiguration();
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);

            JOptionPane.showMessageDialog(
                this,
                "Fullscreen changes will take effect after restarting the game.",
                "Display Mode Changed",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        visualControls.add(themeLabel);
        visualControls.add(themeComboBox);
        visualControls.add(animationLabel);
        visualControls.add(animationComboBox);

        visualPanel.add(visualLabel, BorderLayout.NORTH);
        visualPanel.add(visualControls, BorderLayout.CENTER);
        visualPanel.add(fullscreenToggle, BorderLayout.SOUTH);

        JPanel gameplayPanel = new JPanel(new BorderLayout(10, 0));
        gameplayPanel.setOpaque(false);
        JLabel gameplayLabel = new JLabel("Gameplay Settings");
        gameplayLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel gameplayControls = new JPanel(new GridLayout(2, 2, 10, 10));
        gameplayControls.setOpaque(false);

        JLabel difficultyLabel = new JLabel("Difficulty:");
        String[] difficulties = { "Easy", "Medium", "Hard" };
        JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setSelectedItem(
            ConfigManager.getInstance()
                    .getProperty("game.default.difficulty", "MEDIUM")
                    .equals("EASY")
                ? "Easy"
                : ConfigManager.getInstance()
                        .getProperty("game.default.difficulty", "MEDIUM")
                        .equals("HARD")
                    ? "Hard"
                    : "Medium"
        );
        difficultyComboBox.addActionListener(e -> {
            String selected = (String) difficultyComboBox.getSelectedItem();
            ConfigManager.getInstance()
                .setUserProperty(
                    "game.default.difficulty",
                    selected.toUpperCase()
                );
            ConfigManager.getInstance().saveUserConfiguration();
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        JLabel aiLabel = new JLabel("AI Difficulty:");
        String[] aiLevels = { "Easy", "Medium", "Hard" };
        JComboBox<String> aiComboBox = new JComboBox<>(aiLevels);
        aiComboBox.setSelectedItem(
            ConfigManager.getInstance()
                    .getProperty("ai.autoplay.difficulty", "MEDIUM")
                    .equals("EASY")
                ? "Easy"
                : ConfigManager.getInstance()
                        .getProperty("ai.autoplay.difficulty", "MEDIUM")
                        .equals("HARD")
                    ? "Hard"
                    : "Medium"
        );
        aiComboBox.addActionListener(e -> {
            String selected = (String) aiComboBox.getSelectedItem();
            ConfigManager.getInstance()
                .setUserProperty(
                    "ai.autoplay.difficulty",
                    selected.toUpperCase()
                );
            ConfigManager.getInstance().saveUserConfiguration();
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        JCheckBox hintToggle = new JCheckBox("Show Hints");
        hintToggle.setOpaque(false);
        hintToggle.setSelected(
            ConfigManager.getInstance()
                .getBooleanProperty("ai.hint.enabled", true)
        );
        hintToggle.addActionListener(e -> {
            ConfigManager.getInstance()
                .setUserBooleanProperty(
                    "ai.hint.enabled",
                    hintToggle.isSelected()
                );
            ConfigManager.getInstance().saveUserConfiguration();
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        gameplayControls.add(difficultyLabel);
        gameplayControls.add(difficultyComboBox);
        gameplayControls.add(aiLabel);
        gameplayControls.add(aiComboBox);

        gameplayPanel.add(gameplayLabel, BorderLayout.NORTH);
        gameplayPanel.add(gameplayControls, BorderLayout.CENTER);
        gameplayPanel.add(hintToggle, BorderLayout.SOUTH);

        settingsContent.add(audioPanel);
        settingsContent.add(visualPanel);
        settingsContent.add(gameplayPanel);

        JButton resetButton = new JButton("Reset All Settings to Default");
        resetButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all settings to their default values?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
                resetSettings();
                JOptionPane.showMessageDialog(
                    this,
                    "Settings have been reset to default values."
                );

                CardLayout cl =
                    (CardLayout) (((JPanel) panel.getParent()).getLayout());
                cl.show(panel.getParent(), "MAIN");
                cl.show(panel.getParent(), "SETTINGS");
            }
        });
        settingsContent.add(resetButton);

        FancyButton backButton = new FancyButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "MAIN");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(settingsContent, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the profile panel
     */
    private JPanel createProfilePanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("PLAYER PROFILE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 20, 60));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel profileContent = new JPanel(new BorderLayout(20, 20));
        profileContent.setOpaque(false);

        JPanel statsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 20, 60, 150),
                    2,
                    true
                ),
                "Player Statistics",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(220, 20, 60)
            )
        );

        addStatRow(statsPanel, "Total Games Played:", "0");
        addStatRow(statsPanel, "Games Won:", "0");
        addStatRow(statsPanel, "Highest Score:", "0");
        addStatRow(statsPanel, "Total Matches Made:", "0");
        addStatRow(statsPanel, "Special Candies Created:", "0");

        JPanel achievementsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        achievementsPanel.setOpaque(false);
        achievementsPanel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 20, 60, 150),
                    2,
                    true
                ),
                "Achievements",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(220, 20, 60)
            )
        );

        addAchievement(
            achievementsPanel,
            "Candy Crusher",
            "Win your first game",
            false
        );
        addAchievement(
            achievementsPanel,
            "Sweet Specialist",
            "Create 10 special candies",
            false
        );
        addAchievement(
            achievementsPanel,
            "Candy Connoisseur",
            "Reach a score of 5000",
            false
        );
        addAchievement(
            achievementsPanel,
            "Sugar Rush",
            "Complete a timed mode with 2000+ points",
            false
        );

        profileContent.add(statsPanel, BorderLayout.NORTH);
        profileContent.add(achievementsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonsPanel.setOpaque(false);

        JButton resetStatsButton = new JButton("Reset Statistics");
        resetStatsButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all your statistics and achievements?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
                JOptionPane.showMessageDialog(
                    this,
                    "Statistics and achievements have been reset."
                );
            }
        });

        FancyButton backButton = new FancyButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            CardLayout cl =
                (CardLayout) (((JPanel) panel.getParent()).getLayout());
            cl.show(panel.getParent(), "MAIN");
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
        });

        buttonsPanel.add(resetStatsButton);
        buttonsPanel.add(backButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(profileContent, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Add a stat row to the stats panel
     */
    private void addStatRow(JPanel panel, String label, String value) {
        JLabel statLabel = new JLabel(label);
        statLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel statValue = new JLabel(value);
        statValue.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(statLabel);
        panel.add(statValue);
    }

    /**
     * Add an achievement to the achievements panel
     */
    private void addAchievement(
        JPanel panel,
        String title,
        String description,
        boolean unlocked
    ) {
        JPanel achievementPanel = new JPanel(new BorderLayout(10, 0));
        achievementPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel statusLabel = new JLabel(unlocked ? "✓ Unlocked" : "✗ Locked");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(
            unlocked ? new Color(0, 150, 0) : new Color(150, 0, 0)
        );

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        achievementPanel.add(textPanel, BorderLayout.CENTER);
        achievementPanel.add(statusLabel, BorderLayout.EAST);

        panel.add(achievementPanel);
    }

    /**
     * Reset settings to default values
     */
    private void resetSettings() {
        AudioManager.getInstance().setSoundEnabled(true);
        AudioManager.getInstance().setMusicEnabled(true);
        AudioManager.getInstance().setSoundVolume(0.8f);
        AudioManager.getInstance().setMusicVolume(0.6f);

        themeManager.applyTheme(ThemeManager.THEME_LIGHT);

        ConfigManager config = ConfigManager.getInstance();
        config.setUserProperty("game.default.difficulty", "MEDIUM");
        config.setUserProperty("ai.autoplay.difficulty", "MEDIUM");
        config.setUserBooleanProperty("ai.hint.enabled", true);
        config.setUserProperty("ui.animation.speed", "NORMAL");
        config.setUserBooleanProperty("ui.fullscreen", false);

        config.saveUserConfiguration();
    }

    /**
     * Show high scores dialog
     */
    private void showHighScores() {
        new HighScoreDialog(this).setVisible(true);
    }

    /**
     * Show exit confirmation dialog
     */
    private void confirmExit() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the game?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            if (animationTimer != null) {
                animationTimer.stop();
            }

            AudioManager.getInstance().cleanup();

            dispose();
            System.exit(0);
        }
    }

    /**
     * Start a new game with the specified mode
     */
    private void startGame(String mode) {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        EventQueue.invokeLater(() -> {
            GameScreen gameScreen = new GameScreen(mode);
            gameScreen.setVisible(true);
            dispose();
        });
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainMenuScreen menu = new MainMenuScreen();
                menu.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to launch main menu", e);
                JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while starting the game.",
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
