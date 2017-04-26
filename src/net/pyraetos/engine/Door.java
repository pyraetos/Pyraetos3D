package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class Door{

	private List<Model> models = new ArrayList<Model>();
	private Set<CollisionPlane> colls = new HashSet<CollisionPlane>();
	private static Texture tex = GraphicsUtil.loadTexture("res/gate.png");
	
	private float x;
	private float z;
	
	public Door(float x, float z){
		this.x=x;
		this.z=z;
		Quad quad = new Quad(x, .5f, z);
		quad.rotate(Sys.PI/2f, 0f, 0f);
		quad.getMaterial().setTexture(tex);
		models.add(quad);
		quad = new Quad(x+2f, .5f, z);
		quad.rotate(Sys.PI/2f, 0f, 0f);
		quad.getMaterial().setTexture(tex);
		models.add(quad);
		constructColls();
	}
	
	private void constructColls(){
		//Frontal
		CollisionPlane c = new CollisionPlane('z', new Vector(x-.25f, 0f, z+.2f), new Vector(x+3f, 2.5f, z+.2f));
		colls.add(c);
	}
	
	public List<Model> getModels(){
		return models;
	}
	
	public Set<CollisionPlane> getColls(){
		return colls;
	}
	
	
}
