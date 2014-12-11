import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Chat2 {
	Socket requestSocket;           //socket connect to the server
	static ObjectOutputStream out;         //stream write to the socket
 	static ObjectInputStream in;          //stream read from the socket
	Socket connection = null;
	String IPAddress = "localhost";
	int portNumber = 9000;
	static String send = "";
	static String receive = "";

	public Chat2(String IPAddress, int portNumber) {
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}

		void connect()
	{
		try{
			requestSocket = new Socket(IPAddress, portNumber);

			new ClientThread(requestSocket).start();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();

			System.out.println("Connected Successfully!");

			while (!send.equals("Bye")) {
				send = bufferedReader.readLine();
				if (!receive.equals("Bye")) {
					sendMessage(send);
				}
			}
		}
		catch(IOException ioException){
			// ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

 	// public static void main(String args[]) {
  //       Client c = new Client();
  //       c.connect();  
  //   }


    private static class ClientThread extends Thread {
    	Socket socket;

    	public ClientThread(Socket socket) {
    		this.socket = socket;
    	}
		public void run()
		{
			try {
				//initialize inputStream and outputStream
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
			catch (ConnectException e) {
	    			System.err.println("Connection refused. You need to initiate a server first.");
			} 
			catch(UnknownHostException unknownHost){
				System.err.println("You are trying to connect to an unknown host!");
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
			catch (ClassNotFoundException e) {
	    			System.err.println("Class not found");
			} 
			finally{
				//Close connections
				try{
					in.close();
					socket.close();
				}
				catch(IOException ioException){
					ioException.printStackTrace();
				}
			}
		}	
	}
}
