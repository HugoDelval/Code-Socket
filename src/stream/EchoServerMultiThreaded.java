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
	private ClientThread[] mesClients;
	private int numClient;
	private boolean plein;
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
	EchoServerMultiThreaded(String port){
		try {
			// Création d'un Socket pour écouter les demandes de connexion sur le serveur
			listenSocket = new ServerSocket(Integer.parseInt(port)); //port
		} catch (IOException e) {
			System.err.println("Erreur de construction de EchoServerMultiThreaded :" + e);
		}
		mesClients=new ClientThread[100];
		numClient=-1;
		plein=false;
	}

	public void envoyerInfo(String info){
		if(plein){
			for(int i=0; i<mesClients.length ; i++){
				mesClients[i].envoyerInfo(info);
			}
		}else{
			for(int i=0; i<=this.numClient ; i++){
				mesClients[i].envoyerInfo(info);
			}
		}

	}

	public void lancerServeur(){
		System.out.println("Server ready...");
		try {
			// Ecoute infinie pour savoir si un client se connecte au serveur
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from: " + clientSocket.getInetAddress());
				// Création d'un thread par client (communication unique pour chaque client)
				ClientThread ct = new ClientThread(clientSocket,this);
				// on kicke des clients si il y en a trop et on incremente le compteur
				if(numClient==99){
					numClient=0;
					plein = true;
					mesClients[0].deconnecter();
				}else{
					numClient++;
					if(plein){
						mesClients[numClient].deconnecter();
					}
				}
				// penser a notifier le client !
				mesClients[numClient]=ct;
				ct.start();
			}
		}catch (Exception e) {
			System.err.println("Erreur lors de l'execution du serveur :" + e);
		}
	}

}

  
