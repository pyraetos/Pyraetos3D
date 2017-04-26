package net.pyraetos.engine;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.LinkedList;
import java.util.Map;

public class Client {
    LinkedList<DatagramPacket> pool;
    Map<Integer, Player> playerLocations;

    // This creates sound packets and sends them
    public static void main2(String args[])
    {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        TargetDataLine microphone;
        try {
            DatagramSocket client_socket = new DatagramSocket();
            InetAddress IPAddress=InetAddress.getByName("127.0.0.1");
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);

            while(bytesRead<100000){
                //while (true) { //to loop infinetly
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                DatagramPacket send_packet=new DatagramPacket(data,data.length,IPAddress,Integer.valueOf(4200));
                client_socket.send(send_packet);
                bytesRead += numBytesRead;

            }
            microphone.close();
            client_socket.close();

        microphone.close();
        client_socket.close();
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }
}