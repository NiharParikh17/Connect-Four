package ConnectFour;

/**
 * Observer interface used to notify registered observers whenever the game
 * state changes (i.e., a chip has been dropped onto the board).
 *
 * <p>Implementing classes (typically the View) subscribe to the Model and
 * receive updates through {@link #updateGame()} so they can refresh the
 * visual representation of the board without being tightly coupled to the
 * Model's internals.</p>
 *
 * <p>This interface is part of the Observer design pattern used alongside
 * {@link WinnerObserver} to separate game-state events from end-game events.</p>
 *
 * @see WinnerObserver
 * @see ConnectFourModelInterface#registerObserver(ConnectFourObserver)
 */
public interface ConnectFourObserver {

    /**
     * Called by the Model each time a chip is successfully dropped onto the
     * board. Implementations should query the Model for the updated cell
     * coordinates and player color, then repaint the affected slot.
     */
    void updateGame();
}
