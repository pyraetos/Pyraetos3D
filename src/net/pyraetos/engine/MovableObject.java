package net.pyraetos.engine;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Vector;

public abstract class MovableObject implements Movable{

	protected Vector translation;
	protected Vector rotation;
	
	protected Matrix translationMatrix;
	protected Matrix rotationMatrix;
	
	public MovableObject(float x, float y, float z){
		translation = new Vector(x, y, z);
		rotation = new Vector(0f, 0f, 0f);
		translationMatrix = new Matrix();
		rotationMatrix = new Matrix();
	}
	
	public MovableObject(){
		translation = new Vector(0f, 0f, 0f);
		rotation = new Vector(0f, 0f, 0f);
		translationMatrix = new Matrix();
		rotationMatrix = new Matrix();
	}
	
	protected abstract void update();
	
	@Override
	public Vector getTranslation(){
		return translation;
	}

	@Override
	public Vector getRotation(){
		return rotation;
	}

	@Override
	public void setTranslation(Vector v){
		setTranslation(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void setTranslation(float x, float y, float z){
		translation.setX(x);
		translation.setY(y);
		translation.setZ(z);
		update();
	}

	@Override
	public void setRotation(Vector v){
		setRotation(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void setRotation(float pitch, float yaw, float roll){
		rotation.setX(pitch);
		rotation.setY(yaw);
		rotation.setZ(roll);
		update();
	}

	@Override
	public float getX(){
		return translation.getX();
	}

	@Override
	public float getY(){
		return translation.getY();
	}

	@Override
	public float getZ(){
		return translation.getZ();
	}

	@Override
	public float getPitch(){
		return rotation.getX();
	}

	@Override
	public float getYaw(){
		return rotation.getY();
	}

	@Override
	public float getRoll(){
		return rotation.getZ();
	}

	@Override
	public void setX(float x){
		translation.setX(x);
		update();
	}

	@Override
	public void setY(float y){
		translation.setY(y);
		update();
	}

	@Override
	public void setZ(float z){
		translation.setZ(z);
		update();
	}

	@Override
	public void setPitch(float pitch){
		rotation.setX(pitch);
		update();
	}

	@Override
	public void setYaw(float yaw){
		rotation.setY(yaw);
		update();
	}

	@Override
	public void setRoll(float roll){
		rotation.setZ(roll);
		update();
	}
	
	@Override
	public void translate(Vector v){
		translate(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void translate(float dx, float dy, float dz){
		setTranslation(getX() + dx, getY() + dy, getZ() + dz);
	}
	
	@Override
	public void rotate(Vector v){
		rotate(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void rotate(float dpitch, float dyaw, float droll){
		setRotation(getPitch() + dpitch, getYaw() + dyaw, getRoll() + droll);
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MovableObject other = (MovableObject)obj;
		if(rotation == null){
			if(other.rotation != null)
				return false;
		}else if(!rotation.equals(other.rotation))
			return false;
		if(translation == null){
			if(other.translation != null)
				return false;
		}else if(!translation.equals(other.translation))
			return false;
		return true;
	}

}
