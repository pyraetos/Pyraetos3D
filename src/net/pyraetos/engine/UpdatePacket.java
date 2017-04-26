package net.pyraetos.engine;

import java.io.PrintWriter;

import java.io.OutputStream;

import net.pyraetos.util.Sys;

public class UpdatePacket extends Packet{

	public UpdatePacket(){
		header = "update";
	}

	@Override
	public void send(PrintWriter out){
		try{
			out.println(header);
			out.println(Pyraetos3D.camera.getX());
			out.println(Pyraetos3D.camera.getY());
			out.println(Pyraetos3D.camera.getZ());
			out.flush();
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	
}
