package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.List;

import net.pyraetos.util.Sys;

public class PressurePlate{

	private List<Model> models = new ArrayList<Model>();
	private static Texture tex = GraphicsUtil.loadTexture("res/pressureplate.png");
	private static Texture tex2 = GraphicsUtil.loadTexture("res/ppactivated.png");
	private boolean activated = false;
	private Quad q;
	
	
	public PressurePlate(float x, float y, float z){
		Quad quad = new Quad(x, y, z);
		quad.setScale(0.5f);
		quad.rotate(0, 0f, 0);
		quad.getMaterial().setTexture(tex);
		q = quad;
		models.add(quad);
	}
	
	public List<Model> getModels(){
		return models;
	}
	
	public void setActivated(boolean b){
		if(b == activated) return;
		activated = b;
		if(b){
			q.getMaterial().setTexture(tex2);
		}else{
			q.getMaterial().setTexture(tex);
		}
	}
}
