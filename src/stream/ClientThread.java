/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
		try {
			// Flux d'entrée du point de vue Client
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// Flux de sortie du point de vue Client
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		while (true) {
				// Lecture de ce qu'on écrit sur la console
				String line = socIn.readLine();
				// Envoie vers le serveur
    		  	socOut.println(line);
    		}
    	}catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
	}
  
  }

  
