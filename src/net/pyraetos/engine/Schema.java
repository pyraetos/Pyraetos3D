package net.pyraetos.engine;

public class Schema{

	public Mesh mesh;
	
	public Schema(){
		mesh = GraphicsUtil.loadMesh("res/brick.obj");
	}
	
}
