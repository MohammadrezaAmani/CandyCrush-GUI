package candycrush.ai;

import candycrush.model.Candy;
import candycrush.model.GameBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AI implementation for Candy Crush game.
 * Provides functionality for finding optimal moves, generating hints,
 * and automatic gameplay.
 */
public class GameAI {

    private static final Logger LOGGER = Logger.getLogger(
        GameAI.class.getName()
    );

    private final GameBoard gameBoard;
    private AIStrategy strategy;
    private Random random;

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD,
    }

    public interface AIStrategy {
        int[] findMove(GameBoard board);
    }

    /**
     * Create a new GameAI with the specified game board
     *
     * @param gameBoard The game board to analyze
     */
    public GameAI(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.random = new Random();
        this.strategy = new OptimalMatchStrategy();
    }

    /**
     * Find the best move according to current strategy
     *
     * @return An array with four values [row1, col1, row2, col2] representing a move,
     *         or null if no move is found
     */
    public int[] findBestMove() {
        try {
            return strategy.findMove(gameBoard);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error finding best move", e);
            return findRandomValidMove();
        }
    }

    /**
     * Set the AI difficulty level
     *
     * @param difficulty Difficulty level
     */
    public void setDifficulty(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                this.strategy = new RandomValidMoveStrategy();
                break;
            case MEDIUM:
                this.strategy = new BasicMatchStrategy();
                break;
            case HARD:
                this.strategy = new OptimalMatchStrategy();
                break;
        }
    }

    /**
     * Find a random valid move on the board
     *
     * @return An array with four values [row1, col1, row2, col2] representing a move,
     *         or null if no move is found
     */
    private int[] findRandomValidMove() {
        List<int[]> validMoves = new ArrayList<>();

        int rows = gameBoard.getRows();
        int cols = gameBoard.getCols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < cols - 1 && isValidSwap(i, j, i, j + 1)) {
                    validMoves.add(new int[] { i, j, i, j + 1 });
                }

                if (i < rows - 1 && isValidSwap(i, j, i + 1, j)) {
                    validMoves.add(new int[] { i, j, i + 1, j });
                }
            }
        }

        if (!validMoves.isEmpty()) {
            return validMoves.get(random.nextInt(validMoves.size()));
        }

        return null;
    }

    /**
     * Check if swapping two candies would result in a match
     *
     * @param row1 Row of first candy
     * @param col1 Column of first candy
     * @param row2 Row of second candy
     * @param col2 Column of second candy
     * @return true if the swap would create a match
     */
    private boolean isValidSwap(int row1, int col1, int row2, int col2) {
        Candy candy1 = gameBoard.getCandyAt(row1, col1);
        Candy candy2 = gameBoard.getCandyAt(row2, col2);

        if (candy1 == null || candy2 == null) {
            return false;
        }

        Candy.CandyColor tempColor = candy1.getCandyColor();
        candy1.setCandyColor(candy2.getCandyColor());
        candy2.setCandyColor(tempColor);

        boolean hasMatch = !gameBoard.findAllMatches().isEmpty();

        candy2.setCandyColor(candy1.getCandyColor());
        candy1.setCandyColor(tempColor);

        return hasMatch;
    }

    /**
     * Score a potential move based on match pattern and candy types
     *
     * @param row1 Row of first candy
     * @param col1 Column of first candy
     * @param row2 Row of second candy
     * @param col2 Column of second candy
     * @return Score for this move (higher is better)
     */
    private int scoreMove(int row1, int col1, int row2, int col2) {
        Candy candy1 = gameBoard.getCandyAt(row1, col1);
        Candy candy2 = gameBoard.getCandyAt(row2, col2);

        if (candy1 == null || candy2 == null) {
            return 0;
        }

        Candy.CandyColor tempColor = candy1.getCandyColor();
        candy1.setCandyColor(candy2.getCandyColor());
        candy2.setCandyColor(tempColor);

        List<Candy> matches = gameBoard.findAllMatches();
        int score = matches.size() * 10;

        for (Candy candy : matches) {
            if (candy.getType() != Candy.Type.SIMPLE) {
                score += 20;
            }
        }

        if (matches.size() >= 4) {
            score += 50;
        }

        candy2.setCandyColor(candy1.getCandyColor());
        candy1.setCandyColor(tempColor);

        return score;
    }

    /**
     * Strategy that picks completely random valid moves
     */
    private class RandomValidMoveStrategy implements AIStrategy {

        @Override
        public int[] findMove(GameBoard board) {
            return findRandomValidMove();
        }
    }

    /**
     * Strategy that finds the first valid move it encounters
     */
    private class BasicMatchStrategy implements AIStrategy {

        @Override
        public int[] findMove(GameBoard board) {
            int rows = board.getRows();
            int cols = board.getCols();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (j < cols - 1 && isValidSwap(i, j, i, j + 1)) {
                        return new int[] { i, j, i, j + 1 };
                    }

                    if (i < rows - 1 && isValidSwap(i, j, i + 1, j)) {
                        return new int[] { i, j, i + 1, j };
                    }
                }
            }

            return null;
        }
    }

    /**
     * Strategy that finds the optimal move based on scoring
     */
    private class OptimalMatchStrategy implements AIStrategy {

        @Override
        public int[] findMove(GameBoard board) {
            int rows = board.getRows();
            int cols = board.getCols();
            int[] bestMove = null;
            int bestScore = -1;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (j < cols - 1) {
                        int score = scoreMove(i, j, i, j + 1);
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new int[] { i, j, i, j + 1 };
                        }
                    }

                    if (i < rows - 1) {
                        int score = scoreMove(i, j, i + 1, j);
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new int[] { i, j, i + 1, j };
                        }
                    }
                }
            }

            if (bestMove != null) {
                return bestMove;
            }

            return findRandomValidMove();
        }
    }

    /**
     * Start auto-play mode with the current strategy
     *
     * @param delayMillis Delay between moves in milliseconds
     * @return Thread running the auto-play
     */
    public Thread startAutoPlay(int delayMillis) {
        Thread autoPlayThread = new Thread(() -> {
            try {
                while (
                    !gameBoard.isGameOver() &&
                    !Thread.currentThread().isInterrupted()
                ) {
                    int[] move = findBestMove();
                    if (move != null) {
                        gameBoard.selectCandy(move[0], move[1]);
                        Thread.sleep(delayMillis / 2);

                        gameBoard.selectCandy(move[2], move[3]);
                        Thread.sleep(delayMillis);
                    } else {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        autoPlayThread.start();
        return autoPlayThread;
    }
}
