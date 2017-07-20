package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.List;

import net.pyraetos.util.Sys;

public class Keyboard{

	private List<Model> models = new ArrayList<Model>();
	private static Texture tex = GraphicsUtil.loadTexture("res/keyboard.png");
	
	public Keyboard(float x, float y, float z){
		Quad quad = new Quad(x, y, z);
		quad.setScale(0.5f);
		quad.rotate(Sys.PI/2f, 0f, 0f);
		quad.getMaterial().setTexture(tex);
		models.add(quad);
	}
	
	public List<Model> getModels(){
		return models;
	}
	
}
