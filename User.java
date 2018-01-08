//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class User extends Thread {

	// The user socket
	private static Socket userSocket = null;
	// The output stream
	private static PrintStream output_stream = null;
	// The input stream
	private static BufferedReader input_stream = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 8000;
		// The default host.
		String host = "localhost";

		if (args.length < 2) {
			System.out
			.println("Usage: java User <host> <portNumber>\n"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			userSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			output_stream = new PrintStream(userSocket.getOutputStream());
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host "
					+ host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on port portNumber.
		 */
		if (userSocket != null && output_stream != null && input_stream != null) {
			try {                
				/* Create a thread to read from the server. */
				new Thread(new User()).start();

				// Get user name and join the social net
				System.out.println("enter username:");
				String userName =inputLine.readLine();
				output_stream.println("#join "+userName);
				
				while (!closed) {
					String userMessage = new String();
					String userInput = inputLine.readLine().trim();
					
					// Read user input and send protocol message to server
					if(userInput.compareTo("Exit")==0){
						output_stream.println("#Bye");
					}
					
					else{
						
						output_stream.println("#status " + userInput);
					}
					

				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				output_stream.close();
				input_stream.close();
				userSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	 * Create a thread to read from the server.
	 */
	
	
	public void run() {
		/*
		 * Keep on reading from the socket till we receive a Bye from the
		 * server. Once we received that then we want to break.
		 */
		String responseLine;
		
		
		try {
			while ((responseLine = input_stream.readLine()) != null) {
				responseLine.trim();
				// Display on console based on what protocol message we get from server.
				if(responseLine.contains("#newStatus")){
					responseLine = responseLine.substring(10, responseLine.length());
					System.out.println(responseLine);
					
				}
				if(responseLine.contains("#newuser")){
					responseLine = responseLine.substring(8,responseLine.length());
					System.out.println(responseLine + " has joined the chat");
				}
				if(responseLine.compareTo("#Bye") == 0){
					System.out.println("you have left the chat!");
					break;
				}
				if(responseLine.contains("#statusPosted")){
					System.out.println("Status was posted to server!");
				}
				if(responseLine.contains("#Leave")){
					System.out.println(responseLine.substring(6,responseLine.length()) + " has lef the chat.");
				}
				if(responseLine.contains("#welcome")){
					System.out.println("Welcome to the chat ");
				}
				
			}
			closed = true;
			output_stream.close();
			input_stream.close();
			userSocket.close();
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}


