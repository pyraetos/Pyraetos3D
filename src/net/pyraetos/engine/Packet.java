package net.pyraetos.engine;

import java.io.PrintWriter;

public abstract class Packet{

	public String header;
	
	public abstract void send(PrintWriter out);
	
}
