package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class RoomOneDoor{
	
	private List<WallSegment> wss  = new ArrayList<WallSegment>();
	private Set<CollisionPlane> colls = new HashSet<CollisionPlane>();
	
	private float x;
	private float z;
	
	public RoomOneDoor(float x, float z){
		this.x = x;
		this.z = z;
		WallSegment ws = new WallSegment(x, 0f, z, 0);
		//wss.add(ws);
		//ws = new WallSegment(x, 0f, z-32f, 0);
		//wss.add(ws);
		
		//Side walls
		for(int i = 0; i < 4; i++){
			ws = new WallSegment(x-.5f, 0f, z-3.5f - 4 * i, 1);
			wss.add(ws);
		}
		for(int i = 0; i < 4; i++){
			ws = new WallSegment(x+6*3.5f, 0f, z-3.5f - 4 * i, 1);
			wss.add(ws);
		}
		//Front n back walls
		for(int i = -1; i < 2; i++){//botleft
			ws = new WallSegment(x + 4 * i + 1f, 0f, z, 0);
			wss.add(ws);
		}
		for(int i = 3; i < 6; i++){//botright
			ws = new WallSegment(x + 4 * i +1f, 0f, z, 0);
			wss.add(ws);
		}
		for(int i = -1; i < 6; i++){//top
			ws = new WallSegment(x + 4 * i +1f, 0f, z-16f, 0);
			wss.add(ws);
		}
		//floors
		for(float f = 0f; f <= 20f; f+=2){
			for(int i = 0; i < 4; i++){
				ws = new WallSegment(x+f, -.5f, z-3.5f - 4 * i, 2);
				wss.add(ws);
			}
		}
		constructColls();
	}
	
	private void constructColls(){
		//LEFT WALL
		CollisionPlane c = new CollisionPlane('x', new Vector(x-1f, 0f, z), new Vector(x-1f, 2f, z-17f));
		colls.add(c);
		c = new CollisionPlane('x', new Vector(x, 0f, z), new Vector(x, 2f, z-17f));
		colls.add(c);

		//RIGHT WALL
		c = new CollisionPlane('x', new Vector(x+5*3.5f+3f, 0f, z), new Vector(x+3f, 2f, z-17f));
		colls.add(c);
		c = new CollisionPlane('x', new Vector(x+5*3.5f+4f, 0f, z), new Vector(x + 4f, 2f, z-17f));
		colls.add(c);
		
		//FRONT WALLS
		c = new CollisionPlane('z', new Vector(x-1f, 0f, z-.05f), new Vector(x+9f, 2f, z-.05f));
		colls.add(c);
		c = new CollisionPlane('z', new Vector(x+12f, 0f, z-.05f), new Vector(x+22f, 2f, z-.05f));
		colls.add(c);
		
		//BACK WALL
		c = new CollisionPlane('z', new Vector(x-1f, 0f, z-16f+.05f), new Vector(x+22f, 2f, z-16f+.05f));
		colls.add(c);
		
		//FLOOR
		c = new CollisionPlane('y', new Vector(x-.5f, 0f, z), new Vector(x+21f, 0f, z-16f));
		colls.add(c);
	}
	
	public List<WallSegment> getWalls(){
		return wss;
	}
	
	public Set<CollisionPlane> getColls(){
		return colls;
	}
	
}
