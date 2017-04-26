package net.pyraetos.engine;

import java.io.PrintWriter;

import net.pyraetos.util.Sys;

public class LoginPacket extends Packet{

	public LoginPacket(){
		header = "login";
	}
	
	@Override
	public void send(PrintWriter out){
		out.println(header);
		out.flush();
	}

	
}
