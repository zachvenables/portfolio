import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.lang.*;
import java.net.SocketTimeoutException;

public class Client {

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {

        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);
	//TODO GET INPUT FOR SERVER IP AND PORT THEN APPLY TO SOCKET
	System.out.println("Enter the Server IP: ");
	String ServerIPStr = in.nextLine();	
	
        //set up socket
        DatagramSocket socket = new DatagramSocket(null);
        InetAddress serverAddress = InetAddress.getByName(ServerIPStr);
        int serverPort = 49155;

        System.out.println("post or download a file: ");
        String action = in.nextLine().toLowerCase();

        if (action.equals("post")) {

            System.out.println("Enter file name: ");
            String fileName = in.nextLine().toLowerCase();
            String outMsg = "post:" + fileName;

            byte[] buf = outMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.connect(serverAddress, serverPort);
            socket.send(packet);
	    socket.disconnect();
	
	    while(true){

         	//wait for the server to respond with another client
		System.out.println(
                    "Waiting for another client to request the file...");
            	buf = new byte[1024];
            	packet = new DatagramPacket(buf, buf.length);
            	socket.receive(packet);

	    	TimeUnit.SECONDS.sleep(2);

            	//get other client info
            	StringTokenizer token = new StringTokenizer(new String(buf), ":");

	    	String addrToken = token.nextToken();
            	InetAddress downloadClientAddress = InetAddress
                    	.getByName(addrToken.trim());

	    	String portToken = token.nextToken();
	    	//ports can't be the same, so add 1 when you get it, and the client will be using +1 for download
            	int downloadClientPort = Integer.parseInt(portToken.trim()) + 1;
		System.out.println("download address: " + downloadClientAddress);
		System.out.println("download Port: " + downloadClientPort);
            	//send file
            	File file = new File(fileName);
            	@SuppressWarnings("resource")
            	FileInputStream targetStream = new FileInputStream(file);
            	buf = new byte[1024];
            	long fileSize = file.length();
	    	System.out.println("have file");
            	//send size dgram

            	//send all full dgrams
            	int fullPkts = (int) Math.floor((int)fileSize / 1024);
	
	    
            	for (int i = 0; i < fullPkts; i++) {
                	for (int j = 0; j < 1024; j++) {
                    		buf[j] = (byte) targetStream.read();
                	}
                	DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);
			socket = new DatagramSocket();
                	socket.connect(downloadClientAddress, downloadClientPort);
                	socket.send(sendPacket);
			System.out.println("Sent packet: "+i);
                	Thread.sleep(100);
            	}
            	//send remaining dgram

            	long remainingFileSize = fileSize % 1024;
            	buf = new byte[(int) remainingFileSize];
            	for (int i = 0; i < buf.length; i++) {
               		buf[i] = (byte) targetStream.read();
            	}
            	DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);
            	socket.connect(downloadClientAddress, downloadClientPort);
            	socket.send(sendPacket);
		socket.disconnect();
	}
           // socket.close();

        } else if (action.equals("download")) {
	    System.out.println("Enter your IP Address: ");
	    String myIP = in.nextLine();
            System.out.println("Enter file name: ");
            String fileName = in.nextLine().toLowerCase();
            String msgFileName = "download:" + fileName;

            byte[] buf = msgFileName.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    DatagramSocket outSocket = new DatagramSocket(null);
	    InetSocketAddress outAddr = new InetSocketAddress(myIP, 49159);

	    outSocket.bind(outAddr);
	    System.out.println("Socket bound");
	    outSocket.connect(serverAddress, serverPort);
            outSocket.send(packet);
	    System.out.println("info sent to server");
	
	    DatagramSocket dSocket = new DatagramSocket(null);
	    InetSocketAddress dAddr = new InetSocketAddress("164.107.113.66", 49160); 
	    dSocket.bind(dAddr);
	    


            //download the whole file
            File yourFile = new File(fileName);
            yourFile.createNewFile();
            FileOutputStream FOS = new FileOutputStream(yourFile, false);

            buf = new byte[1024];
            DatagramPacket srvpkt = new DatagramPacket(buf, buf.length);
            int packCount = 0;
	    dSocket.setSoTimeout(10000);
	    System.out.println("waiting for download...");
            while (true) {
		try{
                	dSocket.receive(srvpkt);

               		 FOS.write(buf);
               		 System.out.println("Packet # " + packCount + "received.");
               		 packCount++;
		}catch(SocketTimeoutException e){
			dSocket.close();
		 	System.exit(0);
		}
            }

        }
    }

}
