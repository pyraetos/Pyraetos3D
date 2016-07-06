package net.pyraetos.engine;

import net.pyraetos.util.Vector;

public class PointLight{

	private Vector color;
	private Vector position;
	private float intensity;
	private float specularPower;
	private float attenuationConstant;
	private float attenuationLinear;
	private float attenuationExponent;
	
	public PointLight(){
		this(new Vector(1f, 1f, 1f), new Vector(0f, 0f, 0f));
	}
	
	public PointLight(Vector position){
		this(new Vector(1f, 1f, 1f), position);
	}
	
	public PointLight(Vector color, Vector position){
		this.color = color;
		this.position = position;
		this.intensity = 1f;
		this.specularPower = 10f;
		this.attenuationConstant = 1f;
		this.attenuationLinear = .25f;
		this.attenuationExponent = .125f;
	}

	public float getSpecularPower(){
		return specularPower;
	}

	public void setSpecularPower(float specularPower){
		this.specularPower = specularPower;
	}

	public Vector getColor(){
		return color;
	}

	public void setColor(Vector color){
		this.color = color;
	}

	public Vector getPosition(){
		return position;
	}

	public void setPosition(Vector position){
		this.position = position;
	}

	public float getIntensity(){
		return intensity;
	}

	public void setIntensity(float intensity){
		this.intensity = intensity;
	}

	public float getAttenuationConstant(){
		return attenuationConstant;
	}

	public void setAttenuationConstant(float attenuationConstant){
		this.attenuationConstant = attenuationConstant;
	}

	public float getAttenuationLinear(){
		return attenuationLinear;
	}

	public void setAttenuationLinear(float attenuationLinear){
		this.attenuationLinear = attenuationLinear;
	}

	public float getAttenuationExponent(){
		return attenuationExponent;
	}

	public void setAttenuationExponent(float attenuationExponent){
		this.attenuationExponent = attenuationExponent;
	}
	
}
