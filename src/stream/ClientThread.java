/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
	private EchoServerMultiThreaded parent;
	// Flux d'entrée du point de vue Client
	BufferedReader socIn = null;
	// Flux de sortie du point de vue Client
	PrintStream socOut = null;
	private final String NOM_FICHIER_CONVERSATION ="sauvegarde_conversations.txt";
	private String nomClient="";

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
				String commande = socIn.readLine();
				// Renvoie de la meme chose
				if(commande.contains("ilveutlhistoriquealorsenvoielui")){
					envoyerHistorique();
				}else if (commande.contains("SIGNIN ")) {
					String nomDesire = commande.substring(7);
					if(!nomDesire.isEmpty() && parent.register(nomDesire)){
						nomClient=nomDesire;
						parent.envoyerInfo(commande);
						sauvegarderLigne(commande);
					}else{
						envoyerInfo("nomimpossibleaattribuerparcequilestdejapris");
					}
				}else if(parent != null && !commande.isEmpty()) {
					parent.envoyerInfo(commande);
					sauvegarderLigne(commande);
				}
			}
    	}catch (Exception e) {
        	System.err.println("Error in ClientThread:" + e);
        }
	}

	public void envoyerInfo(String commande)
	{
		if(!commande.isEmpty()){
			socOut.println(commande);
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

	private void envoyerHistorique() {
		try {
			List<String> fichierHistorique = Files.readAllLines(Paths.get(NOM_FICHIER_CONVERSATION), StandardCharsets.UTF_8);
			Iterator iterator = fichierHistorique.iterator();
			String cmd;
			envoyerInfo("MESSAGE FROM server TO You CONTENT ----------Début de l'historique-----------");
			while (iterator.hasNext()) {
				cmd = (String)iterator.next();
				if(iterator.hasNext())  // on envoie pas la derniere commande car redondant
					envoyerInfo(cmd + '\r' + '\n');
			}
			envoyerInfo("MESSAGE FROM server TO You CONTENT -----------Fin de l'historique------------");
			envoyerInfo("cestlafindelhistoriquetupeuxarreterdesimuler");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sauvegarderLigne(String ligne){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(NOM_FICHIER_CONVERSATION, true));
			writer.println(ligne);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public String getNomClient(){
		return nomClient;
	}

}

  
