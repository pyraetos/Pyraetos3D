package net.pyraetos.engine;

import java.io.Serializable;

import net.pyraetos.util.Vector;

@SuppressWarnings("serial")
public class Material implements Serializable{

	private Vector color;
	private float reflectance;
	private Texture texture;
	
	public Material(){
		this(new Vector(1f, 1f, 1f));
	}

	public Material(Vector color){
		this.color = color;
		this.reflectance = 1f;
		this.texture = null;
	}
	
	public Material(Texture texture){
		this();
		this.texture = texture;
	}

	public Vector getColor(){
		return color;
	}

	public void setColor(Vector color){
		this.color = color;
	}

	public boolean usesColor(){
		return texture == null;
	}

	public float getReflectance(){
		return reflectance;
	}

	public void setReflectance(float reflectance){
		this.reflectance = reflectance;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public void bindTexture(){
		if(texture != null)
			texture.bind();
	}
	
	public void setTexture(Texture t){
		this.texture = t;
	}
}
