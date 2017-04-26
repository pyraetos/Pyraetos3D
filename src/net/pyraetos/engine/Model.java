package net.pyraetos.engine;

import java.io.Serializable;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

@SuppressWarnings("serial")
public abstract class Model extends MovableObject implements Serializable{
	
	protected Matrix transformationMatrix;
	protected MatrixBuffer modelViewMatrix;
	protected MatrixBuffer lightModelViewMatrix;
	
	protected Vector scale;
	protected Matrix scaleMatrix;

	protected Material material;
	
	protected long uid;
	
	public Model(float x, float y, float z){
		super(x, y, z);
		uid = Sys.randomSeed();
		scale = new Vector(1f, 1f, 1f);
		scaleMatrix = new Matrix();
		modelViewMatrix = new MatrixBuffer();
		lightModelViewMatrix = new MatrixBuffer();
		transformationMatrix = new MatrixBuffer();
		material = new Material();
	}
	
	public Model(){
		super();
		uid = Sys.randomSeed();
		scale = new Vector(1f, 1f, 1f);
		scaleMatrix = new Matrix();
		modelViewMatrix = new MatrixBuffer();
		lightModelViewMatrix = new MatrixBuffer();
		transformationMatrix = new MatrixBuffer();
		material = new Material();
	}
	
	protected void update(){
		GraphicsUtil.transformation(transformationMatrix, translationMatrix, rotationMatrix, scaleMatrix, translation, rotation, scale);
	}
	
	public Matrix getTransformationMatrix(){
		return transformationMatrix;
	}
	
	public MatrixBuffer getLightModelViewMatrix(Matrix lightViewMatrix){
		Matrix.multiply(lightViewMatrix, transformationMatrix, lightModelViewMatrix);
		lightModelViewMatrix.updateBuffer();
		return lightModelViewMatrix;
	}
	
	public MatrixBuffer getModelViewMatrix(Matrix viewMatrix){
		Matrix.multiply(viewMatrix, transformationMatrix, modelViewMatrix);
		modelViewMatrix.updateBuffer();
		return modelViewMatrix;
	}

	public abstract void render();
	public abstract void renderIgnoreMaterial();
	
	public Vector getScale(){
		return scale;
	}
	
	public void setScale(Vector v){
		setScale(v.getX(), v.getY(), v.getZ());
	}
	
	public void setScale(float scale){
		setScale(scale, scale, scale);
	}
	
	public void setScale(float sx, float sy, float sz){
		scale.setX(sx);
		scale.setY(sy);
		scale.setZ(sz);
		update();
	}
	
	public Material getMaterial(){
		return material;
	}

	public void setMaterial(Material material){
		this.material = material;
	}

	public abstract Mesh getMesh();

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int)(uid ^ (uid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		Model other = (Model)obj;
		if(uid != other.uid)
			return false;
		return true;
	}

}
