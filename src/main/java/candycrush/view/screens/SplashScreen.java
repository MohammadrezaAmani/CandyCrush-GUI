package candycrush.view.screens;

import candycrush.audio.AudioManager;
import candycrush.util.ResourceLoader;
import candycrush.util.ThemeManager;
import candycrush.view.screens.MainMenuScreen;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;
import javax.swing.*;

/**
 * Splash screen displayed when the game is starting.
 * Shows loading progress and transitions to the main menu.
 */
public class SplashScreen extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Timer loadingTimer;
    private JPanel candyPanel;
    private Random random;

    /**
     * Create a new splash screen
     */
    public SplashScreen() {
        setTitle("Candy Crush");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 20, 20));

        ThemeManager themeManager = ThemeManager.getInstance();
        Color bgColor = themeManager.getColor("CANDY_BACKGROUND");
        if (bgColor == null) {
            bgColor = new Color(255, 230, 250);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

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

        random = new Random();
        candyPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCandies(g);
            }
        };
        candyPanel.setOpaque(false);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(220, 20, 60));
        progressBar.setBackground(new Color(255, 240, 255));
        progressBar.setPreferredSize(new Dimension(WIDTH - 100, 30));

        statusLabel = new JLabel("Loading...");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel progressPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        progressPanel.setOpaque(false);
        progressPanel.add(progressBar);
        progressPanel.add(statusLabel);

        mainPanel.add(logoLabel, BorderLayout.NORTH);
        mainPanel.add(candyPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        startLoading();
    }

    /**
     * Draw animated candy symbols in the background
     */
    private void drawCandies(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        long time = System.currentTimeMillis() % 10000;

        for (int i = 0; i < 20; i++) {
            int x = (i * 73 + (int) (time / 50)) % WIDTH;
            int y = (i * 37 + (int) (time / 30)) % HEIGHT;

            int size = 20 + (i % 3) * 10;
            int colorIndex = (i + (int) (time / 1000)) % 4;

            Color color;
            switch (colorIndex) {
                case 0:
                    color = Color.RED;
                    break;
                case 1:
                    color = Color.BLUE;
                    break;
                case 2:
                    color = Color.GREEN;
                    break;
                default:
                    color = Color.YELLOW;
            }

            g2d.setColor(color);

            int symbolType = i % 4;
            if (symbolType == 0) {
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(x, y, size, size);
            } else if (symbolType == 1) {
                g2d.fillRect(x, y, size, size);
                g2d.setColor(Color.WHITE);
                for (int j = 0; j < size; j += 4) {
                    g2d.drawLine(x, y + j, x + size, y + j);
                }
            } else if (symbolType == 2) {
                g2d.fillRoundRect(x, y, size, size, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(x, y, size, size, 10, 10);
                g2d.drawRoundRect(x + 4, y + 4, size - 8, size - 8, 6, 6);
            } else {
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(x, y + size / 2, x + size, y + size / 2);
                g2d.drawLine(x + size / 2, y, x + size / 2, y + size);
                g2d.drawOval(x, y, size, size);
            }
        }
    }

    /**
     * Simulate loading progress with a timer
     */
    private void startLoading() {
        final int[] progress = { 0 };
        final String[] loadingMessages = {
            "Loading resources...",
            "Preparing candies...",
            "Setting up game board...",
            "Loading sound effects...",
            "Mixing the colors...",
            "Almost ready...",
            "Get ready to crush some candies!",
        };

        loadingTimer = new Timer(
            50,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    progress[0] += 1 + random.nextInt(2);
                    progressBar.setValue(Math.min(progress[0], 100));

                    int messageIndex =
                        (progress[0] * loadingMessages.length) / 100;
                    messageIndex = Math.min(
                        messageIndex,
                        loadingMessages.length - 1
                    );
                    statusLabel.setText(loadingMessages[messageIndex]);

                    candyPanel.repaint();

                    if (progress[0] >= 100) {
                        loadingTimer.stop();
                        finishLoading();
                    }
                }
            }
        );

        loadingTimer.start();
    }

    /**
     * Finish loading and show the main menu
     */
    private void finishLoading() {
        AudioManager.getInstance().playSound(AudioManager.SOUND_LEVEL_COMPLETE);

        statusLabel.setText("Loading Complete!");

        Timer transitionTimer = new Timer(
            1000,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventQueue.invokeLater(() -> {
                        MainMenuScreen mainMenu = new MainMenuScreen();
                        mainMenu.setVisible(true);
                        dispose();
                    });
                }
            }
        );
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }
}
