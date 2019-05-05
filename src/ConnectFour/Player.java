package ConnectFour;

/**
	@author Nihar Parikh
	ConnectFour.Player.java
*/

import java.awt.Color;

/**
 * The players playing the game.
 */
public class Player {
    private String name;
    private int id;
    private Color color;

    /**
     * Constructor for the Player class
     * @param name name of the player
     * @param id id of the player
     * @param color color to represent on board
     */
    public Player(String name, int id, Color color){
        this.name = name;
        this.id = id;
        this.color = color;
    }

    /**
     *
     * @return name of the player
     */
    public String getName(){
        return this.name;
    }

    /**
     *
     * @return id of the player
     */
    public int getId(){
        return this.id;
    }

    /**
     *
     * @return color to represent on board
     */
    public Color getColor(){
        return this.color;
    }
}
