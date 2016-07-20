package net.pyraetos.engine;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Vector;

@SuppressWarnings("serial")
public class Skybox extends Model{
	
	public static final int DAWN = 0;
	public static final int MORNING = 1;
	public static final int DAY = 2;
	public static final int SUNSET = 3;
	public static final int DESERT = 4;
	
	public static Mesh MESH = GraphicsUtil.loadMesh("res/skybox/skybox.msh");
	
	private Vector fogColor;
	private Vector ambientColor;
	private DirectionalLight light;

	public Skybox(){
		this(DAY);
	}
	
	public Skybox(int texture){
		super();
		Texture t;
		switch(texture){
		case DAWN: t = GraphicsUtil.loadTexture("res/skybox/dawn.png");
			fogColor = new Vector(0.427f, 0.424f, 0.478f);
			ambientColor = new Vector(0.4f, 0.25f, 0.15f);
			light = new DirectionalLight(new Vector(-0.666667f, 0.333333f, -0.666667f));
			break;
		case MORNING: t = GraphicsUtil.loadTexture("res/skybox/morning.png");
			fogColor = new Vector(.847f, .8f, .69f);
			ambientColor = new Vector(0.35f, 0.3f, 0.2f);
			light = new DirectionalLight(new Vector(0,1f,0));/*(-0.615457f, 0.492366f, -0.615457f));*/
			break;
		case DAY: t = GraphicsUtil.loadTexture("res/skybox/day.png");
			fogColor = new Vector(0.5686f, 0.549f, 0.506f);
			ambientColor = new Vector(0.35f, 0.35f, 0.3f);
			light = new DirectionalLight(new Vector(-0.301511f, 0.904534f, -0.301511f));
			break;
		case SUNSET: t = GraphicsUtil.loadTexture("res/skybox/sunset.png");
			fogColor = new Vector(0.427f, 0.424f, 0.478f);
			ambientColor = new Vector(0.45f, 0.2f, 0.2f);
			light = new DirectionalLight(new Vector(-0.666667f, 0.333333f, -0.666667f));
			break;
		default: t = GraphicsUtil.loadTexture("res/skybox/day.png");
		fogColor = new Vector(0.5686f, 0.549f, 0.506f);
			ambientColor = new Vector(0.35f, 0.35f, 0.3f);
			light = new DirectionalLight(new Vector(-0.301511f, 0.904534f, -0.301511f));
		}
		material.setTexture(t);
		setScale(200f);
		update();
	}
	
	@Override
	public void setTranslation(float x, float y, float z){
		super.setTranslation(x, y, z);
		light.setPosition(x, z);
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		light.setX(x);
	}
	
	@Override
	public void setZ(float z){
		super.setZ(z);
		light.setZ(z);
	}
	
	@Override
	public void render(){
    	material.getTexture().bind();
		MESH.render();
		Texture.unbind();
	}

	public Vector getFogColor(){
		return fogColor;
	}

	public void setFogColor(Vector fogColor){
		this.fogColor = fogColor;
	}
	
	public DirectionalLight getSun(){
		return light;
	}

	public Vector getAmbientLight(){
		return ambientColor;
	}

	public Matrix getLightViewMatrix(){
		return light.getLightViewMatrix();
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
