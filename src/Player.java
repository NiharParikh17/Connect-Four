/**
	@author Nihar Parikh
	Player.java
*/

import java.awt.Color;

public class Player {
    private String name;
    private int id;
    private Color color;

    public Player(String name, int id, Color color){
        this.name = name;
        this.id = id;
        this.color = color;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public Color getColor(){
        return this.color;
    }
}
