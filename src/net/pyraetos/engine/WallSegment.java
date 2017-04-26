package net.pyraetos.engine;

import java.util.ArrayList;
import java.util.List;

import net.pyraetos.util.Sys;

public class WallSegment{

	private List<Model> models = new ArrayList<Model>();
	private static Texture tex = GraphicsUtil.loadTexture("res/stone_texture.png");
	
	public WallSegment(float x, float y, float z, int orient){
		float dx = 0f;
		float dy = 0f;
		float dz = 0f;
		for(int i = 0; i < 8; i++){
			Quad quad = new Quad(x, y, z);
			quad.setScale(.5f);
			if(orient == 0){
				quad.rotate(Sys.PI/2f, 0f, 0f);
				quad.translate(dx, dy, dz);
				dx = (dx == 3f) ? 0f : (dx+1f);
				dy = i >  3 ? 0f : 1f;
			}else if(orient == 1){
				quad.rotate(Sys.PI/2f, 0f, Sys.PI/2f);
				quad.translate(dx, dy, dz);
				dz = (dz == 3f) ? 0f : (dz+1f);
				dy = i >  3 ? 0f : 1f;
			}else if(orient == 2){
				quad.translate(dx, dy, dz);
				dz = (dz == 3f) ? 0f : (dz+1f);
				dx = i >  3 ? 0f : 1f;
			}
			quad.getMaterial().setTexture(tex);
			models.add(quad);
		}
	}
	
	public List<Model> getModels(){
		return models;
	}
	
}
