package net.pyraetos.engine;

import java.io.PrintWriter;

import net.pyraetos.util.Sys;

public class ActionPacket extends Packet{

	public String data;
	
	public ActionPacket(String data){
		header = "action";
		this.data = data;
	}
	
	@Override
	public void send(PrintWriter out){
		out.println(header);
		out.println(data);
		out.flush();
	}

	
}
