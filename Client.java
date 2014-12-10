import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
 	String input = "-1";				//user selection
	String message = "-1";                //message send to the server
	String MESSAGE = "-1";                //capitalized message read from the server
	String IPAddress = "localhost";
	int portNumber = 8000;
	ServerSocket sSocket;
	Socket connection = null;

	public void Client() {}

	void run()
	{
		try{
			Scanner sc = new Scanner(System.in);
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			requestSocket = new Socket(IPAddress, portNumber);
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			while (true) {

				System.out.println("0. Connect to the server");
				System.out.println("1. Get the user list");
				System.out.println("2. Send a message");
				System.out.println("3. Get my messages");
				System.out.println("4. Initiate chat with my friend");
				System.out.println("5. Chat with my friend");
				//create a socket to connect to the server
				System.out.print("Please enter your selection (number): ");
				input = sc.next();
			
				if (input.equals("0")) {
					sendMessage("0");
					String temp = "";
					try {
						do {
							System.out.println((String)in.readObject());
							sendMessage(bufferedReader.readLine());
							System.out.println((String)in.readObject());
							sendMessage(bufferedReader.readLine());
							temp = (String)in.readObject();
							if (!temp.equals("1")) {
								System.out.println(temp);
							}
						} while (!temp.equals("1"));
						System.out.println((String)in.readObject());
					}
					catch (ClassNotFoundException classnot) {
							System.out.println("Class not found");
					}
				}
				else if (input.equals("1")) {
					try {
						sendMessage("1");
						int numberOfUsers = Integer.parseInt((String)in.readObject());
						System.out.println("There are currently " + numberOfUsers + " users:");
						for (int i = 0;i < numberOfUsers;i++) {
							System.out.println((String)in.readObject());
						}
					}
					catch (ClassNotFoundException classnot) {
						System.out.println("Class not found");
					}
				}
				else if (input.equals("2")) {
					sendMessage("2");
					System.out.print("Enter the username: ");
					sendMessage(bufferedReader.readLine());
					System.out.print("Enter the message: ");
					sendMessage(bufferedReader.readLine());
				}
				else if (input.equals("3")) {
					try {
						sendMessage("3");
						System.out.println("Your mailbox: ");
						int countMessages = 1;
						String inbox = "";
						while (!inbox.equals("-1")) {
							inbox = (String)in.readObject();
							if (!inbox.equals("-1")) {
								System.out.print((countMessages++) + "\t");
								System.out.println(inbox);
							}
						}
					}
					catch (ClassNotFoundException classnot) {
						System.out.println("Class not found");
					}
				}
				else if (input.equals("4")) {
					try {
						sendMessage("4");
						String name = (String)in.readObject();
						System.out.print("Please enter a port number: ");
						int port = sc.nextInt();
						chat4(port, name);
					}
					catch (ClassNotFoundException classnot) {
						System.out.println("Class not found");
					}
				}
				else if (input.equals("5")) {
					try {
						sendMessage("5");
						String name = (String)in.readObject();
						System.out.print("Enter in an IP address: ");
						String IPAddress = bufferedReader.readLine();
						System.out.print("Enter in a port number: ");
						int port = sc.nextInt();
						chat5(IPAddress, port, name);
					}
					catch (ClassNotFoundException classnot) {
						System.out.println("Class not found");
					}
				}
				else {
					System.out.println("Invalid input");
				}
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
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
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
	
	void chat4(int Port, String name)
	{
		try{
			//create a serversocket
			sSocket = new ServerSocket(Port);
			//Wait for connection
			System.out.println("Waiting for connection");
			//accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Someone Connected!");
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			try{
				while(!MESSAGE.equals(name + ": Bye"))
				{
					//receive the message sent from the client
					message = (String)in.readObject();
					//show the message to the user
					System.out.println(message);
					System.out.print(name + ": ");
					MESSAGE = name + ": " + bufferedReader.readLine();
					//send MESSAGE back to the client
					out.writeObject(MESSAGE);
					out.flush();
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				sSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	
	void chat5(String IPAddress, int Port, String name)
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket(IPAddress, Port);
			System.out.println("Connected!");
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(!message.equals(name + ": Bye"))
			{
				System.out.print(name + ": ");
				//read a sentence from the standard input
				message = name + ": " + bufferedReader.readLine();
				//Send the sentence to the server
				out.writeObject(message);
				out.flush();
				//Receive the upperCase sentence from the server
				MESSAGE = (String)in.readObject();
				//show the message to the user
				System.out.println(MESSAGE);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//main method
	public static void main(String args[])
	{
		Client client = new Client();
		client.run();
	}

}
