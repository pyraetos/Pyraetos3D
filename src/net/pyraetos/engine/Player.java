package net.pyraetos.engine;

import net.pyraetos.util.Vector;

/**
 * Created by Denise on 3/28/2017.
 */
public class Player {
    public int playerID;
	public float x;
	public float y;
	public float z;
	
	public Player(int playerID, float x, float y, float z){
		this.playerID = playerID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector getPosition(){
		return new Vector(x,y,z);
	}
	
}
