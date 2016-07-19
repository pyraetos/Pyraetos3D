package net.pyraetos.engine;

@SuppressWarnings("serial")
public class Quad extends Model{
	
	public static Mesh MESH = GraphicsUtil.loadMesh("res/quad.obj");

	public Quad(float x, float y, float z){
		super(x, y, z);
		update();
	}
	
	@Override
	public void render(){
		material.bindTexture();
		MESH.render();
	}

	@Override
	public void renderIgnoreMaterial(){
		MESH.render();
	}

	@Override
	public Mesh getMesh(){
		return MESH;
	}
}
