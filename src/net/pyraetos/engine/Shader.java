package net.pyraetos.engine;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class Shader{

	public static final Shader WORLD;
	public static final Shader NO_LIGHT;
	public static final Shader DEPTH;
	
	static{
		WORLD = new Shader();
		NO_LIGHT = new Shader();
		DEPTH = new Shader();
		initWorldShader();
		initNoLightShader();
		initDepthShader();
	}
	
	private static void initWorldShader(){
		WORLD.createVertexShader(Sys.load("res/vertex.vs"));
		WORLD.createFragmentShader(Sys.load("res/fragment.fs"));
		WORLD.link();
		WORLD.createUniform("proj");
		WORLD.createUniform("modelView");
		WORLD.createUniform("texture_sampler");
		WORLD.createUniform("ambientLight");
		WORLD.createUniform("directionalDir");
		WORLD.createUniform("fogColor");
		WORLD.createUniform("fogDensity");
		WORLD.createUniform("lightMV");
		WORLD.createUniform("ortho");
		WORLD.createUniform("shadowMap");
		WORLD.createUniform("useShadows");
		//WORLD.createUniform("pointLight.color");
		//WORLD.createUniform("pointLight.position");
		//WORLD.createUniform("pointLight.intensity");
		//WORLD.createUniform("pointLight.specularPower");
		//WORLD.createUniform("pointLight.att.constant");
		//WORLD.createUniform("pointLight.att.linear");
		//WORLD.createUniform("pointLight.att.exponent");
		WORLD.createUniform("material.color");
		WORLD.createUniform("material.useColor");
		//WORLD.createUniform("material.reflectance");
	}
	
	private static void initNoLightShader(){
		NO_LIGHT.createVertexShader(Sys.load("res/vertexNoLight.vs"));
		NO_LIGHT.createFragmentShader(Sys.load("res/fragmentNoLight.fs"));
		NO_LIGHT.link();
		NO_LIGHT.createUniform("proj");
		NO_LIGHT.createUniform("modelView");
		NO_LIGHT.createUniform("texture_sampler");
		NO_LIGHT.createUniform("useColor");
		NO_LIGHT.createUniform("color");
	}
	
	private static void initDepthShader(){
		DEPTH.createVertexShader(Sys.load("res/vertexDepth.vs"));
		DEPTH.createFragmentShader(Sys.load("res/fragmentDepth.fs"));
		DEPTH.link();
		DEPTH.createUniform("lightMV");
		DEPTH.createUniform("ortho");
		DEPTH.createUniform("isOrtho");
	}
	
	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;
	private Map<String, Integer> uniforms;

	public Shader(){
		programId = glCreateProgram();
		if(programId == 0){
			throw new RuntimeException("Exception encountered while creating shader!");
		}
		uniforms = new HashMap<String, Integer>();
	}

	public void createVertexShader(String shaderCode){
		try{
			vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void createFragmentShader(String shaderCode){
		try{
			fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected int createShader(String shaderCode, int shaderType){
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0){
			throw new RuntimeException("Error creating shader. Code: " + shaderId);
		}

		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);

		if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0){
			throw new RuntimeException("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void createUniform(String name){
		int uid = glGetUniformLocation(programId, name);
		if(uid < 0){
			throw new RuntimeException("No such uniform: " + name);
		}
		uniforms.put(name, uid);
	}
	
	public void setUniform(String name, MatrixBuffer value){
		checkUniform(name);
		glUniformMatrix4fv(uniforms.get(name), false, value.getBuffer());
	}
	
	public void setUniform(String name, Vector value){
		checkUniform(name);
		glUniform3f(uniforms.get(name), value.getX(), value.getY(), value.getZ());
	}
	
	public void setUniform(String name, int value){
		checkUniform(name);
		glUniform1i(uniforms.get(name), value);
	}
	
	public void setUniform(String name, boolean value){
		checkUniform(name);
		glUniform1i(uniforms.get(name), value ? 1 : 0);
	}
	
	public void setUniform(String name, float value){
		checkUniform(name);
		glUniform1f(uniforms.get(name), value);
	}
	
	private void checkUniform(String name){
		if(!uniforms.containsKey(name))
			throw new RuntimeException("Uniform " + name + " was not created!");
	}
	
	public void link(){
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0){
			throw new RuntimeException("Error linking shader code: " + glGetShaderInfoLog(programId, 1024));
		}

		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0){
			System.err.println("Warning validating shader code: " + glGetShaderInfoLog(programId, 1024));
		}
	}

	public void bind(){
		glUseProgram(programId);
	}

	public void unbind(){
		glUseProgram(0);
	}

	public void cleanup(){
		unbind();
		if(programId != 0){
			if(vertexShaderId != 0){
				glDetachShader(programId, vertexShaderId);
			}
			if(fragmentShaderId != 0){
				glDetachShader(programId, fragmentShaderId);
			}
			glDeleteProgram(programId);
		}
	}
}