
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Chat1 {
	int sPort = 9000;    //The server will be listening on this port number
	ServerSocket sSocket;   //serversocket used to lisen on port number 8000
	static ObjectOutputStream out;  //stream write to the socket
	static ObjectInputStream in;    //stream read from the socket
	static String send = "";
	static String receive = "";	

    public Chat1() {}

	void setUp()
	{
		try{
			
			//create a serversocket
			sSocket = new ServerSocket(sPort);

			Socket sock = sSocket.accept();
			new ServerThread(sock).start();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			out = new ObjectOutputStream(sock.getOutputStream());
			out.flush();

			System.out.println("Connected Successfully!");

			while (!send.equals("Bye")) {
				send = bufferedReader.readLine();
				if (!receive.equals("Bye")) {
					sendMessage(send);
				}
				else break;
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

 	// public static void main(String args[]) {
  //       Server s = new Server();
  //       s.setUp();  
  //   }
 	
 	private static class ServerThread extends Thread {
 		private Socket socket;
 		private String currentUser;
 		
 		public ServerThread(Socket socket) {
 			this.socket = socket;
 			System.out.println("Someone connected");
 		}
 		
 		public void run() {
 			try {
 				in = new ObjectInputStream(socket.getInputStream());

 				while (true) {
					if (!send.equals("Bye") && !receive.equals("Bye")) {
						receive = (String)in.readObject();
					}
					else {
						break;
					}
					System.out.println("Receiving: " + receive);
				}
 			}
 			catch (IOException e) {
 				// System.out.println("Error handling client");
 				// e.printStackTrace();
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
 	}
}
