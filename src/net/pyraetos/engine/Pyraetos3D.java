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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Pyraetos3D {

	/*
	 * TODO:
	 * Fix FPS shadow issue. Examine ortho texture in quad
	 * Test ways to add more underground blocks
	 * Improve polymorphism of model classes
	 * Trees
	 */
	
	//Constants
	public static final String TITLE = "Pyraetos3D";
	public static final int WIDTH = 2100;
	public static final int HEIGHT = 1400;
	public static final float FOV = Sys.toRadians(60.0f);
	public static final float FAR_CLIP = 500.0f;
	public static final float NEAR_CLIP = .05f;
	public static final long FRAME_TIME = 16l;
	public static final float SHADOW_BOX_WIDTH = 128f;
	public static final float SHADOW_BOX_HEIGHT = 128f;
	public static final float SHADOW_BOX_DEPTH = 128f;
	public static final float GRAV_ACCEL = -.003f;
	
	//Behavioral constants
	//MODES:
	//0 - Region-based, infinite, procedural
	//1 - SoundTown
	public static final int MODE = 1;
	
	//Window
	private static long window;
	private static int width;
	private static int height;
	@SuppressWarnings("unused")
	private static boolean resized;
	
	//Input
	private static boolean mouseWheelUp;
	private static boolean mouseWheelDown;
	private static Set<Integer> keysDown;
	
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
	public static Camera camera;
	private static float remainingPitch;
	
	//Light
	private static DepthMap shadowMap;
	private static boolean shadowsEnabled;
	
	//Game objects
	private static Skybox skybox;
	private static Map<Mesh, Set<Model>> models;
	public static Set<CollisionPlane> colls = new HashSet<CollisionPlane>();
	private static Region[][] regions;
	private static PGenerate pg;
	private static Set<int[]> beingGenerated;
	
	//Game variables
	private static boolean flight = true;
	private static float gravVelocity = 0f;

	//SoundTown variables
	public static int myID = -1;
	public static MyClientSocket client;
	public static Player other;
	public static long lastup = 0;
	public static Scanner scanner = new Scanner(System.in);
	public static LinkedList<String> scaninput = new LinkedList<String>();
	public static Vector kp1 = new Vector(1f, 0f, -95.98f);
	public static Door p1d1;
	public static Door p1d2;
	public static Door p2d1;
	public static Door p2d2;
	private static boolean win = false;
	public static Sound sound;
	
	public static void win(){
		win = true;
	}
	
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
		//glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		//Initialize OpenGL
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glDepthFunc(GL_LEQUAL);
		
		//Initialize keys down set
		keysDown = new HashSet<Integer>();
		
		//Start camera
		camera = new Camera();
		camera.setTranslation(0f, 5f, 10f);
	    
		//Initialize shadow map
		shadowMap = new DepthMap();
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
	    genStartup();
	    
	    //add render to texture debugging quad
	    //Quad quad = new Quad(0f, 0f, -3f);
	    //quad.rotate(Sys.PI/2f, 0f, 0f);
	    //quad.getMaterial().setTexture(shadowMap.getTexture());
	    //addModel(quad);
	    
	    //GraphicsUtil.saveMesh(Skybox.MESH, "res/skybox/skybox.msh");
	    //GraphicsUtil.saveMesh(Block.MESH, "res/block.msh");

	    //if soundtown
	    if(MODE == 1){
	    	flight = false;
	    	client = new MyClientSocket();
	    	client.send(new LoginPacket());
	    	initScanner();
	    	Sys.thread(sound);
	    }
	    
	    //Begin the game loop
	    loop();
	}

	private static void initScanner(){
		Sys.thread(()->{
			while(true){
				scaninput.offer(scanner.nextLine());
			}
		});
	}
	
	private static void genStartup(){
		switch(MODE){
		case(0):{
			beingGenerated = Sys.concurrentSet(int[].class);
			regions = new Region[1<<9][1<<9];
			pg = new PGenerate(1<<14, 1<<14);
			pg.setEntropy(6f);//Default 9
			for(int rx = -1; rx < 1; rx++){
				for(int ry = -1; ry < 1; ry++){
					genRegion(rx, ry);
				}
			}
		}
		case(1):{
			makeP1Part();
			makeP2Part();
			shadowsEnabled = false;
		}
		}
	}
	
	private static void makeP1Part(){
		ShortHallway hw = new ShortHallway(0f, 0f);
		for(WallSegment ws : hw.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw.getColls())
			colls.add(c);
		
		Room r = new Room(-9f, -16f);
		for(WallSegment ws : r.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : r.getColls())
			colls.add(c);
		
		p1d1 = new Door(.5f, -32f);
		for(Model m : p1d1.getModels())
			addModel(m);
		for(CollisionPlane c : p1d1.getColls())
			colls.add(c);
		
		ShortHallway hw2 = new ShortHallway(0f, -32f);
		for(WallSegment ws : hw2.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw2.getColls())
			colls.add(c);
		
		Room r2 = new Room(-9f, -48f);
		for(WallSegment ws : r2.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : r2.getColls())
			colls.add(c);
		
		p1d2 = new Door(.5f, -64f);
		for(Model m : p1d2.getModels())
			addModel(m);
		for(CollisionPlane c : p1d2.getColls())
			colls.add(c);
		
		ShortHallway hw3 = new ShortHallway(0f, -64f);
		for(WallSegment ws : hw3.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw3.getColls())
			colls.add(c);
		
		RoomOneDoor r3 = new RoomOneDoor(-9f, -80f);
		for(WallSegment ws : r3.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : r3.getColls())
			colls.add(c);
		
		Keypad kp = new Keypad(kp1.getX(), kp1.getY(), kp1.getZ());
		for(Model m : kp.getModels())
			addModel(m);
	}
	
	private static void makeP2Part(){
		float disp = 30f;
		ShortHallway hw2 = new ShortHallway(disp + 0f, 0f);
		for(WallSegment ws : hw2.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw2.getColls())
			colls.add(c);
		
		Room r = new Room(disp + -9f, -16f);
		for(WallSegment ws : r.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : r.getColls())
			colls.add(c);
		
		p2d1 = new Door(disp + .5f, -32f);
		for(Model m : p2d1.getModels())
			addModel(m);
		for(CollisionPlane c : p2d1.getColls())
			colls.add(c);
		
		ShortHallway hw = new ShortHallway(disp + 0f, -32f);
		for(WallSegment ws : hw.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw.getColls())
			colls.add(c);
		
		Room r2 = new Room(disp + -9f, -48f);
		for(WallSegment ws : r2.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : r2.getColls())
			colls.add(c);
		
		p2d2 = new Door(disp + .5f, -64f);
		for(Model m : p2d2.getModels())
			addModel(m);
		for(CollisionPlane c : p2d2.getColls())
			colls.add(c);
		
		Hallway hw3 = new Hallway(disp + 0f, -64f);
		for(WallSegment ws : hw3.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : hw3.getColls())
			colls.add(c);
		
		RoomOneDoor rod = new RoomOneDoor(disp + -9f, -96f);
		for(WallSegment ws : rod.getWalls())
			for(Model m : ws.getModels())
				addModel(m);
		for(CollisionPlane c : rod.getColls())
			colls.add(c);
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

	private static int toRegionCoord(float coord){
		return coord < 0 ? (int)coord / 32 - 1 : (int)coord / 32;
	}
	
	private static void windowInput(){
		checkResize();
		glfwPollEvents();
	}
	
	private static long lastSpaceReleased = 0;
	private static boolean spaceIsReleased = false;
	private static long lastSpacePressed = 0;
	private static boolean checkToggleFlightCondition(){
		return lastSpacePressed < lastSpaceReleased && (Sys.time() - lastSpacePressed < 500) && (Sys.time() - lastSpaceReleased < 500);
	}
	
	private static void toggleFlight(){
		flight = !flight;
		gravVelocity = 0f;
	}
	
	public static boolean isKeyPressed(int keyCode){
		return glfwGetKey(window, keyCode) == GLFW_PRESS;
	}
	
	private static void keyboardInput(){
		float spd = flight ? 0.15f : 0.07f;
		if(isKeyPressed(GLFW_KEY_W)){
			moveWithCollisions(0f, 0f, -spd);
		}else if(isKeyPressed(GLFW_KEY_S)){
			moveWithCollisions(0f, 0f, spd);
		}
		if(isKeyPressed(GLFW_KEY_SPACE)){
			if(checkToggleFlightCondition()){
				toggleFlight();
				lastSpaceReleased = 0;
			}else{
				if(flight) moveWithCollisions(0f, 0.15f, 0.0f);
			}
			if(spaceIsReleased){
				lastSpacePressed = Sys.time();
				spaceIsReleased = false;
			}
		}else{
			if(!spaceIsReleased){
				lastSpaceReleased = Sys.time();
				spaceIsReleased = true;
			}
			if(isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
				if(flight) moveWithCollisions(0f, -0.15f, 0f);
			}
		}
		if(isKeyPressed(GLFW_KEY_A)){
			updateCameraYaw(-0.05f);
		}else if(isKeyPressed(GLFW_KEY_D)){
			updateCameraYaw(0.05f);
		}
		if(isKeyPressed(GLFW_KEY_P)){
			if(!keysDown.contains(GLFW_KEY_P)){
				setShadowsEnabled(!shadowsEnabled);
				keysDown.add(GLFW_KEY_P);
			}
		}else{
			keysDown.remove(GLFW_KEY_P);
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

	private static void scannerInput(){
		if(scaninput.isEmpty()){
			return;
		}
		if(Sys.distanceFrom(camera.getTranslation(), kp1) < 2f){
			String raw = scaninput.poll();
			if(raw.length() == 4){
				try{
					int code = Integer.parseInt(raw);
					Sys.debug("You have entered \"" + code + "\" into the keypad!");
					client.send(new ActionPacket("kp1" + raw));
				}catch(Exception e){}
			}
		}else{
			scaninput.clear();
		}
	}

	private static void input(){
		if(MODE == 1){
			scannerInput();
		}
		windowInput();
		keyboardInput();
		mouseInput();
	}
	
	private static void moveWithCollisions(float dx, float dy, float dz){
		Vector curr = new Vector(camera.getX(), camera.getY(), camera.getZ());
		camera.translate(dx, dy, dz);
		Vector fut = camera.getTranslation();
		
		for(CollisionPlane coll : colls){
			if(coll.collides(curr, fut)){
				if(coll.normal == 'x')
					camera.setTranslation(curr.getX(), camera.getY(), camera.getZ());
				if(coll.normal == 'y'){
					camera.setTranslation(camera.getX(), curr.getY(), camera.getZ());
					gravVelocity = 0f;
				}
				if(coll.normal == 'z')
					camera.setTranslation(camera.getX(), camera.getY(), curr.getZ());
			}
		}
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
	
	private static void applyGravity(){
		gravVelocity += GRAV_ACCEL;
		moveWithCollisions(0f, gravVelocity, 0f);
	}
	
	private static void updatePacket(){
		if(System.currentTimeMillis() - lastup > 100){
			client.send(new UpdatePacket());
			lastup = System.currentTimeMillis();
		}
	}
	
	private static void update(){
		cameraPitch();
		if(!flight)
			applyGravity();
		skybox.setTranslation(camera.getTranslation());
		if(MODE != 1){
			for(Set<Model> set : models.values())
				for(Model m : set)
					m.setTranslation(camera.getX(), camera.getY(), camera.getZ() - 3);
		}else{
			while(myID == -1){
				Sys.sleep(200);
			}
			if(win){
    			YouWin yw = new YouWin(2f, 0f, -95.98f);
    			for(Model m : yw.getModels()) Pyraetos3D.addModel(m);
    			win = false;
			}
			updatePacket();
		}
		updatePerformance();
	}

	private static boolean inFrustum(Matrix modelView){
		Matrix.multiply(perspectiveMatrix, modelView, frustumPH);
		float w = Matrix.multiply(frustumPH, vecPH0, 1, vecPH1);
		vecPH1.multiply(1f / w);
		float x = vecPH1.getX();
		float y = vecPH1.getY();
		float z = vecPH1.getZ();
		return x > -1.3f && x < 1.3f && y > -1.3f && y < 1.3f && z > -0.05f && z < 1.3f;
	}
	
	private static boolean isClose(Model m){
		return Sys.distanceFrom(camera.getTranslation(), m.getTranslation()) < 7f;
	}

	private static void clear(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private static void setGlobalWorldUniforms(){
		Shader.WORLD.setUniform("proj", perspectiveMatrix);
		Shader.WORLD.setUniform("ortho", orthographicMatrix);
		Shader.WORLD.setUniform("texture_sampler", 0);
		if(MODE == 1){
			Shader.WORLD.setUniform("ambientLight", new Vector(.225f,.225f,.225f));
		}else{
			Shader.WORLD.setUniform("ambientLight", skybox.getAmbientLight());
		}
		DirectionalLight sun = skybox.getSun();
		Vector dest = sun.getVectorDest();
		Vector dir = sun.getDirection();
		camera.view(dest, dir);
		Shader.WORLD.setUniform("directionalDir", dest);
		Shader.WORLD.setUniform("dirColor", new Vector(1.3f,1.3f,1.3f));
		if(MODE == 1){
			Shader.WORLD.setUniform("useDirectionalLight", 0);
		}else{
			Shader.WORLD.setUniform("useDirectionalLight", 1);
		}
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
	
	private static void renderDepthMap(DepthMap depthMap, boolean ortho, boolean light, int unit){
		depthMap.bind();
		glViewport(0, 0, DepthMap.WIDTH, DepthMap.HEIGHT);
		glClear(GL_DEPTH_BUFFER_BIT);
		Shader.DEPTH.bind();
		Shader.DEPTH.setUniform("ortho", ortho ? orthographicMatrix : perspectiveMatrix);
		Shader.DEPTH.setUniform("isOrtho", ortho);
		Matrix viewMatrix = light ? skybox.getLightViewMatrix() : camera.getViewMatrix();
		
		//Render models to texture
		for(Mesh mesh : models.keySet()){
			mesh.bind();
			Set<Model> set = models.get(mesh);
			for(Model model : set){
				if(light || isClose(model) || inFrustum(model.getModelViewMatrix(camera.getViewMatrix()))){
					MatrixBuffer modelViewMatrix = light ? model.getLightModelViewMatrix(viewMatrix) : model.getModelViewMatrix(viewMatrix);
					Shader.DEPTH.setUniform("mv", modelViewMatrix);
					model.renderIgnoreMaterial();
				}
			}
			mesh.unbind();
		}
		
		//Render blocks
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
					if(light || isClose(block) || inFrustum(block.getModelViewMatrix(viewMatrix))){
						MatrixBuffer modelViewMatrix = light ? block.getLightModelViewMatrix(viewMatrix) : block.getModelViewMatrix(viewMatrix);
						Shader.DEPTH.setUniform("mv", modelViewMatrix);
						block.renderIgnoreMaterial();
					}
				}
			}
		}
		Block.MESH.unbind();
		Shader.DEPTH.unbind();
		depthMap.unbind();
		depthMap.getTexture().bindTextureInUnit(unit);
		glViewport(0, 0, width, height);
	}

	private static void renderShadowMap(){
		if(!shadowsEnabled || MODE == 1)
			return;
		renderDepthMap(shadowMap, true, true, 1);
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
	
	private static void renderWorld(){
		Shader.WORLD.bind();
		setGlobalWorldUniforms();
		
		//Iterate through models sets and render
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

		//Iterate through all regions and render blocks
		if(MODE == 0){
			Block.MESH.bind();
			int rx = toRegionCoord(camera.getX());
			int ry = toRegionCoord(camera.getZ());
			for(int ri = rx - 1; ri <= rx + 1; ri++){
				for(int rj = ry -1; rj <= ry + 1; rj++){
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
		}
		Shader.WORLD.unbind();
	}
	
	private static void render(){
		clear();
		renderShadowMap();
		renderSkybox();
		renderWorld();
		glfwSwapBuffers(window);
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
