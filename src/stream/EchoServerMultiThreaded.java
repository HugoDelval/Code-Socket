/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.*;


public class EchoServerMultiThreaded  extends Thread{

	private ServerSocket listenSocket;
	private LinkedList<ClientThread> mesClients;
	public BindException erreurPort;
 	/**
  	* main method
	* @param port, String
  	* 
  	**/
	EchoServerMultiThreaded(String port){
		erreurPort = null;
		try {
			// Création d'un Socket pour écouter les demandes de connexion sur le serveur
			listenSocket = new ServerSocket(Integer.parseInt(port)); //port
		} catch (BindException e) {
			// Traitement de l'erreur dans l'interface serveur
			erreurPort = e;
		} catch (IOException e) {
			System.err.println("Erreur de construction de EchoServerMultiThreaded :" + e);
		}
		mesClients=new LinkedList<ClientThread>();

	}

	public void envoyerInfo(String commande){
		ClientThread c;
		Iterator iterator = mesClients.iterator();
		while(iterator.hasNext()){
			c = (ClientThread)iterator.next();
			c.envoyerInfo(commande);
		}
	}

	public void run() {
		try {
			System.out.println("Server ready...");
			// Ecoute infinie pour savoir si un client se connecte au serveur
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from: " + clientSocket.getInetAddress());
				// Création d'un thread par client (communication unique pour chaque client)
				ClientThread ct = new ClientThread(clientSocket,this);
				mesClients.add(ct);
				ct.start();
			}
		}
		catch (SocketException e) {
			// Pas besoin d'envoyer quelque chose, on se déconnecte correctement
			//System.out.println("Le serveur est bien déconnecté : " + e);
		}
		catch (Exception e) {
			System.err.println("Erreur lors de l'execution du serveur : " + e);
		}
	}

	public void decoServeur()
	{
		try {
			System.out.println("Server disconnected...");
			//Déconnecter tous les clients d'abord
			ClientThread c;
			Iterator iterator = mesClients.iterator();
			while(iterator.hasNext()){
				c = (ClientThread)iterator.next();
				c.deconnecter();
			}
			// On efface la liste de clients
			mesClients.clear();

			//Eteindre le serveur (on "arrête" la boucle infinie)
			try {
				listenSocket.close();
			} catch (IOException e) {
				System.out.println("Erreur deconnexion serveur : "+e);
			}

		} catch (Exception e)
		{
			System.err.println("Erreur lors de la déconnexion du serveur : " + e);
		}
	}

}

  
