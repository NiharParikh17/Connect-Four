package connectfour.model;

import connectfour.observer.ConnectFourObserver;
import connectfour.observer.WinnerObserver;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import lombok.Getter;

/**
 * Model component of the MVC architecture for the Connect Four game.
 *
 * <p>{@code ConnectFourModel} owns all game state and encapsulates the rules
 * of Connect Four. It is the single source of truth for:</p>
 * <ul>
 *   <li>The 6 × 7 board grid (stored as a 2-D {@code int} array where
 *       {@code 0} = empty, {@code 1} = Player 1, {@code 2} = Player 2).</li>
 *   <li>The next available row index per column ({@code availableSpot}).</li>
 *   <li>The current {@link Player} whose turn it is.</li>
 *   <li>Win detection across horizontal, vertical, and both diagonal
 *       directions.</li>
 *   <li>Draw detection when the board is completely full.</li>
 * </ul>
 *
 * <p>The Model acts as the <em>Subject</em> in the Observer pattern. It
 * maintains two independent observer lists:</p>
 * <ul>
 *   <li>{@link ConnectFourObserver} — notified after every chip drop so the
 *       View can repaint the affected cell.</li>
 *   <li>{@link WinnerObserver} — notified only when the game ends (win or
 *       draw) so the View can display the result and trigger a reset.</li>
 * </ul>
 *
 * @see ConnectFourModelInterface
 */
public class ConnectFourModel implements ConnectFourModelInterface {
    /** Number of rows in the board grid. */
    @Getter
    private final int totalRows = 6;
    /** Number of columns in the board grid. */
    @Getter
    private final int totalColumns = 7;

    private final ArrayList<ConnectFourObserver> observers;
    private final ArrayList<WinnerObserver> winObservers;

    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    /**
     * 2-D board array. Each cell stores the id of the player who occupies it,
     * or {@code 0} if empty.
     */
    private final int[][] board;

    /**
     * Tracks the lowest available (unfilled) row index for each column.
     * Starts at {@code totalRows - 1} and decrements as chips are dropped.
     * When a value drops below {@code 0} the column is full.
     */
    private final int[] availableSpot;

    /** Row of the most recently placed chip (set during {@link #dropChip}). */
    @Getter
    private int currentRow;
    /** Column of the most recently placed chip (set during {@link #dropChip}). */
    @Getter
    private int currentColumn;

    /**
     * Column index to be disabled in the View because it has been filled, or
     * {@code -1} when no column needs disabling after the current move.
     */
    private int disablingButton;

    /**
     * Constructs a new {@code ConnectFourModel} in its initial state.
     * <ul>
     *   <li>Empty 6 × 7 board.</li>
     *   <li>Player 1 (Red) goes first.</li>
     *   <li>All columns available from the bottom row.</li>
     * </ul>
     */
    public ConnectFourModel() {
        observers = new ArrayList<>();
        winObservers = new ArrayList<>();

        player1 = new Player("Player 1", 1, Color.RED);
        player2 = new Player("Player 2", 2, Color.YELLOW);
        currentPlayer = player1;

        board = new int[totalRows][totalColumns];
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalColumns; col++) {
                board[row][col] = 0;
            }
        }

        availableSpot = new int[totalColumns];
        //We do -1 because it starts with 0 and not 1
        Arrays.fill(availableSpot, totalRows - 1);

        disablingButton = -1;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPlayer() {
        return currentPlayer.getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Color getColor() {
        return currentPlayer.getColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDisableButton() {
        return disablingButton;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Implementation detail: after placing the chip this method sets
     * {@code disablingButton} temporarily, notifies game observers, then
     * resets it to {@code -1}. Win and draw checks follow. A
     * {@link JOptionPane} dialog announces the result before observers are
     * notified and the board is reset.</p>
     */
    @Override
    public void dropChip(int column) {
        board[availableSpot[column]][column] = currentPlayer.getId();
        currentRow = availableSpot[column];
        currentColumn = column;
        availableSpot[column]--;
        if (availableSpot[column] < 0) {
            disablingButton = column;
        }
        notifyGameObservers();
        disablingButton = -1;
        if (checkWin()) {
            JOptionPane.showMessageDialog(null, currentPlayer.getName() + " wins!");
            notifyWinnerObserver();
            switchPlayer();
        }
        if (boardFull()) {
            JOptionPane.showMessageDialog(null, "The game ended as draw!");
            notifyWinnerObserver();
            switchPlayer();
        }
        switchPlayer();
    }

    /**
     * Checks all win conditions for the current board state.
     *
     * @return {@code true} if any player has four chips in a row
     */
    private boolean checkWin() {
        return checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin();
    }

    /**
     * Scans every row for four consecutive identical non-zero values.
     *
     * @return {@code true} if a horizontal four-in-a-row exists
     */
    private boolean checkHorizontalWin() {
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalColumns - 3; col++) {
                int startChip = board[row][col];
                if (startChip != 0) {
                    if (board[row][col + 1] == startChip
                            && board[row][col + 2] == startChip
                            && board[row][col + 3] == startChip) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Scans every column for four consecutive identical non-zero values.
     *
     * @return {@code true} if a vertical four-in-a-row exists
     */
    private boolean checkVerticalWin() {
        for (int row = 0; row < totalRows - 3; row++) {
            for (int col = 0; col < totalColumns; col++) {
                int startChip = board[row][col];
                if (startChip != 0) {
                    if (board[row + 1][col] == startChip
                            && board[row + 2][col] == startChip
                            && board[row + 3][col] == startChip) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Scans both diagonal directions for four consecutive identical non-zero
     * values.
     *
     * <ul>
     *   <li>Left diagonal ({@code /}): row increases, column decreases.</li>
     *   <li>Right diagonal ({@code \}): row increases, column increases.</li>
     * </ul>
     *
     * @return {@code true} if a diagonal four-in-a-row exists in either
     *         direction
     */
    private boolean checkDiagonalWin() {
        //Checking left diagonal /
        for (int row = 0; row < totalRows - 3; row++) {
            for (int col = 3; col < totalColumns; col++) {
                int startChip = board[row][col];
                if (startChip != 0) {
                    if (board[row + 1][col - 1] == startChip
                            && board[row + 2][col - 2] == startChip
                            && board[row + 3][col - 3] == startChip) {
                        return true;
                    }
                }
            }
        }

        //Checking right diagonal \
        for (int row = 0; row < totalRows - 3; row++) {
            for (int col = 0; col < totalColumns - 3; col++) {
                int startChip = board[row][col];
                if (startChip != 0) {
                    if (board[row + 1][col + 1] == startChip
                            && board[row + 2][col + 2] == startChip
                            && board[row + 3][col + 3] == startChip) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Alternates the active player between Player 1 and Player 2.
     */
    private void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else if (currentPlayer == player2) {
            currentPlayer = player1;
        }
    }

    /**
     * Determines whether every cell on the board has been filled.
     *
     * @return {@code true} if no empty ({@code 0}) cell remains, indicating
     *         a draw
     */
    private boolean boardFull() {
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalColumns; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit() {
        System.exit(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        currentPlayer = player1;

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalColumns; col++) {
                board[row][col] = 0;
            }
        }

        //We do -1 because it starts with 0 and not 1
        Arrays.fill(availableSpot, totalRows - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerObserver(ConnectFourObserver o) {
        observers.add(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerObserver(WinnerObserver o) {
        winObservers.add(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObserver(ConnectFourObserver o) {
        observers.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObserver(WinnerObserver o) {
        winObservers.remove(o);
    }

    /**
     * Notifies all registered {@link ConnectFourObserver}s that the board
     * state has changed (a chip was just placed).
     */
    private void notifyGameObservers() {
        for (ConnectFourObserver obs : observers) {
            obs.updateGame();
        }
    }

    /**
     * Notifies all registered {@link WinnerObserver}s that the game has ended
     * (either by a win or a draw).
     */
    private void notifyWinnerObserver() {
        for (WinnerObserver obs : winObservers) {
            obs.updateWinner();
        }
    }
}

