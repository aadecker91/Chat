
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
	int sPort = 8000;    //The server will be listening on this port number
	ServerSocket sSocket;   //serversocket used to lisen on port number 8000
	Socket connection = null; //socket for the connection with the client
	static String message;    //message received from the client
	String MESSAGE;    //uppercase message send to the client
	static ObjectOutputStream out;  //stream write to the socket
	static ObjectInputStream in;    //stream read from the socket
	int clientNumber;		//number of clients connected
	static String[] users = new String[2];
	static String[] passwords = new String[2];
	static String[] AliceInbox = new String[10];
	static String[] BobInbox = new String[10];
	static int AliceMessages = 0;
	static int BobMessages = 0;
	

    public void Server() {}

	void getClients()
	{
		try{
			System.out.println("The server is running...");
			clientNumber = 0;
			users[0] = "Alice";
			users[1] = "Bob";
			passwords[0] = "12345";
			passwords[1] = "67890";
			//create a serversocket
			sSocket = new ServerSocket(sPort);


			while(true)
			{
				new ServerThread(sSocket.accept(), ++clientNumber).start();
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				sSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

 	public static void main(String args[]) {
        Server s = new Server();
        s.getClients();  
    }
 	
 	private static class ServerThread extends Thread {
 		private Socket socket;
 		private int clientNumber;
 		private String currentUser;
 		private boolean closed = false;
 		
 		
 		public ServerThread(Socket socket, int clientNumber) {
 			this.socket = socket;
 			this.clientNumber = clientNumber;
 			System.out.println("Client number " + clientNumber + " is connected");
 		}
 		
 		public void run() {
 			try {
 				out = new ObjectOutputStream(socket.getOutputStream());
 				out.flush();
 				in = new ObjectInputStream(socket.getInputStream());
 				boolean valid = false;
 				message = (String)in.readObject();
 				if (message.equals("0")) {
 					System.out.println("Client " + clientNumber + " choice is 0");
	 				while (!valid) {
		 				sendMessage("Please enter your username: ");
		 				message = (String)in.readObject();
		 				for (int i = 0; i < users.length; i++) {
		 					if (message.equals(users[i])) {
		 						sendMessage("Please enter your password");
		 						message = (String)in.readObject();
		 						if (message.equals(passwords[i])) {
		 							valid = true;
		 							currentUser = users[i];
		 							sendMessage("1");
		 						}
		 						else {
		 							sendMessage("Incorrect password");
		 							break;
		 						}
		 					}
		 				}
	 				}
	 				sendMessage("Successfully logged in");
	 				System.out.println(currentUser + " has logged in");
 				}
 				while (!closed) {
	 				message = (String)in.readObject();
	 				if (message.equals("1")) {
	 					System.out.println("Client " + clientNumber + " choice is 1");
	 					String numberOfUsers = Integer.toString(users.length);
	 					sendMessage(numberOfUsers);
	 					for (int i = 0; i < users.length; i++) {
	 						sendMessage(users[i]);
	 					}
	 				}
	 				else if (message.equals("2")) {
	 					System.out.println("Client " + clientNumber + " choice is 2");
	 					String userSend = (String)in.readObject();
	 					if (userSend.equals("Alice")) {
							AliceInbox[AliceMessages++] = (String)in.readObject();
	 					} else if (userSend.equals("Bob")){
							BobInbox[BobMessages++] = (String)in.readObject();
	 					}
	 				}
	 				else if (message.equals("3")) {
	 					System.out.println("Client " + clientNumber + " choice is 3");
	 					if (currentUser.equals("Alice")) {
	 						for (int i = 0; i < AliceMessages; i++) {
	 							sendMessage(AliceInbox[i]);
	 						}
							sendMessage("-1");
	 					} else if (currentUser.equals("Bob")){
	 						for (int i = 0; i < BobMessages; i++) {
	 							sendMessage(BobInbox[i]);
	 						}
							sendMessage("-1");
	 					}
	 					else {
	 						sendMessage("No mail");
	 						sendMessage("-1");
	 					}
	 				}
					else if (message.equals("4")) {
						System.out.println("Client " + clientNumber + " choice is 4");
						sendMessage(currentUser);
						closed = true;
	 					System.out.println(currentUser + " has disconnected");
					}
					else if (message.equals("5")) {
						System.out.println("Client " + clientNumber + " choice is 5");
						sendMessage(currentUser);
						closed = true;
	 					System.out.println(currentUser + " has disconnected");
		 			}
					else {
						sendMessage("Not a valid input");
					}
 				}
 			}
 			catch (IOException e) {
 				System.out.println("Error handling client");
 				e.printStackTrace();
 			} 
 			catch (ClassNotFoundException classNot) {
 				System.out.println("Class not found exception");
 			} finally {
 				try {
 					out.close();
					in.close();
 					socket.close();
 				} catch (IOException e){
 					System.out.println("Error closing socket, doesn't exist");
 				}
 			}
 		}
 		void sendMessage(String msg)
 		{
 			try{
 				out.writeObject(msg);
 				out.flush();
 			}
 			catch(IOException ioException){
 				ioException.printStackTrace();
 			}
 		}
 	}
}
