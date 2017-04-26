package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class Hallway{
	
	private List<WallSegment> wss  = new ArrayList<WallSegment>();
	private Set<CollisionPlane> colls = new HashSet<CollisionPlane>();
	
	private float x;
	private float z;
	
	public Hallway(float x, float z){
		this.x = x;
		this.z = z;
		WallSegment ws = new WallSegment(x, 0f, z, 0);
		//wss.add(ws);
		//ws = new WallSegment(x, 0f, z-32f, 0);
		//wss.add(ws);
		for(int i = 0; i < 8; i++){
			ws = new WallSegment(x-.5f, 0f, z-3.5f - 4 * i, 1);
			wss.add(ws);
		}
		for(int i = 0; i < 8; i++){
			ws = new WallSegment(x+3.5f, 0f, z-3.5f - 4 * i, 1);
			wss.add(ws);
		}
		//floors
		for(int i = 0; i < 8; i++){
			ws = new WallSegment(x, -.5f, z-3.5f - 4 * i, 2);
			wss.add(ws);
		}
		for(int i = 0; i < 8; i++){
			ws = new WallSegment(x+2f, -.5f, z-3.5f - 4 * i, 2);
			wss.add(ws);
		}
		constructColls();
	}
	
	private void constructColls(){
		//LEFT WALL
		CollisionPlane c = new CollisionPlane('x', new Vector(x-1f, 0f, z), new Vector(x-1f, 2f, z-33f));
		colls.add(c);
		c = new CollisionPlane('x', new Vector(x, 0f, z), new Vector(x, 2f, z-33f));
		colls.add(c);

		//RIGHT WALL
		c = new CollisionPlane('x', new Vector(x+3f, 0f, z), new Vector(x+3f, 2f, z-33f));
		colls.add(c);
		c = new CollisionPlane('x', new Vector(x+4f, 0f, z), new Vector(x + 4f, 2f, z-33f));
		colls.add(c);
		
		//FLOOR
		c = new CollisionPlane('y', new Vector(x-.5f, 0f, z), new Vector(x+3f, 0f, z-32f));
		colls.add(c);
	}
	
	public List<WallSegment> getWalls(){
		return wss;
	}
	
	public Set<CollisionPlane> getColls(){
		return colls;
	}
	
}
