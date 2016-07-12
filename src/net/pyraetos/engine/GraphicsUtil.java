package net.pyraetos.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import net.pyraetos.util.Matrix;
import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public abstract class GraphicsUtil{

	public static Mesh loadMesh(String dir){
		if(dir.endsWith(".obj"))
			return loadMeshObj(dir);
		else if(dir.endsWith(".msh"))
			return loadMeshMsh(dir);
		return null;
	}
	
	private static Mesh loadMeshMsh(String dir){
		try{
			File file = new File(dir);
			if(!file.exists())
				return null;
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			Mesh mesh = (Mesh)in.readObject();
			in.close();
			return mesh;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void saveMesh(Mesh mesh, String dir){
		try{
			File file = new File(dir);
			if(!file.exists())
				file.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(mesh);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static Mesh loadMeshObj(String dir){
		try{
			String s = Sys.load(dir);
			String[] lines;
			if(s.contains("\r\n")){
				lines = s.split("\r\n");
			}else{
				lines = s.split("\n");
			}
			List<Vector> vertices = new ArrayList<Vector>();
			List<float[]> texcoords = new ArrayList<float[]>();
			List<Vector> normals = new ArrayList<Vector>();
			List<Face> faces = new ArrayList<Face>();
			for (String line : lines) {
				String[] tokens = line.split("\\s+");
				switch (tokens[0]) {
				case "v":
					Vector vec = new Vector(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3]));
					vertices.add(vec);
					break;
				case "vt":
					float[] arr = {Float.parseFloat(tokens[1]),
								Float.parseFloat(tokens[2])};
					texcoords.add(arr);
					break;
				case "vn":
					// Vertex normal
					Vector vec2 = new Vector(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3]));
					normals.add(vec2);
					break;
				case "f":
					Face face = new Face(tokens[1], tokens[2], tokens[3]);
					faces.add(face);
					break;
				default:
					break;
				}
			}
			return toMesh(vertices, texcoords, normals, faces);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static float[] toArray(List<Vector> list){
		float f[] = new float[list.size() * 3];
		for(int i = 0; i < list.size(); i++){
			Vector v = list.get(i);
			f[i * 3] = v.getX();
			f[i * 3 + 1] = v.getY();
			f[i * 3 + 2] = v.getZ();
		}
		return f;
	}
	
	private static Mesh toMesh(List<Vector> vertices, List<float[]> texList, List<Vector> normals, List<Face> faces){
		float[] vertexArray = toArray(vertices);
		float[] texArray = new float[vertices.size() * 2];
		float[] normalArray = new float[vertices.size() * 3];
		List<Integer> indices = new ArrayList<Integer>();
		for(Face face : faces){
			for(int[] group : face.indexGroups){
				int vi = group[0];
				indices.add(vi);
				if(group[1] >= 0){
					float[] tex = texList.get(group[1]);
					texArray[vi * 2] = tex[0];
					texArray[vi * 2 + 1] = 1f - tex[1];
				}
				if(group[2] >= 0){
					Vector norm = normals.get(group[2]);
					normalArray[vi * 3] = norm.getX();
					normalArray[vi * 3 + 1] = norm.getY();
					normalArray[vi * 3 + 2] = norm.getZ();
				}
			}
		}
		int[] indexArray = new int[indices.size()];
		for(int i = 0; i < indices.size(); i++)
			indexArray[i] = indices.get(i);
		return new Mesh(vertexArray, indexArray, texArray, normalArray);
	}
	
	private static class Face{
		
		public int[][] indexGroups;
		
		public Face(String i1, String i2, String i3){
			int[][] i = {parse(i1), parse(i2), parse(i3)};
			indexGroups = i;
		}
		
		public int[] parse(String s) {
	        String[] tokens = s.split("/");
	        int length = tokens.length;
	        int indexGroup[] = {-1, -1, -1};
	        indexGroup[0] = Integer.parseInt(tokens[0]) - 1;
	        if (length > 1){
	            String texCoord = tokens[1];
	            indexGroup[1] = texCoord.length() > 0 ? Integer.parseInt(texCoord) - 1 : -1;
	            if (length > 2) {
	                indexGroup[2] = Integer.parseInt(tokens[2]) - 1;
	            }
	        }
	        return indexGroup;
	    }
		
	}
	
	public static FloatBuffer toBuffer(float[] array){
		FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
		buf.put(array).flip();
		return buf;
	}

	public static IntBuffer toBuffer(int[] array){
		IntBuffer buf = BufferUtils.createIntBuffer(array.length);
		buf.put(array).flip();
		return buf;
	}
	
	public static void perspectiveProjection(Matrix dest, float aspect, float fov, float farClip, float nearClip){
		float zm = farClip - nearClip;
		float zp = farClip + nearClip;
		float tan = Sys.tan(fov / 2f);
		float a = (1f / tan) / aspect;
		float b = 1f / tan;
		float c = - (zp / zm);
		float d = - (2f * farClip * nearClip) / zm;
		dest.v00 = a; dest.v01 = 0f; dest.v02 = 0f; dest.v03 = 0f;
		dest.v10 = 0f; dest.v11 = b; dest.v12 = 0f; dest.v13 = 0f;
		dest.v20 = 0f; dest.v21 = 0f; dest.v22 = c; dest.v23 = d;
		dest.v30 = 0f; dest.v31 = 0f; dest.v32 = -1f; dest.v33 = 0f;
	}
	
	public static void orthographicProjection(Matrix dest, float left, float right, float bottom, float top, float near, float far){
		float a = 2f / (right - left);
		float b = 2f / (top - bottom);
		float c = -2f / (far - near);
		float d = - (right + left) / (right - left);
		float e = - (top + bottom) / (top - bottom);
		float f = - (far + near) / (far - near);
		dest.v00 = a; dest.v01 = 0f; dest.v02 = 0f; dest.v03 = d;
		dest.v10 = 0f; dest.v11 = b; dest.v12 = 0f; dest.v13 = e;
		dest.v20 = 0f; dest.v21 = 0f; dest.v22 = c; dest.v23 = f;
		dest.v30 = 0f; dest.v31 = 0f; dest.v32 = 0f; dest.v33 = 1f;
	}

	public static void translation(Matrix dest, float dx, float dy, float dz){
		dest.v00 = 1f; dest.v01 = 0f; dest.v02 = 0f; dest.v03 = dx;
		dest.v10 = 0f; dest.v11 = 1f; dest.v12 = 0f; dest.v13 = dy;
		dest.v20 = 0f; dest.v21 = 0f; dest.v22 = 1f; dest.v23 = dz;
		dest.v30 = 0f; dest.v31 = 0f; dest.v32 = 0f; dest.v33 = 1f;
	}
	
	public static void rotation(Matrix dest, float p, float y, float r){
		float cp = Sys.cos(p); float sp = Sys.sin(p);
		float cy = Sys.cos(y); float sy = Sys.sin(y);
		float cr = Sys.cos(r); float sr = Sys.sin(r);
		dest.v00 = cy*cr; dest.v01 = -cy*sr; dest.v02 = sy; dest.v03 = 0f;
		dest.v10 = cp*sr + sp*sy*cr; dest.v11 = cp*cr - sp*sy*sr; dest.v12 = -sp*cy; dest.v13 = 0f;
		dest.v20 = sp*sr - cp*sy*cr; dest.v21 = sp*cr + cp*sy*sr; dest.v22 = cp*cy; dest.v23 = 0f;
		dest.v30 = 0f; dest.v31 = 0f; dest.v32 = 0f; dest.v33 = 1f;
		/*Matrix m = new Matrix(
				cy*cr, -cy*sr, sy, 0f,
				cp*sr + sp*sy*cr, cp*cr - sp*sy*sr, -sp*cy, 0f,
				sp*sr - cp*sy*cr, sp*cr + cp*sy*sr, cp*cy, 0f,
				0f, 0f, 0f, 1f);*/
	}

	public static void scale(Matrix dest, float sx, float sy, float sz){
		dest.v00 = sx; dest.v01 = 0f; dest.v02 = 0f; dest.v03 = 0f;
		dest.v10 = 0f; dest.v11 = sy; dest.v12 = 0f; dest.v13 = 0f;
		dest.v20 = 0f; dest.v21 = 0f; dest.v22 = sz; dest.v23 = 0f;
		dest.v30 = 0f; dest.v31 = 0f; dest.v32 = 0f; dest.v33 = 1f;
	}

	public static void transformation(Matrix dest, Matrix a, Matrix b, Matrix c, Vector translation, Vector rotation, Vector scale){
		transformation(
				dest, a, b, c,
				translation.getX(), translation.getY(), translation.getZ(),
				rotation.getX(), rotation.getY(), rotation.getZ(),
				scale.getX(), scale.getY(), scale.getZ()
		);
	}
	
	public static void transformation(Matrix dest, Matrix a, Matrix b, Matrix c, float dx, float dy, float dz, float p, float y, float r, float sx, float sy, float sz){
		translation(a, dx, dy, dz);
		rotation(b, p, y, r);
		scale(c, sx, sy, sz);
		Matrix.multiply(b, c, dest);
		Matrix.multiply(a, dest, dest);
	}

	public static void view(Matrix dest, Matrix a, Matrix b, Vector translation, Vector rotation){
		view(
			dest, a, b, 
			translation.getX(), translation.getY(), translation.getZ(),
			rotation.getX(), rotation.getY(), rotation.getZ()
		);
	}

	public static void view(Matrix dest, Matrix a, Matrix b, float dx, float dy, float dz, float p, float y, float r){
		translation(a, -dx, -dy, -dz);
		rotation(b, p, y, r);
		Matrix.multiply(b, a, dest);
	}
	
	public static void lightView(Matrix dest, Matrix a, Matrix b, Vector dir, float x, float z){
		Vector translation = new Vector(0f,0f,0f);
		translation.setX(x);
		translation.setZ(z);
		float pitch = Sys.asin(dir.getY());
		float yaw = Sys.direction(dir.getX(), dir.getZ());
		Vector rotation = new Vector(pitch, yaw, 0f);
		view(dest, a, b, translation, rotation);
	}

	public static Texture loadTexture(String path){
		File file = new File(path);
		if(!file.exists())
			return null;
		try{
			BufferedImage image = ImageIO.read(file);
			return new Texture(image);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

}
