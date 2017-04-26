package net.pyraetos.sound;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;


public class Client {
    public static void main(String[] args) throws Exception 
    {
        SourceDataLine sourceDataLine;
        AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, true);
        byte tempBuffer[] = new byte[1024];
        
        // play soundfile from server
        System.out.println("Client: reading from 127.0.0.1:6666");
        try{
            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            DatagramSocket server_socket=new DatagramSocket(Integer.valueOf(6666));

            while(true) 
                {
                    if(sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
                    {
                        FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                        volume.setValue(-80.0F);//use this to adjust the output
                    }
                    DatagramPacket receive_packet = new DatagramPacket(tempBuffer,tempBuffer.length);
                    server_socket.receive(receive_packet);
                    sourceDataLine.write(receive_packet.getData(), 0,tempBuffer.length);
                }
            }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}