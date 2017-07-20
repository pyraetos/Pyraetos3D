package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.List;

import net.pyraetos.util.Sys;

public class Text{

	private List<Model> models = new ArrayList<Model>();
	
	public Text(float x, float y, float z, String t){
		if(!t.equals("Computers") && !t.equals("NewMedia")) throw new RuntimeException();
		Quad quad = new Quad(x, y, z);
		quad.setScale(0.5f);
		quad.rotate(Sys.PI/2f, 0f, 0f);
		Texture tex = GraphicsUtil.loadTexture(t.equals("Computers") ? "res/computers.png" : "res/neewmedia.png");
		quad.getMaterial().setTexture(tex);
		models.add(quad);
	}
	
	public List<Model> getModels(){
		return models;
	}
	
}
