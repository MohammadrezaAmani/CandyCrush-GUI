package candycrush.model;

import candycrush.ai.GameAI;
import candycrush.audio.AudioManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the game board for Candy Crush.
 * Handles game logic, candy matching, and board state.
 * Implements Observable pattern to notify views of changes.
 */
public class GameBoard extends Observable {

    private static final Logger LOGGER = Logger.getLogger(
        GameBoard.class.getName()
    );

    private final int rows;
    private final int cols;

    private Candy[][] board;
    private int score;
    private int targetScore;
    private int movesLeft;
    private GameMode gameMode;
    private boolean gameOver;
    private boolean gameWon;

    private Candy selectedCandy;
    private List<Candy> pendingMatches;

    private GameAI ai;

    public enum GameMode {
        CLASSIC,
        TIMED,
        PUZZLE,
        MULTIPLAYER,
    }

    /**
     * Create a new game board with default settings
     */
    public GameBoard() {
        this(10, 10);
    }

    /**
     * Create a new game board with specified dimensions
     *
     * @param rows Number of rows
     * @param cols Number of columns
     */
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Candy[rows][cols];
        this.pendingMatches = new ArrayList<>();
        this.gameMode = GameMode.CLASSIC;
        this.targetScore = 1500;
        this.movesLeft = 30;
        this.gameOver = false;
        this.gameWon = false;

        initializeBoard();
    }

    /**
     * Initialize the board with random candies
     */
    private void initializeBoard() {
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = createRandomCandy(i, j, random);
            }
        }

        while (findAllMatches().size() > 0) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    board[i][j] = createRandomCandy(i, j, random);
                }
            }
        }
    }

    /**
     * Create a random candy
     *
     * @param row Row position
     * @param col Column position
     * @param random Random generator
     * @return New candy instance
     */
    private Candy createRandomCandy(int row, int col, Random random) {
        Candy.CandyColor[] colors = Candy.CandyColor.values();
        Candy.CandyColor randomColor = colors[random.nextInt(colors.length)];
        Candy candy = new Candy(randomColor);
        candy.setPosition(row, col);
        return candy;
    }

    /**
     * Get candy at specified position
     *
     * @param row Row position
     * @param col Column position
     * @return Candy at position or null if position is invalid
     */
    public Candy getCandyAt(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }

    /**
     * Check if position is valid
     *
     * @param row Row position
     * @param col Column position
     * @return true if position is on the board
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Select a candy
     *
     * @param row Row position
     * @param col Column position
     * @return true if selection was successful
     */
    public boolean selectCandy(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }

        Candy candy = board[row][col];

        if (selectedCandy == null) {
            selectedCandy = candy;
            candy.setSelected(true);
            AudioManager.getInstance().playSound(AudioManager.SOUND_SELECT);

            setChanged();
            notifyObservers();
            return true;
        } else if (candy == selectedCandy) {
            selectedCandy.setSelected(false);
            selectedCandy = null;

            setChanged();
            notifyObservers();
            return true;
        } else if (isAdjacent(selectedCandy, candy)) {
            return swapCandies(selectedCandy, candy);
        } else {
            selectedCandy.setSelected(false);
            selectedCandy = candy;
            candy.setSelected(true);
            AudioManager.getInstance().playSound(AudioManager.SOUND_SELECT);

            setChanged();
            notifyObservers();
            return true;
        }
    }

    /**
     * Check if two candies are adjacent
     *
     * @param candy1 First candy
     * @param candy2 Second candy
     * @return true if candies are adjacent
     */
    private boolean isAdjacent(Candy candy1, Candy candy2) {
        int rowDiff = Math.abs(candy1.getRow() - candy2.getRow());
        int colDiff = Math.abs(candy1.getColumn() - candy2.getColumn());

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    /**
     * Swap two candies and check for matches
     *
     * @param candy1 First candy
     * @param candy2 Second candy
     * @return true if swap was successful
     */
    public boolean swapCandies(Candy candy1, Candy candy2) {
        int row1 = candy1.getRow();
        int col1 = candy1.getColumn();
        int row2 = candy2.getRow();
        int col2 = candy2.getColumn();

        board[row1][col1] = candy2;
        board[row2][col2] = candy1;
        candy1.setPosition(row2, col2);
        candy2.setPosition(row1, col1);

        candy1.setSelected(false);
        selectedCandy = null;

        List<Candy> matches = findAllMatches();

        if (matches.isEmpty()) {
            board[row1][col1] = candy1;
            board[row2][col2] = candy2;
            candy1.setPosition(row1, col1);
            candy2.setPosition(row2, col2);

            setChanged();
            notifyObservers("invalid_move");
            return false;
        }

        AudioManager.getInstance().playSound(AudioManager.SOUND_MATCH);
        decreaseMoves();

        processMatches(matches);

        return true;
    }

    /**
     * Find all matching candies on the board
     *
     * @return List of candies that are part of matches
     */
    public List<Candy> findAllMatches() {
        List<Candy> matches = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 2; j++) {
                Candy c1 = board[i][j];
                Candy c2 = board[i][j + 1];
                Candy c3 = board[i][j + 2];

                if (
                    c1 != null &&
                    c2 != null &&
                    c3 != null &&
                    c1.isSameColor(c2) &&
                    c1.isSameColor(c3)
                ) {
                    int matchLength = 3;

                    while (j + matchLength < cols) {
                        Candy nextCandy = board[i][j + matchLength];
                        if (nextCandy != null && c1.isSameColor(nextCandy)) {
                            matchLength++;
                        } else {
                            break;
                        }
                    }

                    for (int k = 0; k < matchLength; k++) {
                        if (!matches.contains(board[i][j + k])) {
                            matches.add(board[i][j + k]);
                        }
                    }

                    j += matchLength - 1;
                }
            }
        }

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 2; i++) {
                Candy c1 = board[i][j];
                Candy c2 = board[i + 1][j];
                Candy c3 = board[i + 2][j];

                if (
                    c1 != null &&
                    c2 != null &&
                    c3 != null &&
                    c1.isSameColor(c2) &&
                    c1.isSameColor(c3)
                ) {
                    int matchLength = 3;

                    while (i + matchLength < rows) {
                        Candy nextCandy = board[i + matchLength][j];
                        if (nextCandy != null && c1.isSameColor(nextCandy)) {
                            matchLength++;
                        } else {
                            break;
                        }
                    }

                    for (int k = 0; k < matchLength; k++) {
                        if (!matches.contains(board[i + k][j])) {
                            matches.add(board[i + k][j]);
                        }
                    }

                    i += matchLength - 1;
                }
            }
        }

        return matches;
    }

    /**
     * Process matches by removing candies and creating special candies
     *
     * @param matches List of candies to process
     */
    private void processMatches(List<Candy> matches) {
        Candy specialCandy = determineSpecialCandy(matches);

        for (Candy candy : matches) {
            score += candy.getScore();

            candy.setExploding(true);
        }

        pendingMatches.addAll(matches);
        setChanged();
        notifyObservers("match");

        new java.util.Timer()
            .schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        removeMatches(matches, specialCandy);
                        collapseBoard();
                        fillEmptySpaces();
                        checkGameState();
                    }
                },
                500
            );
    }

    /**
     * Determine what type of special candy to create based on match pattern
     *
     * @param matches List of matching candies
     * @return Special candy to create, or null if no special candy
     */
    private Candy determineSpecialCandy(List<Candy> matches) {
        if (matches.size() < 4) {
            return null;
        }

        boolean isRowMatch = true;
        boolean isColumnMatch = true;

        int firstRow = matches.get(0).getRow();
        int firstCol = matches.get(0).getColumn();

        for (int i = 1; i < matches.size(); i++) {
            Candy candy = matches.get(i);
            if (candy.getRow() != firstRow) {
                isRowMatch = false;
            }
            if (candy.getColumn() != firstCol) {
                isColumnMatch = false;
            }
        }

        Candy.CandyColor color = matches.get(0).getCandyColor();
        Candy specialCandy = null;

        if (matches.size() >= 5) {
            specialCandy = new Candy(Candy.Type.WRAPPED, color);
            AudioManager.getInstance().playSound(AudioManager.SOUND_SPECIAL);
        } else if (matches.size() == 4) {
            if (isRowMatch) {
                specialCandy = new Candy(Candy.Type.ROW_STRIPED, color);
            } else if (isColumnMatch) {
                specialCandy = new Candy(Candy.Type.COLUMN_STRIPED, color);
            } else {
                specialCandy = new Candy(Candy.Type.ROW_STRIPED, color);
            }
            AudioManager.getInstance().playSound(AudioManager.SOUND_SPECIAL);
        }

        return specialCandy;
    }

    /**
     * Remove matching candies from the board
     *
     * @param matches List of candies to remove
     * @param specialCandy Special candy to create, if any
     */
    private void removeMatches(List<Candy> matches, Candy specialCandy) {
        pendingMatches.clear();

        for (Candy candy : matches) {
            int row = candy.getRow();
            int col = candy.getColumn();
            board[row][col] = null;
        }

        if (specialCandy != null && !matches.isEmpty()) {
            Candy firstCandy = matches.get(0);
            int row = firstCandy.getRow();
            int col = firstCandy.getColumn();

            specialCandy.setPosition(row, col);
            board[row][col] = specialCandy;
        }

        setChanged();
        notifyObservers("remove");
    }

    /**
     * Collapse the board after removing candies
     */
    private void collapseBoard() {
        for (int col = 0; col < cols; col++) {
            int emptyRow = -1;

            for (int row = rows - 1; row >= 0; row--) {
                if (board[row][col] == null) {
                    if (emptyRow == -1) {
                        emptyRow = row;
                    }
                } else if (emptyRow != -1) {
                    board[emptyRow][col] = board[row][col];
                    board[row][col] = null;
                    board[emptyRow][col].setPosition(emptyRow, col);

                    emptyRow--;
                    row++;
                }
            }
        }

        setChanged();
        notifyObservers("collapse");
    }

    /**
     * Fill empty spaces with new candies
     */
    private void fillEmptySpaces() {
        Random random = new Random();

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (board[row][col] == null) {
                    board[row][col] = createRandomCandy(row, col, random);

                    board[row][col].setAnimationOffset(-1.0f * (rows - row));
                }
            }
        }

        List<Candy> newMatches = findAllMatches();
        if (!newMatches.isEmpty()) {
            processMatches(newMatches);
        } else {
            setChanged();
            notifyObservers("stable");
        }
    }

    /**
     * Check for special candy effects when activated
     *
     * @param candy Candy to activate
     */
    public void activateSpecialCandy(Candy candy) {
        List<Candy> affected = new ArrayList<>();

        if (candy.getType() == Candy.Type.ROW_STRIPED) {
            int row = candy.getRow();
            for (int col = 0; col < cols; col++) {
                if (board[row][col] != null) {
                    affected.add(board[row][col]);
                }
            }
            AudioManager.getInstance().playSound(AudioManager.SOUND_SPECIAL);
        } else if (candy.getType() == Candy.Type.COLUMN_STRIPED) {
            int col = candy.getColumn();
            for (int row = 0; row < rows; row++) {
                if (board[row][col] != null) {
                    affected.add(board[row][col]);
                }
            }
            AudioManager.getInstance().playSound(AudioManager.SOUND_SPECIAL);
        } else if (candy.getType() == Candy.Type.WRAPPED) {
            int centerRow = candy.getRow();
            int centerCol = candy.getColumn();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int row = centerRow + i;
                    int col = centerCol + j;

                    if (isValidPosition(row, col) && board[row][col] != null) {
                        affected.add(board[row][col]);
                    }
                }
            }
            AudioManager.getInstance().playSound(AudioManager.SOUND_SPECIAL);
        }

        if (!affected.isEmpty()) {
            processMatches(affected);
        }
    }

    /**
     * Decrease moves left
     */
    private void decreaseMoves() {
        if (gameMode == GameMode.CLASSIC || gameMode == GameMode.PUZZLE) {
            movesLeft--;
        }
    }

    /**
     * Check game state for win/lose conditions
     */
    private void checkGameState() {
        if (score >= targetScore) {
            gameWon = true;
            gameOver = true;
            AudioManager.getInstance()
                .playSound(AudioManager.SOUND_LEVEL_COMPLETE);
            setChanged();
            notifyObservers("win");
        }

        if (gameMode == GameMode.CLASSIC || gameMode == GameMode.PUZZLE) {
            if (movesLeft <= 0 && !gameWon) {
                gameOver = true;
                AudioManager.getInstance()
                    .playSound(AudioManager.SOUND_GAME_OVER);
                setChanged();
                notifyObservers("lose");
            }
        }

        if (!hasValidMoves() && !gameOver) {
            gameOver = true;
            AudioManager.getInstance().playSound(AudioManager.SOUND_GAME_OVER);
            setChanged();
            notifyObservers("no_moves");
        }
    }

    /**
     * Check if there are valid moves available
     *
     * @return true if valid moves exist
     */
    public boolean hasValidMoves() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < cols - 1) {
                    Candy temp = board[i][j];
                    board[i][j] = board[i][j + 1];
                    board[i][j + 1] = temp;

                    boolean hasMatch = !findAllMatches().isEmpty();

                    board[i][j + 1] = board[i][j];
                    board[i][j] = temp;

                    if (hasMatch) {
                        return true;
                    }
                }

                if (i < rows - 1) {
                    Candy temp = board[i][j];
                    board[i][j] = board[i + 1][j];
                    board[i + 1][j] = temp;

                    boolean hasMatch = !findAllMatches().isEmpty();

                    board[i + 1][j] = board[i][j];
                    board[i][j] = temp;

                    if (hasMatch) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Find hints for possible moves
     *
     * @return An array with four values [row1, col1, row2, col2] representing a possible move
     */
    public int[] getHint() {
        if (ai == null) {
            ai = new GameAI(this);
        }

        return ai.findBestMove();
    }

    /**
     * Load game from file
     *
     * @param filePath File path
     * @throws IOException If file cannot be read
     */
    public void loadFromFile(String filePath) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            this.board = new Candy[rows][cols];

            String scoreLine = reader.readLine();
            if (scoreLine != null) {
                try {
                    this.score = Integer.parseInt(scoreLine.trim());
                } catch (NumberFormatException e) {
                    LOGGER.log(
                        Level.WARNING,
                        "Invalid score in file: " + scoreLine
                    );
                    this.score = 0;
                }
            }

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("File format error: not enough rows");
                }

                String[] candyStrings = line.split(",");
                if (candyStrings.length < cols) {
                    throw new IOException(
                        "File format error: not enough columns in row " + i
                    );
                }

                for (int j = 0; j < cols; j++) {
                    String candyString = candyStrings[j].trim();
                    Candy candy = Candy.fromFileString(candyString);
                    candy.setPosition(i, j);
                    board[i][j] = candy;
                }
            }

            selectedCandy = null;
            pendingMatches.clear();
            gameOver = false;
            gameWon = false;

            setChanged();
            notifyObservers("load");
        } catch (IOException e) {
            LOGGER.log(
                Level.SEVERE,
                "Error loading game from file: " + filePath,
                e
            );
            throw e;
        }
    }

    /**
     * Save game to file
     *
     * @param filePath File path
     * @throws IOException If file cannot be written
     */
    public void saveToFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(String.valueOf(score) + "\n");

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Candy candy = board[i][j];
                    writer.write(candy.toFileString());

                    if (j < cols - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            LOGGER.log(
                Level.SEVERE,
                "Error saving game to file: " + filePath,
                e
            );
            throw e;
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int targetScore) {
        this.targetScore = targetScore;
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public List<Candy> getPendingMatches() {
        return pendingMatches;
    }
}
