import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Iterator;

public class Server {

    @SuppressWarnings("resource")
    public static void main(String args[]) throws IOException {
        HashMap<String, String> clients = new HashMap<>();
	

        byte[] buf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(49155);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        System.out.println("Socket created");

	StringTokenizer msg;
	String action;
	String fileName = "";
	String testName = "test.txt";

        while (true) {
	    buf = new byte[1024];
	    packet = new DatagramPacket(buf, buf.length);
	    System.out.println("Waiting...");
            socket.receive(packet);
	    
            System.out.println("request received");
	   
            //get message
            msg = new StringTokenizer(new String(buf), ":");
	
            action = msg.nextToken();
	   
	   
            fileName = msg.nextToken();
	    
	    
            //get senders info
            InetAddress clientAddress = packet.getAddress();
            String clientAddressStr = clientAddress.getHostAddress();
            String clientPort = Integer.toString(packet.getPort());
	    
            if (action.equals("post")) {
		
		
                clients.put(fileName.trim(), clientAddressStr + ":" + clientPort);

                System.out.println("File holder added to the hash map");
            } else if (action.equals("download")) {
		System.out.println("client downloading...");
                //send ip:port to client with file, to forward the file
		
		String fileHolder = clients.get(fileName.trim());
		
                StringTokenizer clientToken = new StringTokenizer(fileHolder,
                        ":");

                InetAddress fileHolderAddress = InetAddress
                        .getByName(clientToken.nextToken());
                int fileHolderPort = Integer.parseInt(clientToken.nextToken());

                //create a buf with requester info
                buf = (clientAddressStr + ":" + clientPort).getBytes();
		System.out.println("got here");

                packet = new DatagramPacket(buf, buf.length);
		//DatagramSocket sendSocket = new DatagramSocket(49156);
			
                socket.connect(fileHolderAddress, fileHolderPort);
                socket.send(packet);
		System.out.println("client addr: " + clientAddressStr);
		System.out.println("client port: " + clientPort);
		socket.disconnect();
		System.out.println("Clients are connected.");
            }

        }

    }

}
