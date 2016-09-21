/* 
* Main Server code for Senior Design Project
* by Ben Tomasulo and Matt Rowe
* Accepts TCP data from an android device over internet 
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
 
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;
	private static String message;
 
	public static void main(String[] args) {
		try {
			serverSocket = new ServerSocket(4444); // Server socket
 
		} catch (IOException e) {
			System.out.println("ERROR: Cannot listen on port 4444");
		}
 
		System.out.println("Server started. Listening on port 4444");

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try{
					System.out.println("\nServer Shutting Down...");
					inputStreamReader.close();
					clientSocket.close();
					System.out.println("Goodbye.");
				} catch (IOException e) {
					System.out.println("ERROR: Improper Shutdown");
				} catch (NullPointerException e) {
					System.out.println("Adios.");
				}
			}
		});
 
		while (true) {
			try {
 
				clientSocket = serverSocket.accept(); // accept the client connection
				inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
				bufferedReader = new BufferedReader(inputStreamReader); // get the client message
				
				//Heartrate and status
				message = bufferedReader.readLine();
				System.out.println("Android: "+message);
				
				//Latitude
				message = bufferedReader.readLine();
				System.out.println("Latitude: "+message);
				//Longitude
				message = bufferedReader.readLine();
				System.out.println("Longitude: "+message);

				//close connection
				inputStreamReader.close();
				clientSocket.close();
 
			} catch (IOException e) {
				System.out.println("ERROR: Cannot Read Message from device");
			}
		}
 
	}
 
}