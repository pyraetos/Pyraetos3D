package net.pyraetos.engine;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.pyraetos.util.Sys;

public class Sound implements Runnable{

	public static DatagramSocket local;
	public static int localPort;
	public static InetAddress remoteIP;
	
	public Sound(int myPort){
		try{
			local = new DatagramSocket(myPort);
			localPort = myPort;
			remoteIP = InetAddress.getByName("s7.irl.cs.tamu.edu");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public float getAttenuation(){
		float d = Sys.distanceFrom(Pyraetos3D.camera.getTranslation(), Pyraetos3D.other.getPosition());
		float att = (float)Math.pow(2, -0.075f * d) * 81f - 80f;
		return att;
	}
	
	public void startQToSpeaker(){
		Sys.thread(()->{
			SourceDataLine sourceDataLine;
			AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, true);
			try{
				DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);

				sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo1);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();
				while(true) 
				{
					byte[] recvBuf = new byte[10000];
					DatagramPacket packet = new DatagramPacket(recvBuf, 10000);
					local.receive(packet);
					int len = packet.getLength();
					if(sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
					{
						FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
						volume.setValue(getAttenuation());//use this to adjust the output
					}
					sourceDataLine.write(recvBuf,  0, len);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		});
	}

	public void run()
	{
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		TargetDataLine microphone;
		startQToSpeaker();
		
		try {
			/*byte[] test = {1,2,3,4,5};
			DatagramPacket paket = new DatagramPacket(test, test.length, remoteIP, 55556);
			local.send(paket);
			Sys.debug("Sending sound of length " + test.length);
			*/
			microphone = AudioSystem.getTargetDataLine(format);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			int CHUNK_SIZE = 1024;
			byte[] data = new byte[10000];
			microphone.start();
			try{
				while(true){
					//while (true) { //to loop infinetly
					int amtRead = microphone.read(data, 0, CHUNK_SIZE);
					DatagramPacket packet = new DatagramPacket(data, amtRead, remoteIP, 55556);
					local.send(packet);
				}
			}finally{
				microphone.close();
			}
		} 
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}
}
