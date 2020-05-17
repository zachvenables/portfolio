
// Title: Simple IM Chat Client
// Author: Zachary Venables (Venables.3@osu.edu)
// CSE 3461 tu/thur 0800
// Java implementation for multithreaded chat client
//

// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String args[])
            throws UnknownHostException, IOException {
        // getting localhost ip (code left in comment in case I need localhost)
        //InetAddress ServerIP = InetAddress.getByName();
        Scanner in = new Scanner(System.in);
	System.out.println("Enter server IP address: ");//get server IP
	String ipAddress = in.nextLine();
	System.out.println("Enter port number(default 49155): ");//get server port number
	int ServerPort = Integer.parseInt(in.nextLine());

	System.out.println("Connecting to server");

        //open the socket to the server
        Socket socket = new Socket(ipAddress, ServerPort);

	System.out.println("Server Connected");

        // obtaining input and output streams
        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(
                socket.getOutputStream());

        //client has both a thread for sending and receiving so they can be done simultaneously

        //this thread sends messages to the server
        Thread output = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = in.nextLine();

                    try {
                        // write on the output stream
                        dataOut.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }//if user decides to logout, socket closes, program exits
                    if (msg.equals("logout")) {
                        try {
                            socket.close();
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
            }
        });

        //thread for data coming into the client FROM the server
        Thread input = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        String incoming = dataIn.readUTF();
			//if the message closesocket is received from the sender, the socket closes
                        if (incoming.equals("closesocket")) {
                            break;
                        }
                        System.out.println(incoming);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
	//each thread is started
        output.start();
        input.start();
    }
}
