package net.pyraetos.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import net.pyraetos.pgenerate.PGenerate;
import net.pyraetos.util.Matrix;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Pyraetos3D {

	/*
	 * TODO:
	 * Implement depth culling
	 */
	
	//Constants
	public static final String TITLE = "Pyraetos3D";
	public static final int WIDTH = 2100;
	public static final int HEIGHT = 1400;
	public static final float FOV = Sys.toRadians(60.0f);
	public static final float FAR_CLIP = 500.0f;
	public static final float NEAR_CLIP = .05f;
	public static final long FRAME_TIME = 17l;
	public static final float SHADOW_BOX_WIDTH = 128f;
	public static final float SHADOW_BOX_HEIGHT = 128f;
	public static final float SHADOW_BOX_DEPTH = 128f;
	
	//Window
	private static long window;
	private static int width;
	private static int height;
	@SuppressWarnings("unused")
	private static boolean resized;
	
	//Input
	private static boolean mouseWheelUp;
	private static boolean mouseWheelDown;
	
	//Performance
	private static int pCounter = 0;
	private static int pMax = 10;
	private static long cumulative;
	
	//Projection
	private static float aspect;
	private static MatrixBuffer perspectiveMatrix;
	private static MatrixBuffer orthographicMatrix;
	private static Matrix frustumPH = new Matrix();
	private static Vector vecPH0 = new Vector(0,0,0);
	private static Vector vecPH1 = new Vector(0,0,0);
	
	//Camera
	private static Camera camera;
	private static float remainingPitch;
	
	//Light
	private static ShadowMap shadowMap;
	private static boolean shadowsEnabled;
	
	//Game objects
	private static Skybox skybox;
	private static Map<Mesh, Set<Model>> models;
	private static Region[][] regions;
	private static PGenerate pg;
	private static Set<int[]> beingGenerated;
	
	private static void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		width = WIDTH;
		height = HEIGHT;
		aspect = ((float)width) / ((float)height);
		
		// Window resize callback
		glfwSetWindowSizeCallback(window, (window, width, height) ->{
			Pyraetos3D.width = width;
			Pyraetos3D.height = height;
			resized = true;
		});
		
		// Mosuse wheel callback
		glfwSetScrollCallback(window, (window, x, y) ->{
			if(y < 0) mouseWheelDown = true;
			else if(y > 0) mouseWheelUp = true;
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		// Center our window
		glfwSetWindowPos(
			window,
			(vidmode.width() - WIDTH) / 2,
			(vidmode.height() - HEIGHT) / 2
		);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		//Initialize OpenGL
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glDepthFunc(GL_LEQUAL);
		
		//Start camera
		camera = new Camera();
		camera.setTranslation(0f, 5f, 10f);
	    
		//Initialize shadow map
		shadowMap = new ShadowMap();
		shadowsEnabled = true;

	    //Initialize projection
		perspectiveMatrix = new MatrixBuffer();
	    GraphicsUtil.perspectiveProjection(perspectiveMatrix, aspect, FOV, FAR_CLIP, NEAR_CLIP);
	    perspectiveMatrix.updateBuffer();
	    orthographicMatrix = new MatrixBuffer();
	    GraphicsUtil.orthographicProjection(orthographicMatrix, -SHADOW_BOX_WIDTH/2f, SHADOW_BOX_WIDTH/2f, -SHADOW_BOX_HEIGHT/2f, SHADOW_BOX_HEIGHT/2f, -SHADOW_BOX_DEPTH/2f, SHADOW_BOX_DEPTH/2f);
	    orthographicMatrix.updateBuffer();
	    
	    //Initialize skybox
	    skybox = new Skybox(Skybox.MORNING);
	    
	    //Initialize model map
	    models = new ConcurrentHashMap<Mesh, Set<Model>>();
	    
	    //Initialize regions
	    beingGenerated = Sys.concurrentSet(int[].class);
	    regions = new Region[1<<9][1<<9];
	    pg = new PGenerate(1<<14, 1<<14);
	    pg.setEntropy(9f);
	    for(int rx = -1; rx < 1; rx++){
	    	for(int ry = -1; ry < 1; ry++){
	    		genRegion(rx, ry);
		    }
	    }
	    
	    //GraphicsUtil.saveMesh(Skybox.MESH, "res/skybox/skybox.msh");
	    //GraphicsUtil.saveMesh(Block.MESH, "res/block.msh");
	    
	    //Begin the game loop
		loop();
	}
	
	private static void genRegion(int rx, int ry){
		int x = rx * 32;
		int y = ry * 32;
		Block[] blocks = new Block[32 * 32/* * 3*/];
		for(int i = 0; i < 32; i++){
			for(int j = 0; j < 32; j++){
				float bx = x + i;
				pg.generate((1<<13) + x + i, (1<<13) + y + j);
				float by = Math.round(pg.getValue((1<<13) + x + i, (1<<13) +  y + j));
				float bz = y + j;
				Block b = new Block(bx, by, bz);
				//Block b2 = new Block(bx, by - 1, bz, Block.DIRT);
				//Block b3 = new Block(bx, by - 2, bz, Block.DIRT);
				blocks[i * 32 + j] = b;
				//blocks[32 * 32 + i * 32 + j] = b2;
				//blocks[2 * 32 * 32 + i * 32 + j] = b3;
			}
		}
		Region r = new Region(blocks);
		regions[rx + (1<<8)][ry + (1<<8)] = r;
	}

	private static void genRegionAsync(int rx, int ry){
		if(beingGenerated.contains(new int[]{rx, ry}))
			return;
		beingGenerated.add(new int[]{rx, ry});
		Sys.thread(()->{
			genRegion(rx, ry);
			beingGenerated.remove(new int[]{rx, ry});
		});
	}
	
	public static void setShadowsEnabled(boolean enabled){
		shadowsEnabled = enabled;
	}
	
	public static void addModel(Model model){
		Mesh mesh = model.getMesh();
		if(!models.containsKey(mesh)){
			Set<Model> set = Sys.concurrentSet(Model.class);
			models.put(mesh, set);
			set.add(model);
		}else{
			models.get(mesh).add(model);
		}
	}
	
	public static void removeModel(Model model){
		Mesh mesh = model.getMesh();
		if(!models.containsKey(mesh))
			return;
		Set<Model> set = models.get(mesh);
		set.remove(model);
	}

	private static void windowInput(){
		checkResize();
		glfwPollEvents();
	}
	
	public static boolean isKeyPressed(int keyCode){
		return glfwGetKey(window, keyCode) == GLFW_PRESS;
	}
	
	private static void keyboardInput(){
		if(isKeyPressed(GLFW_KEY_W)){
			camera.translate(0f, 0f, -0.15f);
		}else if(isKeyPressed(GLFW_KEY_S)){
			camera.translate(0f, 0f, 0.15f);
		}
		if(isKeyPressed(GLFW_KEY_SPACE)){
			camera.translate(0f, 0.15f, 0.0f);
		}else if(isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
			camera.translate(0f, -0.15f, 0f);
		}
		if(isKeyPressed(GLFW_KEY_A)){
			updateCameraYaw(-0.05f);
		}else if(isKeyPressed(GLFW_KEY_D)){
			updateCameraYaw(0.05f);
		}
		if(isKeyPressed(GLFW_KEY_P)){
			setShadowsEnabled(!shadowsEnabled);
		}
	}
	
	private static void updateCameraYaw(float dyaw){
		camera.rotate(0f, dyaw, 0f);
	}

	private static void mouseInput(){
		if(mouseWheelUp){
			remainingPitch = remainingPitch >= 0 ? remainingPitch + 0.06f : 0.06f;
			mouseWheelUp = false;
		}
		if(mouseWheelDown){
			remainingPitch = remainingPitch <= 0 ? remainingPitch - 0.06f : -0.06f;
			mouseWheelDown = false;
		}
	}
	
	private static void input(){
		windowInput();
		keyboardInput();
		mouseInput();
	}
	
	private static void cameraPitch(){
		if(remainingPitch > 0.0f){
			camera.rotate(0.02f, 0f, 0f);
			remainingPitch -= 0.02f;
			if(remainingPitch <= 0.0f)
				remainingPitch = 0.0f;
		}
		else if(remainingPitch < 0.0f){
			camera.rotate(-0.02f, 0f, 0f);
			remainingPitch += 0.02f;
			if(remainingPitch >= 0.0f)
				remainingPitch = 0.0f;
		}
	}
	
	private static void updatePerformance(){
		if(++pCounter == pMax){
			long ms = cumulative / pMax;
			double fps = 1d / ((double)ms / 1000d);
			glfwSetWindowTitle(window, TITLE + " (" + Sys.round(fps) + " FPS) (" + camera.getYaw() + " YAW)");
			pCounter = 0;
			cumulative = 0l;
		}
	}
	
	private static void update(){
		cameraPitch();
		skybox.setTranslation(camera.getTranslation());
		updatePerformance();
	}

	private static boolean inFrustum(Matrix modelView){
		Matrix.multiply(perspectiveMatrix, modelView, frustumPH);
		float w = Matrix.multiply(frustumPH, vecPH0, 1, vecPH1);
		vecPH1.multiply(1f / w);
		float x = vecPH1.getX();
		float y = vecPH1.getY();
		float z = vecPH1.getZ();
		return x > -1.25f && x < 1.25f && y > -1.25f && y < 1.25f && z > -0.05f && z < 1.25f;
	}
	
	private static boolean isClose(Model m){
		return Sys.distanceFrom(camera.getTranslation(), m.getTranslation()) < 7f;
	}

	private static void clear(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private static void renderShadowMap(){
		if(!shadowsEnabled)
			return;
		shadowMap.bind();
		glViewport(0, 0, ShadowMap.WIDTH, ShadowMap.HEIGHT);
		glClear(GL_DEPTH_BUFFER_BIT);
		Shader.DEPTH.bind();
		Shader.DEPTH.setUniform("ortho", orthographicMatrix);
		Matrix lightViewMatrix = skybox.getLightViewMatrix();
		for(Mesh mesh : models.keySet()){
			mesh.bind();
			Set<Model> set = models.get(mesh);
			for(Model model : set){
				MatrixBuffer lightModelViewMatrix = model.getLightModelViewMatrix(lightViewMatrix);
				Shader.DEPTH.setUniform("lightMV", lightModelViewMatrix);
				model.renderIgnoreMaterial();
			}
			mesh.unbind();
		}
		Block.MESH.bind();
		int rx = toRegionCoord(camera.getX());
		int ry = toRegionCoord(camera.getZ());
		for(int ri = rx - 1; ri <= rx + 1; ri++){
			for(int rj = ry - 1; rj <= ry + 1; rj++){
				if(regions[ri + (1<<8)][rj + (1<<8)] == null){
					continue;
				}
				Region r = regions[ri + (1<<8)][rj + (1<<8)];
				for(Block block : r.getBlocks()){
					MatrixBuffer lightModelViewMatrix = block.getLightModelViewMatrix(lightViewMatrix);
					Shader.DEPTH.setUniform("lightMV", lightModelViewMatrix);
					block.renderIgnoreMaterial();
				}
			}
		}
		Block.MESH.unbind();
		Shader.DEPTH.unbind();
		shadowMap.unbind();
		shadowMap.getTexture().bindTextureInUnit(1);
		glViewport(0, 0, width, height);
	}
	
	private static void renderSkybox(){
		Shader.NO_LIGHT.bind();
		Shader.NO_LIGHT.setUniform("proj", perspectiveMatrix);
		MatrixBuffer modelViewMatrix = skybox.getModelViewMatrix(camera.getViewMatrix());
		Shader.NO_LIGHT.setUniform("modelView", modelViewMatrix);
		Shader.NO_LIGHT.setUniform("color", skybox.getMaterial().getColor());
		Shader.NO_LIGHT.setUniform("useColor", skybox.getMaterial().usesColor() ? 1 : 0);
		Shader.NO_LIGHT.setUniform("texture_sampler", 0);
		Skybox.MESH.bind();
		skybox.render();
		Skybox.MESH.unbind();
		Shader.NO_LIGHT.unbind();
	}

	private static int toRegionCoord(float coord){
		return coord < 0 ? (int)coord / 32 - 1 : (int)coord / 32;
	}
	
	private static void renderWorld(){
		Shader.WORLD.bind();
		setGlobalWorldUniforms();
		for(Mesh mesh : models.keySet()){
			mesh.bind();
			Set<Model> set = models.get(mesh);
			for(Model model : set){
				if(isClose(model) || inFrustum(model.getModelViewMatrix(camera.getViewMatrix()))){
					setModelWorldUniforms(model);
					model.render();
				}
			}
			mesh.unbind();
		}
		Block.MESH.bind();
		int rx = toRegionCoord(camera.getX());
		int ry = toRegionCoord(camera.getZ());
		for(int ri = rx - 1; ri <= rx + 1; ri++){
			for(int rj = ry -1; rj <= ry +1; rj++){
				if(regions[ri + (1<<8)][rj + (1<<8)] == null){
					genRegionAsync(ri, rj);
					continue;
				}
				Region r = regions[ri + (1<<8)][rj + (1<<8)];
				for(Block block : r.getBlocks()){
					if(isClose(block) || inFrustum(block.getModelViewMatrix(camera.getViewMatrix()))){
						setModelWorldUniforms(block);
						block.render();
					}
				}
			}
		}
		Block.MESH.unbind();
		Shader.WORLD.unbind();
	}
	
	private static void render(){
		clear();
		renderShadowMap();
		renderSkybox();
		renderWorld();
		glfwSwapBuffers(window);
	}
	
	private static void setGlobalWorldUniforms(){
		Shader.WORLD.setUniform("proj", perspectiveMatrix);
		Shader.WORLD.setUniform("ortho", orthographicMatrix);
		Shader.WORLD.setUniform("texture_sampler", 0);
		Shader.WORLD.setUniform("ambientLight", skybox.getAmbientLight());
		DirectionalLight sun = skybox.getSun();
		Vector dest = sun.getVectorDest();
		Vector dir = sun.getDirection();
		camera.view(dest, dir);
		Shader.WORLD.setUniform("directionalDir", dest);
		Shader.WORLD.setUniform("fogColor", skybox.getFogColor());
		Shader.WORLD.setUniform("fogDensity", .025f);
		Shader.WORLD.setUniform("useShadows", shadowsEnabled);
		Shader.WORLD.setUniform("shadowMap", 1);
	}
	
	private static void setModelWorldUniforms(Model model){
		Material mat = model.getMaterial();
		Shader.WORLD.setUniform("material.color", mat.getColor());
		Shader.WORLD.setUniform("material.useColor", mat.usesColor());
		//Shader.WORLD.setUniform("material.reflectance", mat.getReflectance());
		Matrix lightViewMatrix = skybox.getLightViewMatrix();
		MatrixBuffer lightModelViewMatrix = model.getLightModelViewMatrix(lightViewMatrix);
		Shader.WORLD.setUniform("lightMV", lightModelViewMatrix);
		Matrix viewMatrix = camera.getViewMatrix();
		MatrixBuffer modelViewMatrix = model.getModelViewMatrix(viewMatrix);
		Shader.WORLD.setUniform("modelView", modelViewMatrix);
	}
	
	private static void checkResize(){
		if(resized = true){
			glViewport(0, 0, width, height);
			aspect = ((float)width) / ((float)height);
		    GraphicsUtil.perspectiveProjection(perspectiveMatrix, aspect, FOV, FAR_CLIP, NEAR_CLIP);
		    perspectiveMatrix.updateBuffer();
			resized = false;
		}
	}
	
	private static void loop(){
		while (!glfwWindowShouldClose(window)) {
			long beginTime = Sys.time();
			input();
			update();
			render();
			long endTime = Sys.time();
			Sys.sleep(beginTime + Pyraetos3D.FRAME_TIME - endTime);
			cumulative += Sys.time() - beginTime;
		}
		cleanup();
	}
	
	public static void cleanup(){
		Shader.WORLD.cleanup();
		Shader.DEPTH.cleanup();
		Shader.NO_LIGHT.cleanup();
		Skybox.MESH.cleanup();
		Block.MESH.cleanup();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static long getWindow(){
		return window;
	}
	
	public static void main(String[] args) {
		init();
	}
}
