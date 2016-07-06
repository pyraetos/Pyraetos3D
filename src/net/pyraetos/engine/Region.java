package net.pyraetos.engine;

public class Region{

	private Block blocks[];
	
	public Region(Block[] blocks){
		this.blocks = blocks;
	}

	public Block[] getBlocks(){
		return blocks;
	}

	public void setBlocks(Block[] blocks){
		this.blocks = blocks;
	}
	
}
