package net.pyraetos.engine;

@SuppressWarnings("serial")
public class Block extends Model{
	
	public static Mesh MESH = GraphicsUtil.loadMesh("res/block.msh");
	public static Texture GRASS = GraphicsUtil.loadTexture("res/allgrassblock.png");
	public static Texture DIRT = GraphicsUtil.loadTexture("res/dirtblock.png");

	public Block(float x, float y, float z){
		this(x, y, z, GRASS);
	}
	
	public Block(float x, float y, float z, Texture texture){
		super(x, y, z);
		material.setTexture(texture);
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
