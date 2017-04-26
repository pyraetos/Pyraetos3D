package net.pyraetos.engine;

import net.pyraetos.util.Sys;
import net.pyraetos.util.Vector;

public class CollisionPlane{

	public char normal;
	public Vector p1;
	public Vector p2;
	
	public CollisionPlane(char normal, Vector p1, Vector p2){
		this.normal = normal;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public boolean collides(Vector curr, Vector fut){
		if(normal == 'x')
			return collidesX(curr, fut);
		if(normal == 'y')
			return collidesY(curr, fut);
		if(normal == 'z')
			return collidesZ(curr, fut);
		return false;
	}
	
	private boolean collidesX(Vector curr, Vector fut){
		float xc = curr.getX(); float xf = fut.getX();
		float x = p1.getX();
		if((xc < x && xf < x) || (xc > x && xf > x))
			return false;
		
		float yc = curr.getY(); float yf = fut.getY();
		float y1 = Math.min(p1.getY(), p2.getY());
		float y2 = Math.max(p1.getY(), p2.getY());
		if(yc < y1 || yf < y1 || yc > y2 || yf > y2)
			return false;
		
		float zc = curr.getZ(); float zf = fut.getZ();
		float z1 = Math.min(p1.getZ(), p2.getZ());
		float z2 = Math.max(p1.getZ(), p2.getZ());
		if(zc < z1 || zf < z1 || zc > z2 || zf > z2)
			return false;
		return true;
	}
	
	private boolean collidesY(Vector curr, Vector fut){
		float yc = curr.getY(); float yf = fut.getY();
		float y = p1.getY();
		if((yc < y && yf < y) || (yc > y && yf > y))
			return false;
		
		float xc = curr.getX(); float xf = fut.getX();
		float x1 = Math.min(p1.getX(), p2.getX());
		float x2 = Math.max(p1.getX(), p2.getX());
		if(xc < x1 || xf < x1 || xc > x2 || xf > x2)
			return false;
		
		float zc = curr.getZ(); float zf = fut.getZ();
		float z1 = Math.min(p1.getZ(), p2.getZ());
		float z2 = Math.max(p1.getZ(), p2.getZ());
		if(zc < z1 || zf < z1 || zc > z2 || zf > z2)
			return false;
		return true;
	}
	
	private boolean collidesZ(Vector curr, Vector fut){
		float zc = curr.getZ(); float zf = fut.getZ();
		float z = p1.getZ();
		if((zc < z && zf < z) || (zc > z && zf > z))
			return false;
		
		float yc = curr.getY(); float yf = fut.getY();
		float y1 = Math.min(p1.getY(), p2.getY());
		float y2 = Math.max(p1.getY(), p2.getY());
		if(yc < y1 || yf < y1 || yc > y2 || yf > y2)
			return false;
		
		float xc = curr.getX(); float xf = fut.getX();
		float x1 = Math.min(p1.getX(), p2.getX());
		float x2 = Math.max(p1.getX(), p2.getX());
		if(xc < x1 || xf < x1 || xc > x2 || xf > x2)
			return false;
		return true;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + normal;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		CollisionPlane other = (CollisionPlane)obj;
		if(normal != other.normal)
			return false;
		if(p1 == null){
			if(other.p1 != null)
				return false;
		}else if(!p1.equals(other.p1))
			return false;
		if(p2 == null){
			if(other.p2 != null)
				return false;
		}else if(!p2.equals(other.p2))
			return false;
		return true;
	}
	
}
