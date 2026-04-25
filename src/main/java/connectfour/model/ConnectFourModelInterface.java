package connectfour.model;

import connectfour.observer.ConnectFourObserver;
import connectfour.observer.WinnerObserver;
import java.awt.Color;

/**
 * Defines the contract for the Model layer in the MVC architecture.
 *
 * <p>The Model owns all game state: the board grid, the two players, whose
 * turn it is, and win/draw detection. It also acts as the subject in the
 * Observer pattern, maintaining lists of {@link ConnectFourObserver} (for
 * per-turn board updates) and {@link WinnerObserver} (for end-game events).</p>
 *
 * <p>Programming against this interface rather than the concrete
 * {@link ConnectFourModel} class keeps the Controller and View loosely coupled
 * to the game logic and simplifies testing.</p>
 *
 * @see ConnectFourModel
 * @see ConnectFourObserver
 * @see WinnerObserver
 */
public interface ConnectFourModelInterface {

    /**
     * Returns the total number of rows in the board grid.
     *
     * @return number of rows (default: 6)
     */
    int getTotalRows();

    /**
     * Returns the total number of columns in the board grid.
     *
     * @return number of columns (default: 7)
     */
    int getTotalColumns();

    /**
     * Returns the display name of the player whose turn it currently is.
     *
     * @return name of the current player (e.g., "Player 1" or "Player 2")
     */
    String getPlayer();

    /**
     * Returns the row index of the most recently dropped chip.
     * This is valid immediately after {@link #dropChip(int)} is called and
     * before the next call changes state.
     *
     * @return zero-based row index of the last placed chip
     */
    int getCurrentRow();

    /**
     * Returns the column index of the most recently dropped chip.
     * This is valid immediately after {@link #dropChip(int)} is called and
     * before the next call changes state.
     *
     * @return zero-based column index of the last placed chip
     */
    int getCurrentColumn();

    /**
     * Returns the color associated with the player who just dropped a chip.
     * Used by the View to paint the correct chip color in the board slot.
     *
     * @return {@link Color} of the current player (Red for Player 1,
     *         Yellow for Player 2)
     */
    Color getColor();

    /**
     * Returns the index of the column whose "Drop" button should be disabled
     * because the column has been completely filled.
     *
     * @return zero-based column index to disable, or {@code -1} if no column
     *         needs to be disabled after the latest move
     */
    int getDisableButton();

    /**
     * Drops a chip for the current player into the specified column.
     *
     * <p>The chip occupies the lowest available row in that column. After
     * placing the chip the method:</p>
     * <ol>
     *   <li>Notifies all {@link ConnectFourObserver}s to refresh the board.</li>
     *   <li>Checks for a winning condition (horizontal, vertical, diagonal).</li>
     *   <li>Checks for a draw (board completely full).</li>
     *   <li>Notifies {@link WinnerObserver}s and switches the player if the
     *       game has ended.</li>
     *   <li>Always switches the player at the end of the turn.</li>
     * </ol>
     *
     * @param column zero-based index of the column (0 to totalColumns - 1)
     *               into which the chip should be dropped; the caller is
     *               responsible for ensuring the column is not full
     */
    void dropChip(int column);

    /**
     * Terminates the application by calling {@link System#exit(int)} with
     * status code 0.
     */
    void exit();

    /**
     * Resets the game model to its initial state: clears the board, restores
     * all available-slot counters, and sets the current player back to
     * Player 1.
     */
    void reset();

    /**
     * Registers a {@link ConnectFourObserver} to be notified whenever a chip
     * is dropped (i.e., after each turn).
     *
     * @param o the observer to register; must not be {@code null}
     */
    void registerObserver(ConnectFourObserver o);

    /**
     * Registers a {@link WinnerObserver} to be notified when the game ends
     * (win or draw).
     *
     * @param o the observer to register; must not be {@code null}
     */
    void registerObserver(WinnerObserver o);

    /**
     * Removes a previously registered {@link ConnectFourObserver}.
     *
     * @param o the observer to remove; no-op if not currently registered
     */
    void removeObserver(ConnectFourObserver o);

    /**
     * Removes a previously registered {@link WinnerObserver}.
     *
     * @param o the observer to remove; no-op if not currently registered
     */
    void removeObserver(WinnerObserver o);
}

