package net.pyraetos.engine;

import net.pyraetos.util.Vector;

public interface Movable{

	Vector getTranslation();
	Vector getRotation();
	
	void setTranslation(Vector v);
	void setTranslation(float x, float y, float z);
	void setRotation(Vector v);
	void setRotation(float pitch, float yaw, float roll);
	
	float getX();
	float getY();
	float getZ();
	
	float getPitch();
	float getYaw();
	float getRoll();
	
	void setX(float x);
	void setY(float y);
	void setZ(float z);
	
	void setPitch(float pitch);
	void setYaw(float yaw);
	void setRoll(float roll);
	
	void translate(Vector v);
	void rotate(Vector v);
	void translate(float dx, float dy, float dz);
	void rotate(float dpitch, float dyaw, float droll);
	
}
