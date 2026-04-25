package connectfour.controller;

/**
 * Defines the contract for the Controller layer in the MVC architecture.
 *
 * <p>The Controller mediates between the View and the Model. It exposes only
 * the actions that the View is allowed to trigger, keeping the View decoupled
 * from Model internals.</p>
 *
 * <p>Programming to this interface rather than to the concrete
 * {@link ConnectFourController} class makes it straightforward to substitute
 * alternative controller implementations (e.g., for AI players or testing).</p>
 *
 * @see ConnectFourController
 */
public interface ConnectFourControllerInterface {

    /**
     * Instructs the Controller to drop a chip into the specified column on
     * behalf of the current player. The Controller delegates the state change
     * to the Model and triggers a visual update in the View.
     *
     * @param column zero-based index of the column (0 to totalColumns - 1)
     *               into which the chip should be dropped
     */
    void dropChip(int column);

    /**
     * Instructs the Controller to exit the application. Delegates to the
     * Model which terminates the JVM process.
     */
    void exit();

    /**
     * Instructs the Controller to reset the game to its initial state.
     * Both the Model data (board, current player, available slots) and the
     * View (chip colors, button states, turn label) are cleared.
     */
    void reset();
}

