package ConnectFour;

/**
 * Observer interface used to notify registered observers when the game reaches
 * a terminal state — either a player has won or the board is completely full
 * (a draw).
 *
 * <p>Implementing classes (typically the View) subscribe to the Model and
 * receive the {@link #updateWinner()} callback so they can react to end-game
 * events (e.g., prompting a reset) without being coupled to the Model's
 * win-detection logic.</p>
 *
 * <p>This interface works in tandem with {@link ConnectFourObserver}: the
 * {@link ConnectFourObserver} handles per-turn updates, while
 * {@code WinnerObserver} handles final game-over events.</p>
 *
 * @see ConnectFourObserver
 * @see ConnectFourModelInterface#registerObserver(WinnerObserver)
 */
public interface WinnerObserver {

    /**
     * Called by the Model when the game ends — either because a player has
     * connected four chips in a row (horizontally, vertically, or diagonally)
     * or because the board is completely full with no winner (draw).
     * Implementations should trigger any end-game UI logic such as displaying
     * a result and resetting the board.
     */
    void updateWinner();
}
