package net.pyraetos.engine;

import java.net.*;
import java.io.*;
import java.util.*;

import net.pyraetos.util.Sys;

public class MyClientSocket implements Runnable{


	public static LinkedList<Packet> queue = new LinkedList<Packet>();
	
    public int port = 55555;
    public String hostname = "s7.irl.cs.tamu.edu";

    public Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private InputStream is;
    
    public MyClientSocket() {
    	Sys.thread(this);
    	try{
			Thread.sleep(500);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
    }

    @Override
    public void run(){ 
    	try {
    		client = new Socket(hostname, port);
    		out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
    		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    		while(true){
    			if(!queue.isEmpty()){
    				queue.poll().send(out);
    			}
    			if(in.ready()){
    				String h = in.readLine();
    				handle(h);
    			}
    		}
    	}
    	catch (Throwable o) {
    		System.out.println("Failed to open client side connections. :(");

    	}
    }
    
    public void send(Packet packet){
    	queue.offer(packet);
    }
    
    public void handle(String h){
    	if(h.equals("loginresponse")){
    		System.out.println("Login Response");
    		handleLoginResponse();
    	}
    	if(h.equals("otherplayerlogin")){
    		System.out.println("Other Player Login");
    		handleOtherPlayerLogin();
    	}
    	if(h.equals("disconnect")){
    		System.out.println("Disconnect");
    		handleDisconnect();
    	}
    	if(h.equals("actionapproved")){
    		handleActionApproved();
    	}
    	if(h.equals("otherplayerupdate")){
    		handleOtherPlayerUpdate();
    	}
    }
    
    public void handleLoginResponse(){
    	try{
    		int myID = Integer.parseInt(in.readLine());
    		Pyraetos3D.sound = new Sound(myID == 1 ? 55557 : 55558);
			Sys.thread(Pyraetos3D.sound);
    		float x = Float.parseFloat(in.readLine());
    		float y = Float.parseFloat(in.readLine());
    		float z = Float.parseFloat(in.readLine());
    		boolean isThereAnotherPlayer = Boolean.parseBoolean(in.readLine());
    		if(isThereAnotherPlayer){
    			int oid = Integer.parseInt(in.readLine());
    			float ox = Float.parseFloat(in.readLine());
        		float oy = Float.parseFloat(in.readLine());
        		float oz = Float.parseFloat(in.readLine());
    			Pyraetos3D.other = new Player(oid, ox, oy, oz);
    		}
    		Pyraetos3D.myID = myID;
    		Pyraetos3D.camera.setTranslation(x, y, z);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void handleOtherPlayerUpdate(){
    	if(Pyraetos3D.other == null) return;
    	try{
    		float x = Float.parseFloat(in.readLine());
    		float y = Float.parseFloat(in.readLine());
    		float z = Float.parseFloat(in.readLine());
    		Pyraetos3D.other.x = x;
    		Pyraetos3D.other.y = y;
    		Pyraetos3D.other.z = z;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void handleOtherPlayerLogin(){
    	try{
    		int id = Integer.parseInt(in.readLine());
    		float x = Float.parseFloat(in.readLine());
    		float y = Float.parseFloat(in.readLine());
    		float z = Float.parseFloat(in.readLine());
    		Pyraetos3D.other = new Player(id, x, y, z);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void handleActionApproved(){
    	try{
    		String data = in.readLine();
    		if(data.equals("p1d1")){
    			for(Model m : Pyraetos3D.p1d1.getModels()) Pyraetos3D.removeModel(m);
    			for(CollisionPlane c : Pyraetos3D.p1d1.getColls()) Pyraetos3D.colls.remove(c);
    		}
    		if(data.equals("p1youwin")){
    			Pyraetos3D.win();
    		}
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
    }
    
    public void handleDisconnect(){
    	System.exit(0);
    }
}