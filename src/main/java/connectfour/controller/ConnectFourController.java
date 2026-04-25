package connectfour.controller;

import connectfour.model.ConnectFourModelInterface;
import connectfour.view.ConnectFourView;

/**
 * Controller component of the MVC architecture for the Connect Four game.
 *
 * <p>{@code ConnectFourController} acts as the mediator between
 * the View and the Model. It receives high-level user actions from the View,
 * delegates state mutations to the Model, and instructs the View to update
 * its presentation accordingly.</p>
 *
 * <p>By wiring the View and Model through this Controller, neither component
 * needs a direct reference to the other's concrete implementation, preserving
 * the separation of concerns central to MVC.</p>
 *
 * <p><b>Lifecycle:</b> Instantiating this class automatically creates a
 * {@link ConnectFourView} and registers it as an observer with the Model so
 * the GUI is ready to display game state immediately.</p>
 *
 * @see ConnectFourControllerInterface
 */
public class ConnectFourController implements ConnectFourControllerInterface {
    private ConnectFourModelInterface model;
    private ConnectFourView view;

    /**
     * Constructs a {@code ConnectFourController} and initializes the View.
     *
     * <p>The View is created and bound to both the provided Model and this
     * Controller. The View registers itself as an observer on the Model so it
     * will be notified of all subsequent game-state changes.</p>
     *
     * @param model the game Model; must not be {@code null}
     */
    public ConnectFourController(ConnectFourModelInterface model){
        this.model = model;
        view = new ConnectFourView(model, this);
    }

    /**
     * Drops a chip into the specified column for the current player.
     *
     * <p>Delegates the state mutation to the Model (which validates placement,
     * updates the board array, and notifies observers), then tells the View to
     * update the turn label for the next player.</p>
     *
     * @param column zero-based column index (0 to totalColumns - 1) into which
     *               the chip is dropped; the View ensures only valid, non-full
     *               columns can be selected
     */
    @Override
    public void dropChip(int column) {
        model.dropChip(column);
        view.switchTurns();
    }

    /**
     * Exits the application by delegating to the Model's exit routine, which
     * calls {@link System#exit(int)} with status {@code 0}.
     */
    @Override
    public void exit() {
        model.exit();
    }

    /**
     * Resets the game to its initial state.
     *
     * <p>Delegates Model data reset (board cleared, current player set to
     * Player 1, available slots restored) to the Model, and then delegates
     * the visual reset (chip colors cleared, buttons re-enabled, turn label
     * updated) to the View.</p>
     */
    @Override
    public void reset() {
        model.reset();
        view.resetView();
    }
}

