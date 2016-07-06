package net.pyraetos.engine;

import java.io.Serializable;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Vector;

@SuppressWarnings("serial")
public class DirectionalLight extends AbstractLight implements Serializable{

	protected float x;
	protected float z;
	protected Vector direction;
	protected Vector dest;
	protected Matrix translationMatrix;
	protected Matrix rotationMatrix;
	protected Matrix lightViewMatrix;
	
	public DirectionalLight(Vector direction){
		this.direction = direction;
		this.dest = new Vector(0f, 0f, 0f);
		translationMatrix = new Matrix();
		rotationMatrix = new Matrix();
		lightViewMatrix = new Matrix();
		update();
	}
	
	protected void update(){
		GraphicsUtil.lightView(lightViewMatrix, translationMatrix, rotationMatrix, direction, x, z);
	}
	
	public Vector getDirection(){
		return direction;
	}
	
	public void setDirection(Vector direction){
		this.direction = direction;
		update();
	}
	
	public Vector getVectorDest(){
		return dest;
	}
	
	public float getX(){
		return x;
	}
	
	public float getZ(){
		return z;
	}
	
	public void setPosition(float x, float z){
		this.x = x;
		this.z = z;
		update();
	}
	
	public void setX(float x){
		this.x = x;
		update();
	}
	
	public void setZ(float z){
		this.z = z;
		update();
	}

	public Matrix getTranslationMatrix(){
		return translationMatrix;
	}

	public Matrix getRotationMatrix(){
		return rotationMatrix;
	}

	public Matrix getLightViewMatrix(){
		return lightViewMatrix;
	}
}
