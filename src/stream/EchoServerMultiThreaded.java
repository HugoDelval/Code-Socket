/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class EchoServerMultiThreaded  {

	private ServerSocket listenSocket;
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
	EchoServerMultiThreaded(String port){
		try {
			listenSocket = new ServerSocket(Integer.parseInt(port)); //port
		} catch (IOException e) {
			System.err.println("Erreur de construction de EchoServerMultiThreaded :" + e);
			e.printStackTrace();
		}
	}

	public void lancerServeur(){
		System.out.println("Server ready...");
		try {
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
		}catch (Exception e) {
			System.err.println("Erreur lors de l'execution du serveur :" + e);
			e.printStackTrace();
		}
	}
  }

  
