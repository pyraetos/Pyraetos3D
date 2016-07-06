package net.pyraetos.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("unused")
public class ShadowMap{

	public static final int WIDTH = 1 << 12;
	public static final int HEIGHT = 1 << 12;
	
	private int fbo;
	private Texture texture;
	
	public ShadowMap(){
		fbo = glGenFramebuffers();
		texture = new Texture(WIDTH, HEIGHT, GL_DEPTH_COMPONENT);
		bind();
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture.getId(), 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		unbind();
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public int getFramebufferObject(){
		return fbo;
	}
	
	public void bind(){
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public void unbind(){
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void cleanup(){
		glDeleteFramebuffers(fbo);
		texture.cleanup();
	}
	
}
