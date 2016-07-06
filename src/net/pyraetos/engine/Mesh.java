package net.pyraetos.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("serial")
public class Mesh implements Serializable{

    private transient int vaoId;
    private transient int vboId;
    private transient int iboId;
    private transient int tboId;
    private transient int nboId;
    
    private int vertexCount;
    
    private float[] vertices;
    private int[] indices;
    private float[] textureCoordinates;
    private float[] normals;

    //For transient initialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	in.defaultReadObject();
    	init();
    }
    
    public Mesh(float[] vertices, int[] indices, float[] textureCoordinates, float[] normals){
    	this.vertices = vertices;
    	this.indices = indices;
    	this.textureCoordinates = textureCoordinates;
    	this.normals = normals;
    	vertexCount = vertices.length / 3;
    	init();
    }
    
    private void init(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        FloatBuffer vbuf = GraphicsUtil.toBuffer(vertices);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        iboId = glGenBuffers();
        IntBuffer ibuf = GraphicsUtil.toBuffer(indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        tboId = glGenBuffers();
        FloatBuffer tbuf = GraphicsUtil.toBuffer(textureCoordinates);
        glBindBuffer(GL_ARRAY_BUFFER, tboId);
        glBufferData(GL_ARRAY_BUFFER, tbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        nboId = glGenBuffers();
        FloatBuffer nbuf = GraphicsUtil.toBuffer(normals);
        glBindBuffer(GL_ARRAY_BUFFER, nboId);
        glBufferData(GL_ARRAY_BUFFER, nbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindVertexArray(0);
    }
    
    public void bind(){
    	glBindVertexArray(vaoId);
    	glEnableVertexAttribArray(0);
    	glEnableVertexAttribArray(1);
    	glEnableVertexAttribArray(2);
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
    }

    public void render(){
    	glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    public void unbind(){
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    	glDisableVertexAttribArray(2);
    	glDisableVertexAttribArray(1);
    	glDisableVertexAttribArray(0);
		glBindVertexArray(0);
    }

    public float[] getVertexArray(){
    	return vertices;
    }
    
    public int[] getIndexArray(){
    	return indices;
    }
    
    public float[] getNormalArray(){
    	return normals;
    }
    
    public float[] getTextureCoordinateArray(){
    	return textureCoordinates;
    }
    
    public int getVaoId(){
        return vaoId;
    }

    public int getVertexCount(){
        return vertexCount;
    }
    
    public int getIndexCount(){
    	return indices.length;
    }
    
    public int getTextureCoordinateCount(){
    	return textureCoordinates.length;
    }
    
    public void cleanup(){
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        glDeleteBuffers(iboId);
        glDeleteBuffers(tboId);
        glDeleteBuffers(nboId);
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}