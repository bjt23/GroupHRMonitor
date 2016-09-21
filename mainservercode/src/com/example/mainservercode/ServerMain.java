package com.example.mainservercode;

/* 
* Main Server code for Senior Design Project
* by Ben Tomasulo and Matt Rowe
* Accepts TCP data from an android device over internet 
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//Server Class
public class ServerMain{
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static InputStreamReader inputStreamReader;
	private static ObjectOutputStream listout;
	private static BufferedReader bufferedReader;
	private static String message;
 
	public static void main(String[] args) {
		try {
			serverSocket = new ServerSocket(4444); // Server socket
 
		} catch (IOException e) {
			System.out.println("ERROR: Cannot listen on port 4444");
		}
 
		System.out.println("Server started. Listening on port 4444");

		//phone list
		ArrayList<Pcontain> phonelist=new ArrayList<Pcontain>();
		
		//temporary phone data for list updater
		String tname;
		String trate;
		String tstat;
		double tlat;
		double tlon;
		boolean listup;

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try{
					System.out.println("\nServer Shutting Down...");
					inputStreamReader.close();
					listout.close();
					clientSocket.close();
					serverSocket.close();
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
				
				//Recieve Data
				//Name
				message = bufferedReader.readLine();
				System.out.println("\nName: "+message);
				tname=message;
				//Heartrate
				message = bufferedReader.readLine();
				System.out.println("Heart Rate: "+message);
				trate=message;
				//Status
				message = bufferedReader.readLine();
				System.out.println("Status: "+message);
				tstat=message;
				//Latitude
				message = bufferedReader.readLine();
				System.out.println("Latitude: "+message);
				tlat=Double.parseDouble(message);
				//Longitude
				message = bufferedReader.readLine();
				System.out.println("Longitude: "+message);
				tlon=Double.parseDouble(message);
				

				inputStreamReader.close();
				clientSocket = serverSocket.accept();

				//Update list of phones
				//System.out.println(""+phonelist.size()+"N"+tname);
				listup=false;
				for(int i=0;i<phonelist.size();i++)
				{
					//System.out.println(""+phonelist.get(0).aname);
					if(tname.equals(phonelist.get(i).aname)) {
						phonelist.set(i, new Pcontain(tname, trate, tstat, tlat, tlon));
						listup=true;
						break;
					}
				}
				if(!listup){
					phonelist.add(new Pcontain(tname, trate, tstat, tlat, tlon));
				}
				
				//Send phone list to phones
				listout=new ObjectOutputStream(clientSocket.getOutputStream());
				listout.writeObject(phonelist);

				//close connection
				listout.close();
				clientSocket.close();
			
 
			} catch (IOException e) {
				System.out.println("ERROR: Cannot Read Message from device");
			}
		}
 
	}
 
}