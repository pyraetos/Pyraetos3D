package net.pyraetos.engine;

import net.pyraetos.util.Vector;

public interface Light{

	float getIntensity();
	
	void setIntensity(float intensity);
	
	Vector getColor();
	
	void setColor(Vector color);
	
	void setColor(float r, float g, float b);
	
}
