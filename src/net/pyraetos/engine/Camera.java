package net.pyraetos.engine;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class Camera extends MovableObject{

	public static final float MAX_PITCH = 5.14872f;
	public static final float MIN_PITCH = 1.309f;
	
	private Matrix viewMatrix;
	
	public Camera(){
		viewMatrix = new Matrix();
		update();
	}
	
	@Override
	protected void update(){
		GraphicsUtil.view(viewMatrix, translationMatrix, rotationMatrix, translation, rotation);
	}
	
	@Override
	public void translate(Vector v){
		translate(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void translate(float dx, float dy, float dz){
		if(dz != 0){
			translation.setX(translation.getX() + (Sys.sin(rotation.getY()) * -1f * dz));
			translation.setZ(translation.getZ() + (Sys.cos(rotation.getY()) * dz));
		}
		translation.setY(translation.getY() + dy);
		update();
	}
	
	@Override
	public void rotate(Vector v){
		rotate(v.getX(), v.getY(), v.getZ());
	}
	
	@Override
	public void rotate(float pitch, float yaw, float roll){
		float p = Sys.simplifyAngler(rotation.getX() + pitch);
		if(p <= MIN_PITCH || p >= MAX_PITCH)
			rotation.setX(p);
		rotation.setY(Sys.simplifyAngler(rotation.getY() + yaw));
		rotation.setZ(Sys.simplifyAngler(rotation.getZ() + roll));
		update();
	}
	
	public Matrix getViewMatrix(){
		return viewMatrix;
	}
	
	public void view(Vector dest, Vector v){
		Matrix.multiply(viewMatrix, v, 0f, dest);
	}
}
