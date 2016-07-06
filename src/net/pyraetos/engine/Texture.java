package net.pyraetos.engine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("serial")
public class Texture implements Serializable{

	private transient ByteBuffer buf;
	private byte[] serialData;
	private transient int tid;
	private int width;
	private int height;
	
	public static Texture BOUND_TEXTURE = null;
	public static int ACTIVE_TEXTURE_UNIT = 0;
	
	static{
		activateTextureUnit(0);
	}

	//For transient initialization
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		buf = ByteBuffer.allocateDirect(4 * width * height);
		for(byte b : serialData){
			buf.put(b);
		}
		buf.flip();
		tid = glGenTextures();
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glGenerateMipmap(GL_TEXTURE_2D);
		unbind();
	}
	
	public static void activateTextureUnit(int textureUnit){
		if(ACTIVE_TEXTURE_UNIT == textureUnit)
			return;
		ACTIVE_TEXTURE_UNIT = textureUnit;
		int constant = GL_TEXTURE0 + textureUnit;
		glActiveTexture(constant);
	}
	
	public Texture(int width, int height, int pixelFormat){
		tid = glGenTextures();
		this.width = width;
		this.height = height;
		bind();
		glTexImage2D(GL_TEXTURE_2D, 0, pixelFormat, width, height, 0, pixelFormat, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
	    float borderColor[] = {1.0f, 1.0f, 1.0f, 1.0f};
	    glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
		unbind();
	}
	
	public Texture(BufferedImage image){
		width = image.getWidth();
		height = image.getHeight();
		buf = ByteBuffer.allocateDirect(4 * width * height);
		serialData = new byte[4 * width * height];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int argb = image.getRGB(x, y);
				byte r = (byte)(argb >> 16);
				byte g = (byte)(argb >> 8);
				byte b = (byte)argb;
				byte a = (byte)(argb >> 24);
				buf.put(r);
				buf.put(g);
				buf.put(b);
				buf.put(a);
				int i = y * width + x * 4;
				serialData[i] = r;
				serialData[i + 1] = g;
				serialData[i + 2] = b;
				serialData[i + 3] = a;
			}
		}
		buf.flip();
		tid = glGenTextures();
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glGenerateMipmap(GL_TEXTURE_2D);
		unbind();
	}
	
	public int getId(){
		return tid;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}

	public void setInterpolation(int interp){
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, interp);
		unbind();
	}
	
	public void bind(){
		if(BOUND_TEXTURE == this)
			return;
		glBindTexture(GL_TEXTURE_2D, tid);
		BOUND_TEXTURE = this;
	}
	
	public void bindTextureInUnit(int textureUnit){
		int old = ACTIVE_TEXTURE_UNIT;
		activateTextureUnit(textureUnit);
		if(BOUND_TEXTURE == this)
			return;
		glBindTexture(GL_TEXTURE_2D, tid);
		BOUND_TEXTURE = this;
		activateTextureUnit(old);
	}
	
	public static void unbind(){
		if(BOUND_TEXTURE == null)
			return;
		glBindTexture(GL_TEXTURE_2D, 0);
		BOUND_TEXTURE = null;
	}
	
	public void cleanup(){
		glDeleteTextures(tid);
	}
	
}
