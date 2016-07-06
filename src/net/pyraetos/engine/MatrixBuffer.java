package net.pyraetos.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.pyraetos.util.Matrix;

@SuppressWarnings("serial")
public class MatrixBuffer extends Matrix{

	private transient FloatBuffer buf;
	
	//For transient initialization
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		buf = BufferUtils.createFloatBuffer(16);
	}
	
	public MatrixBuffer(){
		super();
		buf = BufferUtils.createFloatBuffer(16);
	}
	
	public MatrixBuffer(float...values){
		super(values);
		buf = BufferUtils.createFloatBuffer(16);
	}
	
	public MatrixBuffer(Matrix m){
		super(m);
		buf = BufferUtils.createFloatBuffer(16);
	}
	
	public void updateBuffer(){
		buf.clear();
		
		buf.put(v00); buf.put(v10); buf.put(v20); buf.put(v30);
		buf.put(v01); buf.put(v11); buf.put(v21); buf.put(v31);
		buf.put(v02); buf.put(v12); buf.put(v22); buf.put(v32);
		buf.put(v03); buf.put(v13); buf.put(v23); buf.put(v33);
		
		buf.flip();
	}
	
	public FloatBuffer getBuffer(){
		return buf;
	}
	
}
