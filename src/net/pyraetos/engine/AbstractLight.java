package net.pyraetos.engine;

import net.pyraetos.util.Vector;

public abstract class AbstractLight implements Light{

	protected float intensity;
	protected Vector color;
	
	public AbstractLight(){
		intensity = 1f;
		color = new Vector(1f, 1f, 1f);
	}
	
	@Override
	public float getIntensity(){
		return intensity;
	}

	@Override
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}

	@Override
	public Vector getColor(){
		return color;
	}

	@Override
	public void setColor(Vector color){
		this.color = color;
	}

	@Override
	public void setColor(float r, float g, float b){
		color.setX(r);
		color.setY(g);
		color.setZ(b);
	}
	
}
