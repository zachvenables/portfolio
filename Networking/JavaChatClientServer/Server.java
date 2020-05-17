//Title: Simple IM Chat Server
//
//Author: Zachary Venables (Venables.3@osu.edu)
//
//CSE 3461 tu/thur 0800
//
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Server {
    //list of all the active clients
    static ArrayList<ClientThread> clientList = new ArrayList<>();

    //checks to see if the username exists
    public static boolean nameRegister(String name) throws IOException {
        File file = new File("UserInfo.txt");
        BufferedReader in = new BufferedReader(new FileReader(file));

        boolean result = false;

        String line;
        StringTokenizer checkLine;
        String currentLineName;

        while ((line = in.readLine()) != null) {

            checkLine = new StringTokenizer(line, " ");
            currentLineName = checkLine.nextToken();
            if (currentLineName.equals(name)) {
                result = true;
                break;
            }

        }
        in.close();

        return result;
    }

    //checks to see if the user is registered
    public static boolean correctLogin(String name, String password)
            throws IOException {

        File file = new File("UserInfo.txt");
        BufferedReader in = new BufferedReader(new FileReader(file));

        boolean result = false;
        String line;
        StringTokenizer checkLine;
        String currentLineName;
        String currentLinePassword;

        while ((line = in.readLine()) != null) {
            checkLine = new StringTokenizer(line, " ");

            currentLineName = checkLine.nextToken();
            currentLinePassword = checkLine.nextToken();

            if (currentLineName.equals(name)) {
                if (currentLinePassword.equals(password)) {
                    result = true;
                    break;
                }
            }
        }
        in.close();
        return result;
    }

    //adds a user to userinfo.txt
    public static void addUser(String name, String password)
            throws IOException {
        BufferedWriter out = new BufferedWriter(
                new FileWriter("UserInfo.txt", true));
        out.append(name + " " + password + "\n");
        out.close();
    }

    //comment
    public static class ClientThread implements Runnable {

        DataInputStream dataIn;
        DataOutputStream dataOut;
        Socket clientSocket;
        boolean isLoggedIn;
        boolean correctLogin;
        String name;
        String password;

        // constructor
        public ClientThread(Socket socket) throws IOException {
            this.dataIn = new DataInputStream(socket.getInputStream());
            this.dataOut = new DataOutputStream(socket.getOutputStream());
            this.clientSocket = socket;
            this.isLoggedIn = true;
            this.correctLogin = false;
            this.name = "";
            this.password = "";
        }

        @Override
        public void run() {

            String received;
            while (this.isLoggedIn) {
                try {

                    this.dataOut.writeUTF("Enter Command: ");
                    // receive the string
                    received = this.dataIn.readUTF();
                    received.toLowerCase();

                    if (received.equals("logout")) {
                        this.isLoggedIn = false;
                        this.dataOut.writeUTF("closesocket");
                        this.clientSocket.close();
                        break;
                    } else if (received.equals("login")) {
			if(this.isLoggedIn && this.name.length() > 0){
				this.dataOut.writeUTF("User already logged in.");
			}else{
                        	this.dataOut.writeUTF("Enter Chat Name: ");
                        	String tempName = this.dataIn.readUTF();
				boolean notLogged = true;
				//checks client list for entered name login status
				for (ClientThread recievingClient : Server.clientList){
					if(recievingClient.name.equals(tempName) && recievingClient.isLoggedIn){
						notLogged = false;
					}
				} 
				//if they are not on clientlist then log in
				if(notLogged){
					this.name = tempName;
	                        	this.dataOut.writeUTF("Enter password: ");
        	                	this.password = this.dataIn.readUTF();

                	        	while (!correctLogin(this.name, this.password)) {
						this.dataOut.writeUTF("Wrong username and password.");

						this.dataOut.writeUTF("Enter Chat Name: ");
                            			this.name = this.dataIn.readUTF();

                            			this.dataOut.writeUTF("Enter password: ");
                            			this.password = this.dataIn.readUTF();
                        		}
                        		this.correctLogin = true;
					this.dataOut.writeUTF("Now logged in.\n");
				}else{
					this.dataOut.writeUTF("Error. User already logged in.\n");
				}
			}
                    } else if (received.equals("register user")) {
			if(this.isLoggedIn && this.name.length() > 0){
				this.dataOut.writeUTF("User already logged in.");
			}else{
                       		this.dataOut.writeUTF("Enter new chat name: ");
                       		this.name = this.dataIn.readUTF();

                       		while (nameRegister(this.name)) {
                           		 this.dataOut.writeUTF("name alread exists: ");

                          		 this.dataOut.writeUTF("Enter new chat name: ");
                           		 this.name = this.dataIn.readUTF();
                       		}
                       		this.dataOut.writeUTF("Enter new Password: ");
                       		this.password = this.dataIn.readUTF();

                       		addUser(this.name, this.password);
				this.isLoggedIn = true;
                        	this.dataOut.writeUTF("User registered and now logged in.");
				this.correctLogin = true;
			}
                    } else if (received.equals("listusers")) {
                        String list = "";
                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).isLoggedIn) {
                                list += "<" + clientList.get(i).name + ">";
                            }
                        }

                        this.dataOut.writeUTF(list);
                    } else if (received.equals("sendmsg")
                            && this.correctLogin) {
                        this.dataOut.writeUTF("Enter recipient: ");
                        String recip = this.dataIn.readUTF();
			boolean exist = false;

                        this.dataOut.writeUTF("Enter message: ");
                        String msgToSend = this.dataIn.readUTF();

                        // search for the recipient in the connected devices list.
                        for (ClientThread recievingClient : Server.clientList) {
                            // if the recipient is found, write on its
                            // output stream
                            if (recievingClient.name.equals(recip)
                                    && recievingClient.isLoggedIn == true) {
                                recievingClient.dataOut
                                        .writeUTF(this.name + ": " + msgToSend);
				exist = true;
                                break;
                            }
                        }
			if (!exist){
				this.dataOut.writeUTF("Error.  Recipient is not logged in.");
			}
                    } else if(received.equals("help")){
			this.dataOut.writeUTF("Commands are: \nlogout\nlogin\nregister user\nlistusers\nsendmsg\nType the commands exactly as they are listed.\n");
		}else {
                        this.dataOut.writeUTF("Error.  Not a valid command or you need to log in.\n Enter Command(type help for commands): ");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // closing resources
                this.dataIn.close();
                this.dataOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {

        @SuppressWarnings("resource")
        ServerSocket serverSocket = new ServerSocket(49155);
        Socket clientSocket;

        while (true) {
            // Accept the incoming request
            System.out.println("waiting on client...");
            clientSocket = serverSocket.accept();

            ClientThread currentClient = new ClientThread(clientSocket);

            //Create a new thread for each client that connects to socket.
            Thread currentThread = new Thread(currentClient);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            clientList.add(currentClient);

            currentThread.start();

        }

    }
}
