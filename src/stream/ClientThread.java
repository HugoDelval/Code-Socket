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
	private EchoServerMultiThreaded parent;
	// Flux d'entrée du point de vue Client
	BufferedReader socIn = null;
	// Flux de sortie du point de vue Client
	PrintStream socOut = null;

	ClientThread(Socket s, EchoServerMultiThreaded p) {
		try {
			this.clientSocket = s;
			socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			socOut = new PrintStream(clientSocket.getOutputStream());
			parent = p;
		} catch (IOException e) {
			System.out.println("Erreur construction client du cote serveur : "+e);
		}

	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
		try {
    		while (true) {
				// Récupération du nom du client
				String user = socIn.readLine();
				// Renvoie de la meme chose
				if(parent != null && !user.isEmpty()){
					String commande = socIn.readLine();
					// Renvoie de la meme chose
					if(parent != null && !commande.isEmpty())
						parent.envoyerInfo(commande,user);
				}
    		}
    	}catch (Exception e) {
        	System.err.println("Error in ClientThread:" + e);
        }
	}

	public void envoyerInfo(String commande){
		envoyerInfo(commande,"");
	}

	public void envoyerInfo(String commande, String user)
	{
		if(!commande.isEmpty()){
			String retour="";
			if(commande.contains("CONNECT ")){
				String username = commande.substring(8);
				if(username.equals("all")){
					//error !!!!!!!!!
				}else{
					retour = "SIGNIN "+username;
				}
			}
			if(commande.contains("SENDTO ") && commande.contains(" CONTENT ")){
				String destinataire = commande.substring(7,commande.indexOf(" CONTENT "));
				String message = commande.substring(commande.indexOf(" CONTENT ")+8);
				retour = "MESSAGE FROM " + user + " TO " +destinataire +" CONTENT " + message;
			}
			if(commande.equals("QUIT")){
				retour = "SIGNOUT "+user;
			}
			socOut.println(retour);
		}
	}

	public void deconnecter(){
		System.out.println("Deconnection ClientThread");
		try {
			socOut.close();
			socIn.close();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("erreur de fermeture de client cote serveur : " + e);
		}
	}
}

  
