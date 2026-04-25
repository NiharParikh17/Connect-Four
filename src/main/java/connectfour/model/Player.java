package connectfour.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;

/**
 * Represents a player in the Connect Four game.
 *
 * <p>Each player has a unique display name, a numeric identifier used to
 * mark cells on the internal board grid, and a color used to visually
 * distinguish their chips in the GUI.</p>
 */
@Getter
@AllArgsConstructor
public class Player {
    /** Display name shown in the GUI turn label (e.g., "Player 1"). */
    private final String name;

    /**
     * Numeric identifier used to mark this player's cells on the
     * internal board array (must be unique per game instance).
     */
    private final int id;

    /** AWT {@link Color} used to paint this player's chips in the board slots. */
    private final Color color;
}
