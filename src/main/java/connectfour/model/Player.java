package connectfour.model;

import java.awt.Color;

/**
 * Represents a player in the Connect Four game.
 *
 * <p>Each player has a unique display name, a numeric identifier used to
 * mark cells on the internal board grid, and a color used to visually
 * distinguish their chips in the GUI.</p>
 *
 * <p>The two default players created by {@link ConnectFourModel} are:</p>
 * <ul>
 *   <li><b>Player 1</b> — id {@code 1}, color {@link Color#RED}</li>
 *   <li><b>Player 2</b> — id {@code 2}, color {@link Color#YELLOW}</li>
 * </ul>
 */
public class Player {
    private String name;
    private int id;
    private Color color;

    /**
     * Constructs a new Player with the given attributes.
     *
     * @param name  display name shown in the GUI turn label (e.g., "Player 1")
     * @param id    numeric identifier used to mark this player's cells on the
     *              internal board array (must be unique per game instance)
     * @param color AWT {@link Color} used to paint this player's chips in the
     *              board slots
     */
    public Player(String name, int id, Color color){
        this.name = name;
        this.id = id;
        this.color = color;
    }

    /**
     * Returns the display name of this player.
     *
     * @return player's name (e.g., "Player 1")
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns the numeric identifier of this player.
     * The id is stored in the board's 2-D integer array to track chip
     * ownership for each cell.
     *
     * @return player's unique integer id
     */
    public int getId(){
        return this.id;
    }

    /**
     * Returns the AWT {@link Color} used to render this player's chips.
     *
     * @return player's chip color
     */
    public Color getColor(){
        return this.color;
    }
}

