package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.List;

import net.pyraetos.util.Sys;

public class Number{

	private List<Model> models = new ArrayList<Model>();
	
	public Number(float x, float y, float z, int number){
		if(number != 5841 && number != 1031) throw new RuntimeException();
		Quad quad = new Quad(x, y, z);
		quad.setScale(0.5f);
		quad.rotate(Sys.PI/2f, 0f, Sys.PI);
		Texture tex = GraphicsUtil.loadTexture(number == 5841 ? "res/5841.png" : "res/1031.png");
		quad.getMaterial().setTexture(tex);
		models.add(quad);
	}
	
	public List<Model> getModels(){
		return models;
	}
	
}
