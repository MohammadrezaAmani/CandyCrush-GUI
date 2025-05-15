package candycrush.view.game;

import candycrush.ai.GameAI;
import candycrush.audio.AudioManager;
import candycrush.model.Candy;
import candycrush.model.GameBoard;
import candycrush.util.ConfigManager;
import candycrush.util.ResourceLoader;
import candycrush.util.ThemeManager;
import candycrush.view.components.FancyButton;
import candycrush.view.components.GlassPanel;
import candycrush.view.dialogs.HighScoreDialog;
import candycrush.view.screens.MainMenuScreen;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * The main game screen where the gameplay occurs.
 * Handles rendering the game board, user interactions, animations, and game state.
 */
public class GameScreen extends JFrame implements Observer {

    private static final Logger LOGGER = Logger.getLogger(
        GameScreen.class.getName()
    );
    private static final int BOARD_SIZE = 10;
    private static final int CANDY_SIZE = 60;
    private static final int BOARD_PADDING = 20;

    private GameBoard gameBoard;
    private GameMode gameMode;
    private GameAI gameAI;
    private Thread aiThread;

    private JPanel gameBoardPanel;
    private JLabel scoreLabel;
    private JLabel movesLabel;
    private JLabel timeLabel;
    private JLabel targetLabel;
    private JProgressBar progressBar;
    private FancyButton pauseButton;
    private FancyButton hintButton;
    private FancyButton menuButton;
    private FancyButton resetButton;

    private boolean isPaused;
    private boolean isAnimating;
    private int secondsRemaining;
    private Timer gameTimer;
    private int hintCount;
    private int hintLimit = 3;

    private javax.swing.Timer animationTimer;
    private List<CandyAnimation> animations;

    public enum GameMode {
        CLASSIC("Classic Mode", "Reach the target score with limited moves"),
        TIMED("Timed Mode", "Score as much as possible in the time limit"),
        PUZZLE("Puzzle Mode", "Complete specific objectives"),
        MULTIPLAYER("Multiplayer Mode", "Compete with another player");

        private final String name;
        private final String description;

        GameMode(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    private class CandyAnimation {

        int row, col;
        float offsetX, offsetY;
        float targetOffsetX, targetOffsetY;
        float alpha;
        boolean isRemoving;
        Color color;

        CandyAnimation(
            int row,
            int col,
            float targetOffsetX,
            float targetOffsetY,
            Color color
        ) {
            this.row = row;
            this.col = col;
            this.offsetX = 0;
            this.offsetY = 0;
            this.targetOffsetX = targetOffsetX;
            this.targetOffsetY = targetOffsetY;
            this.alpha = 1.0f;
            this.isRemoving = false;
            this.color = color;
        }

        void update() {
            if (isRemoving) {
                alpha -= 0.1f;
                if (alpha < 0) alpha = 0;
            } else {
                offsetX += (targetOffsetX - offsetX) * 0.2f;
                offsetY += (targetOffsetY - offsetY) * 0.2f;

                if (
                    Math.abs(offsetX - targetOffsetX) < 0.1f &&
                    Math.abs(offsetY - targetOffsetY) < 0.1f
                ) {
                    offsetX = targetOffsetX;
                    offsetY = targetOffsetY;
                }
            }
        }

        boolean isFinished() {
            return (
                (isRemoving && alpha <= 0) ||
                (!isRemoving &&
                    offsetX == targetOffsetX &&
                    offsetY == targetOffsetY)
            );
        }
    }

    /**
     * Create a new game screen with the specified game mode
     *
     * @param mode Game mode identifier
     */
    public GameScreen(String mode) {
        gameMode = "TIMED".equals(mode)
            ? GameMode.TIMED
            : "PUZZLE".equals(mode)
                ? GameMode.PUZZLE
                : "MULTIPLAYER".equals(mode)
                    ? GameMode.MULTIPLAYER
                    : GameMode.CLASSIC;

        setTitle("Candy Crush - " + gameMode.getName());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (
            ConfigManager.getInstance()
                .getBooleanProperty("ui.fullscreen", false)
        ) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
        }

        animations = new ArrayList<>();
        gameBoard = new GameBoard(BOARD_SIZE, BOARD_SIZE);
        gameBoard.addObserver(this);

        String difficulty = ConfigManager.getInstance()
            .getProperty("game.default.difficulty", "MEDIUM");
        if (gameMode == GameMode.CLASSIC) {
            if ("EASY".equals(difficulty)) {
                gameBoard.setTargetScore(1000);
                gameBoard.setMovesLeft(50);
            } else if ("HARD".equals(difficulty)) {
                gameBoard.setTargetScore(2000);
                gameBoard.setMovesLeft(20);
            } else {
                gameBoard.setTargetScore(1500);
                gameBoard.setMovesLeft(30);
            }
        } else if (gameMode == GameMode.TIMED) {
            if ("EASY".equals(difficulty)) {
                secondsRemaining = 180;
                gameBoard.setTargetScore(1000);
            } else if ("HARD".equals(difficulty)) {
                secondsRemaining = 90;
                gameBoard.setTargetScore(1500);
            } else {
                secondsRemaining = 120;
                gameBoard.setTargetScore(1200);
            }
        }

        gameAI = new GameAI(gameBoard);
        difficulty = ConfigManager.getInstance()
            .getProperty("ai.autoplay.difficulty", "MEDIUM");
        if ("EASY".equals(difficulty)) {
            gameAI.setDifficulty(GameAI.Difficulty.EASY);
        } else if ("HARD".equals(difficulty)) {
            gameAI.setDifficulty(GameAI.Difficulty.HARD);
        } else {
            gameAI.setDifficulty(GameAI.Difficulty.MEDIUM);
        }

        hintCount = 0;

        initializeUI();

        if (AudioManager.getInstance().isMusicEnabled()) {
            AudioManager.getInstance().startBackgroundMusic();
        }

        if (gameMode == GameMode.TIMED) {
            startGameTimer();
        }
    }

    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(
            ThemeManager.getInstance().getBackgroundColor()
        );
        setContentPane(mainPanel);

        gameBoardPanel = createGameBoardPanel();
        mainPanel.add(gameBoardPanel, BorderLayout.CENTER);

        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        JPanel controlsPanel = createControlsPanel();
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.EAST);

        addKeyBindings();
    }

    /**
     * Create the game board panel
     */
    private JPanel createGameBoardPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(null);

        int totalSize = BOARD_SIZE * CANDY_SIZE + 2 * BOARD_PADDING;
        panel.setPreferredSize(new Dimension(totalSize, totalSize));

        panel.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (isPaused || isAnimating) return;

                    int col = (e.getX() - BOARD_PADDING) / CANDY_SIZE;
                    int row = (e.getY() - BOARD_PADDING) / CANDY_SIZE;

                    if (
                        row >= 0 &&
                        row < BOARD_SIZE &&
                        col >= 0 &&
                        col < BOARD_SIZE
                    ) {
                        gameBoard.selectCandy(row, col);
                    }
                }
            }
        );

        panel.setOpaque(false);

        panel = new GlassPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGameBoard(g);
            }
        };

        return panel;
    }

    /**
     * Draw the game board
     */
    private void drawGameBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.setColor(new Color(255, 255, 255, 80));
        for (int i = 0; i <= BOARD_SIZE; i++) {
            int pos = BOARD_PADDING + i * CANDY_SIZE;
            g2d.drawLine(
                BOARD_PADDING,
                pos,
                BOARD_PADDING + BOARD_SIZE * CANDY_SIZE,
                pos
            );
            g2d.drawLine(
                pos,
                BOARD_PADDING,
                pos,
                BOARD_PADDING + BOARD_SIZE * CANDY_SIZE
            );
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Candy candy = gameBoard.getCandyAt(row, col);
                if (candy != null) {
                    drawCandy(g2d, row, col, candy);
                }
            }
        }

        for (CandyAnimation animation : animations) {
            int x =
                BOARD_PADDING +
                animation.col * CANDY_SIZE +
                (int) animation.offsetX;
            int y =
                BOARD_PADDING +
                animation.row * CANDY_SIZE +
                (int) animation.offsetY;

            AlphaComposite ac = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                animation.alpha
            );
            g2d.setComposite(ac);

            g2d.setColor(animation.color);
            g2d.fillOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);

            g2d.setComposite(AlphaComposite.SrcOver);
        }
    }

    /**
     * Draw a candy on the board
     */
    private void drawCandy(Graphics2D g2d, int row, int col, Candy candy) {
        int x = BOARD_PADDING + col * CANDY_SIZE;
        int y = BOARD_PADDING + row * CANDY_SIZE;

        float animOffset = candy.getAnimationOffset();
        if (animOffset != 0) {
            y -= animOffset * CANDY_SIZE;
        }

        Color candyColor;
        switch (candy.getCandyColor()) {
            case RED:
                candyColor = Color.RED;
                break;
            case BLUE:
                candyColor = Color.BLUE;
                break;
            case GREEN:
                candyColor = Color.GREEN;
                break;
            case YELLOW:
                candyColor = Color.YELLOW;
                break;
            default:
                candyColor = Color.GRAY;
        }

        if (candy.isSelected()) {
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillRect(x, y, CANDY_SIZE, CANDY_SIZE);
        }

        g2d.setColor(candyColor);

        switch (candy.getType()) {
            case SIMPLE:
                g2d.fillOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                break;
            case ROW_STRIPED:
                g2d.fillOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);

                for (int i = 0; i < 5; i++) {
                    int stripeY = y + 10 + i * 8;
                    g2d.drawLine(x + 10, stripeY, x + CANDY_SIZE - 10, stripeY);
                }
                break;
            case COLUMN_STRIPED:
                g2d.fillOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);

                for (int i = 0; i < 5; i++) {
                    int stripeX = x + 10 + i * 8;
                    g2d.drawLine(stripeX, y + 10, stripeX, y + CANDY_SIZE - 10);
                }
                break;
            case WRAPPED:
                g2d.fillOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(x + 5, y + 5, CANDY_SIZE - 10, CANDY_SIZE - 10);
                g2d.drawOval(x + 10, y + 10, CANDY_SIZE - 20, CANDY_SIZE - 20);
                break;
        }

        if (candy.isExploding()) {
            g2d.setColor(new Color(255, 255, 255, 180));
            for (int i = 0; i < 8; i++) {
                double angle = (i * Math.PI) / 4;
                int startX = x + CANDY_SIZE / 2;
                int startY = y + CANDY_SIZE / 2;
                int endX = (int) (startX + 20 * Math.cos(angle));
                int endY = (int) (startY + 20 * Math.sin(angle));
                g2d.drawLine(startX, startY, endX, endY);
            }
        }
    }

    /**
     * Create the status panel that shows score and other game info
     */
    private JPanel createStatusPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel scorePanel = new JPanel(new BorderLayout(5, 0));
        scorePanel.setOpaque(false);
        JLabel scoreTitleLabel = new JLabel("Score:");
        scoreTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scorePanel.add(scoreTitleLabel, BorderLayout.WEST);
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

        JPanel targetPanel = new JPanel(new BorderLayout(5, 0));
        targetPanel.setOpaque(false);
        JLabel targetTitleLabel = new JLabel("Target:");
        targetTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        targetLabel = new JLabel(String.valueOf(gameBoard.getTargetScore()));
        targetLabel.setFont(new Font("Arial", Font.BOLD, 20));
        targetPanel.add(targetTitleLabel, BorderLayout.WEST);
        targetPanel.add(targetLabel, BorderLayout.CENTER);

        JPanel movesPanel = new JPanel(new BorderLayout(5, 0));
        movesPanel.setOpaque(false);

        if (gameMode == GameMode.TIMED) {
            JLabel timeTitleLabel = new JLabel("Time:");
            timeTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            timeLabel = new JLabel(formatTime(secondsRemaining));
            timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
            movesPanel.add(timeTitleLabel, BorderLayout.WEST);
            movesPanel.add(timeLabel, BorderLayout.CENTER);
        } else {
            JLabel movesTitleLabel = new JLabel("Moves:");
            movesTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            movesLabel = new JLabel(String.valueOf(gameBoard.getMovesLeft()));
            movesLabel.setFont(new Font("Arial", Font.BOLD, 20));
            movesPanel.add(movesTitleLabel, BorderLayout.WEST);
            movesPanel.add(movesLabel, BorderLayout.CENTER);
        }

        progressBar = new JProgressBar(0, gameBoard.getTargetScore());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setForeground(new Color(220, 20, 60));

        panel.add(scorePanel);
        panel.add(targetPanel);
        panel.add(movesPanel);
        panel.add(progressBar);

        return panel;
    }

    /**
     * Create the controls panel with game buttons
     */
    private JPanel createControlsPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new GridLayout(1, 4, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        menuButton = new FancyButton("Main Menu");
        resetButton = new FancyButton("Reset Game");
        pauseButton = new FancyButton("Pause");
        hintButton = new FancyButton("Hint (" + (hintLimit - hintCount) + ")");

        menuButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            showMainMenuConfirmation();
        });

        resetButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            resetGame();
        });

        pauseButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            togglePause();
        });

        hintButton.addActionListener(e -> {
            if (hintCount < hintLimit) {
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
                showHint();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "You've used all your hints for this game!",
                    "No Hints Left",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        panel.add(menuButton);
        panel.add(resetButton);
        panel.add(pauseButton);
        panel.add(hintButton);

        return panel;
    }

    /**
     * Create the sidebar panel with game information and options
     */
    private JPanel createSidebarPanel() {
        JPanel panel = new GlassPanel();
        panel.setLayout(new BorderLayout(0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 0));

        JPanel gameModePanel = new JPanel(new BorderLayout(0, 10));
        gameModePanel.setOpaque(false);
        JLabel gameModeLabel = new JLabel(gameMode.getName());
        gameModeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gameModeLabel.setHorizontalAlignment(JLabel.CENTER);

        JTextArea gameModeDescription = new JTextArea(
            gameMode.getDescription()
        );
        gameModeDescription.setEditable(false);
        gameModeDescription.setLineWrap(true);
        gameModeDescription.setWrapStyleWord(true);
        gameModeDescription.setOpaque(false);

        gameModePanel.add(gameModeLabel, BorderLayout.NORTH);
        gameModePanel.add(gameModeDescription, BorderLayout.CENTER);

        JPanel candyInfoPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        candyInfoPanel.setOpaque(false);
        candyInfoPanel.setBorder(
            BorderFactory.createTitledBorder("Candy Types")
        );

        candyInfoPanel.add(createCandyInfoRow(Candy.Type.SIMPLE, "Regular"));
        candyInfoPanel.add(
            createCandyInfoRow(Candy.Type.ROW_STRIPED, "Row Striped")
        );
        candyInfoPanel.add(
            createCandyInfoRow(Candy.Type.COLUMN_STRIPED, "Column Striped")
        );
        candyInfoPanel.add(createCandyInfoRow(Candy.Type.WRAPPED, "Wrapped"));

        JButton autoplayButton = new JButton("Auto Play");
        autoplayButton.addActionListener(e -> {
            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
                aiThread = null;
                autoplayButton.setText("Auto Play");
            } else {
                autoplayButton.setText("Stop Auto Play");
                aiThread = gameAI.startAutoPlay(300);
            }
        });

        panel.add(gameModePanel, BorderLayout.NORTH);
        panel.add(candyInfoPanel, BorderLayout.CENTER);
        panel.add(autoplayButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create a row in the candy info panel
     */
    private JPanel createCandyInfoRow(Candy.Type type, String description) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel symbolLabel = new JLabel();
        symbolLabel.setText(type.getSymbol());
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 24));
        symbolLabel.setHorizontalAlignment(JLabel.CENTER);
        symbolLabel.setPreferredSize(new Dimension(40, 30));

        JLabel descLabel = new JLabel(
            description + " (" + type.getScore() + " pts)"
        );
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(symbolLabel, BorderLayout.WEST);
        panel.add(descLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Add key bindings for keyboard controls
     */
    private void addKeyBindings() {
        JComponent contentPane = (JComponent) getContentPane();
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = contentPane.getInputMap(condition);
        ActionMap actionMap = contentPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
        actionMap.put(
            "pause",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    togglePause();
                }
            }
        );

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hint");
        actionMap.put(
            "hint",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (hintCount < hintLimit) {
                        showHint();
                    }
                }
            }
        );

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        actionMap.put(
            "reset",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetGame();
                }
            }
        );

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "fullscreen");
        actionMap.put(
            "fullscreen",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleFullscreen();
                }
            }
        );
    }

    /**
     * Toggle fullscreen mode
     */
    private void toggleFullscreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
            setUndecorated(false);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
        }
    }

    /**
     * Start the game timer for timed mode
     */
    private void startGameTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    if (!isPaused) {
                        secondsRemaining--;

                        SwingUtilities.invokeLater(() -> {
                            if (timeLabel != null) {
                                timeLabel.setText(formatTime(secondsRemaining));

                                if (secondsRemaining <= 10) {
                                    timeLabel.setForeground(Color.RED);
                                }
                            }
                        });

                        if (secondsRemaining <= 0) {
                            gameTimer.cancel();

                            SwingUtilities.invokeLater(() -> {
                                endGame(
                                    gameBoard.getScore() >=
                                    gameBoard.getTargetScore()
                                );
                            });
                        }
                    }
                }
            },
            1000,
            1000
        );
    }

    /**
     * Format time as MM:SS
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Show hint by highlighting a possible move
     */
    private void showHint() {
        if (isPaused || isAnimating) return;

        int[] move = gameAI.findBestMove();
        if (move != null) {
            Candy candy1 = gameBoard.getCandyAt(move[0], move[1]);
            Candy candy2 = gameBoard.getCandyAt(move[2], move[3]);

            if (candy1 != null && candy2 != null) {
                Color hintColor = new Color(255, 255, 100, 180);

                final int[] pulseCount = { 0 };
                final javax.swing.Timer[] pulseTimer = { null };

                pulseTimer[0] = new javax.swing.Timer(
                    300,
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            candy1.setSelected(!candy1.isSelected());
                            candy2.setSelected(!candy2.isSelected());
                            gameBoardPanel.repaint();

                            pulseCount[0]++;
                            if (pulseCount[0] >= 6) {
                                pulseTimer[0].stop();
                                candy1.setSelected(false);
                                candy2.setSelected(false);
                                gameBoardPanel.repaint();
                            }
                        }
                    }
                );

                pulseTimer[0].start();

                hintCount++;
                hintButton.setText("Hint (" + (hintLimit - hintCount) + ")");

                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_BUTTON_CLICK);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No valid moves found! Try shuffling the board.",
                "No Hints Available",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Toggle pause state
     */
    private void togglePause() {
        isPaused = !isPaused;

        if (isPaused) {
            pauseButton.setText("Resume");
            if (gameMode == GameMode.TIMED) {
                AudioManager.getInstance().pauseBackgroundMusic();
            }

            JOptionPane.showMessageDialog(
                this,
                "Game Paused\n\nClick OK to resume",
                "Paused",
                JOptionPane.INFORMATION_MESSAGE
            );

            isPaused = false;
            pauseButton.setText("Pause");
            if (gameMode == GameMode.TIMED) {
                AudioManager.getInstance().resumeBackgroundMusic();
            }
        }
    }

    /**
     * Show main menu confirmation dialog
     */
    private void showMainMenuConfirmation() {
        if (isPaused) return;

        isPaused = true;
        pauseButton.setText("Resume");

        int response = JOptionPane.showConfirmDialog(
            this,
            "Return to main menu? Your current game progress will be lost.",
            "Return to Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            if (gameTimer != null) {
                gameTimer.cancel();
            }

            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
            }

            dispose();
            new MainMenuScreen().setVisible(true);
        } else {
            isPaused = false;
            pauseButton.setText("Pause");
        }
    }

    /**
     * Reset the game
     */
    private void resetGame() {
        if (isPaused) return;

        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the game?",
            "Reset Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            if (gameTimer != null) {
                gameTimer.cancel();
            }

            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
                aiThread = null;
            }

            gameBoard = new GameBoard(BOARD_SIZE, BOARD_SIZE);
            gameBoard.addObserver(this);
            hintCount = 0;
            hintButton.setText("Hint (" + (hintLimit - hintCount) + ")");

            gameAI = new GameAI(gameBoard);
            String difficulty = ConfigManager.getInstance()
                .getProperty("ai.autoplay.difficulty", "MEDIUM");
            if ("EASY".equals(difficulty)) {
                gameAI.setDifficulty(GameAI.Difficulty.EASY);
            } else if ("HARD".equals(difficulty)) {
                gameAI.setDifficulty(GameAI.Difficulty.HARD);
            } else {
                gameAI.setDifficulty(GameAI.Difficulty.MEDIUM);
            }

            scoreLabel.setText("0");
            progressBar.setValue(0);
            progressBar.setString("0%");

            if (gameMode == GameMode.TIMED) {
                secondsRemaining = 120;
                timeLabel.setText(formatTime(secondsRemaining));
                timeLabel.setForeground(Color.BLACK);
                startGameTimer();
            } else {
                difficulty = ConfigManager.getInstance()
                    .getProperty("game.default.difficulty", "MEDIUM");
                if ("EASY".equals(difficulty)) {
                    gameBoard.setMovesLeft(50);
                } else if ("HARD".equals(difficulty)) {
                    gameBoard.setMovesLeft(20);
                } else {
                    gameBoard.setMovesLeft(30);
                }
                movesLabel.setText(String.valueOf(gameBoard.getMovesLeft()));
            }

            gameBoardPanel.repaint();

            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_LEVEL_COMPLETE);
        }
    }

    /**
     * End the game and show appropriate message
     */
    private void endGame(boolean isWin) {
        isPaused = true;

        if (gameTimer != null) {
            gameTimer.cancel();
        }

        if (aiThread != null && aiThread.isAlive()) {
            aiThread.interrupt();
            aiThread = null;
        }

        if (isWin) {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_LEVEL_COMPLETE);
        } else {
            AudioManager.getInstance().playSound(AudioManager.SOUND_GAME_OVER);
        }

        String message;
        String title;

        if (isWin) {
            title = "Level Complete!";
            message =
                "Congratulations! You've reached the target score.\n\n" +
                "Your score: " +
                gameBoard.getScore() +
                "\n" +
                "Target score: " +
                gameBoard.getTargetScore();

            String playerName = JOptionPane.showInputDialog(
                this,
                "You've earned a high score! Enter your name:",
                "High Score",
                JOptionPane.PLAIN_MESSAGE
            );

            if (playerName != null && !playerName.trim().isEmpty()) {
                HighScoreDialog.addHighScore(playerName, gameBoard.getScore());
            }
        } else {
            title = "Game Over";
            message =
                "Sorry, you've run out of moves.\n\n" +
                "Your score: " +
                gameBoard.getScore() +
                "\n" +
                "Target score: " +
                gameBoard.getTargetScore();
        }

        int response = JOptionPane.showOptionDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            isWin
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.WARNING_MESSAGE,
            null,
            new String[] { "Play Again", "Return to Menu" },
            "Play Again"
        );

        if (response == 0) {
            resetGame();
            isPaused = false;
        } else {
            dispose();
            new MainMenuScreen().setVisible(true);
        }
    }

    /**
     * Start animation timer
     */
    private void startAnimationTimer() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        isAnimating = true;

        animationTimer = new javax.swing.Timer(16, e -> {
            boolean stillAnimating = false;

            Iterator<CandyAnimation> iterator = animations.iterator();
            while (iterator.hasNext()) {
                CandyAnimation animation = iterator.next();
                animation.update();

                if (animation.isFinished()) {
                    iterator.remove();
                } else {
                    stillAnimating = true;
                }
            }

            gameBoardPanel.repaint();

            if (!stillAnimating) {
                isAnimating = false;
                animationTimer.stop();
            }
        });

        animationTimer.start();
    }

    /**
     * Handle updates from the game board model
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameBoard) {
            scoreLabel.setText(String.valueOf(gameBoard.getScore()));

            int score = gameBoard.getScore();
            int targetScore = gameBoard.getTargetScore();
            int progress = Math.min(100, (score * 100) / targetScore);
            progressBar.setValue(score);
            progressBar.setString(progress + "%");

            if (gameMode != GameMode.TIMED && movesLabel != null) {
                movesLabel.setText(String.valueOf(gameBoard.getMovesLeft()));
            }

            if (arg instanceof String) {
                String updateType = (String) arg;

                switch (updateType) {
                    case "match":
                        List<Candy> matches = gameBoard.getPendingMatches();
                        for (Candy candy : matches) {
                            Color color = getColorForCandy(candy);
                            CandyAnimation animation = new CandyAnimation(
                                candy.getRow(),
                                candy.getColumn(),
                                0,
                                0,
                                color
                            );
                            animation.isRemoving = true;
                            animations.add(animation);
                        }
                        startAnimationTimer();
                        break;
                    case "remove":
                        animations.clear();
                        break;
                    case "collapse":
                        break;
                    case "win":
                        endGame(true);
                        break;
                    case "lose":
                    case "no_moves":
                        endGame(false);
                        break;
                }
            }

            gameBoardPanel.repaint();
        }
    }

    /**
     * Get the color for a candy
     */
    private Color getColorForCandy(Candy candy) {
        if (candy == null) return Color.GRAY;

        switch (candy.getCandyColor()) {
            case RED:
                return Color.RED;
            case BLUE:
                return Color.BLUE;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            default:
                return Color.GRAY;
        }
    }
}
