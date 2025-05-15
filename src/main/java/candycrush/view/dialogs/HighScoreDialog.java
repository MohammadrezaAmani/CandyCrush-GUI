package candycrush.view.dialogs;

import candycrush.audio.AudioManager;
import candycrush.util.ResourceLoader;
import candycrush.util.ThemeManager;
import candycrush.view.components.FancyButton;
import candycrush.view.components.GlassPanel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for displaying high scores in the game.
 * Shows a list of the top scores achieved by players.
 */
public class HighScoreDialog extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(
        HighScoreDialog.class.getName()
    );
    private static final String HIGH_SCORES_FILE =
        System.getProperty("user.home") + "/.candycrush/highscores.dat";
    private static final int MAX_SCORES = 10;

    private JTable scoresTable;
    private DefaultTableModel tableModel;
    private List<ScoreEntry> highScores;

    /**
     * Create a new high score dialog
     *
     * @param parent Parent frame
     */
    public HighScoreDialog(JFrame parent) {
        super(parent, "High Scores", true);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        highScores = loadHighScores();

        initializeUI();
    }

    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        JPanel contentPanel = new GlassPanel();
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPanel);

        JLabel titleLabel = new JLabel("High Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 20, 60));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Rank");
        tableModel.addColumn("Player");
        tableModel.addColumn("Score");
        tableModel.addColumn("Date");

        scoresTable = new JTable(tableModel);
        scoresTable.setRowHeight(30);
        scoresTable.setShowGrid(false);
        scoresTable.setOpaque(false);
        scoresTable.setBackground(new Color(0, 0, 0, 0));
        scoresTable.setForeground(Color.BLACK);
        scoresTable.setSelectionBackground(new Color(220, 20, 60, 80));
        scoresTable.setSelectionForeground(Color.BLACK);
        scoresTable.setFont(new Font("Arial", Font.PLAIN, 14));
        scoresTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        scoresTable.getTableHeader().setOpaque(false);
        scoresTable.getTableHeader().setBackground(new Color(220, 20, 60));
        scoresTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer =
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
                ) {
                    Component c = super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                    );
                    c.setBackground(new Color(0, 0, 0, 0));
                    return c;
                }
            };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < scoresTable.getColumnCount(); i++) {
            scoresTable
                .getColumnModel()
                .getColumn(i)
                .setCellRenderer(centerRenderer);
        }

        scoresTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scoresTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        scoresTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scoresTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel decorationPanel = new JPanel(new BorderLayout());
        decorationPanel.setOpaque(false);

        try {
            ImageIcon trophyIcon = ResourceLoader.getInstance()
                .getImageIcon("trophy.png", 100, 100);
            if (trophyIcon != null) {
                JLabel trophyLabel = new JLabel(trophyIcon);
                trophyLabel.setHorizontalAlignment(JLabel.CENTER);
                decorationPanel.add(trophyLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load trophy image", e);
        }

        FancyButton closeButton = new FancyButton("Close");
        closeButton.addActionListener(e -> {
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_BUTTON_CLICK);
            dispose();
        });

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(closeButton, BorderLayout.SOUTH);
        contentPanel.add(decorationPanel, BorderLayout.EAST);

        populateTable();
    }

    /**
     * Populate the table with high scores
     */
    private void populateTable() {
        tableModel.setRowCount(0);

        for (int i = 0; i < highScores.size(); i++) {
            ScoreEntry entry = highScores.get(i);
            tableModel.addRow(
                new Object[] {
                    (i + 1) + ".",
                    entry.playerName,
                    entry.score,
                    entry.date,
                }
            );
        }

        for (int i = highScores.size(); i < MAX_SCORES; i++) {
            tableModel.addRow(new Object[] { (i + 1) + ".", "-", "-", "-" });
        }
    }

    /**
     * Load high scores from file
     *
     * @return List of high score entries
     */
    @SuppressWarnings("unchecked")
    private List<ScoreEntry> loadHighScores() {
        List<ScoreEntry> scores = new ArrayList<>();

        try {
            File file = new File(HIGH_SCORES_FILE);
            if (file.exists()) {
                try (
                    ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file)
                    )
                ) {
                    scores = (List<ScoreEntry>) ois.readObject();
                }
            } else {
                createSampleScores(scores);
                saveHighScores(scores);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading high scores", e);

            createSampleScores(scores);
        }

        return scores;
    }

    /**
     * Create sample scores for first run
     *
     * @param scores List to populate with sample scores
     */
    private void createSampleScores(List<ScoreEntry> scores) {
        scores.add(new ScoreEntry("Champion", 2500, "2023-01-15"));
        scores.add(new ScoreEntry("CandyCrusher", 2100, "2023-01-10"));
        scores.add(new ScoreEntry("SweetTooth", 1800, "2023-01-05"));
        scores.add(new ScoreEntry("Player1", 1500, "2023-01-01"));
        scores.add(new ScoreEntry("SugarRush", 1200, "2022-12-25"));
    }

    /**
     * Save high scores to file
     *
     * @param scores List of high score entries to save
     */
    private void saveHighScores(List<ScoreEntry> scores) {
        try {
            File dir = new File(
                System.getProperty("user.home") + "/.candycrush"
            );
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (
                ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(HIGH_SCORES_FILE)
                )
            ) {
                oos.writeObject(scores);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error saving high scores", e);
        }
    }

    /**
     * Add a new high score
     *
     * @param playerName Player name
     * @param score Score value
     * @return true if the score was high enough to be added
     */
    public static boolean addHighScore(String playerName, int score) {
        HighScoreDialog dialog = new HighScoreDialog(null);
        List<ScoreEntry> scores = dialog.highScores;

        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(
            new java.util.Date()
        );

        if (
            scores.size() < MAX_SCORES ||
            score > scores.get(scores.size() - 1).score
        ) {
            ScoreEntry newEntry = new ScoreEntry(playerName, score, date);

            scores.add(newEntry);

            Collections.sort(scores, (a, b) -> Integer.compare(b.score, a.score)
            );

            if (scores.size() > MAX_SCORES) {
                scores = scores.subList(0, MAX_SCORES);
            }

            dialog.saveHighScores(scores);
            return true;
        }

        return false;
    }

    /**
     * Class representing a high score entry
     */
    private static class ScoreEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        String playerName;
        int score;
        String date;

        public ScoreEntry(String playerName, int score, String date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }
    }
}
