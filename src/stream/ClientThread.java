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
				}else if(commande.contains("onveutlalistedesutilisateursconnectestp")){
					envoyerUtilisateurs();
				}else if (commande.contains("SIGNIN ")) {
					String nomDesire = commande.substring(7);
					if(!nomDesire.isEmpty() && parent.estAbsent(nomDesire)){
						nomClient=nomDesire;
						parent.envoyerInfo(commande);
//						parent.envoyerListeClients(this);
						sauvegarderLigne(commande);
					}else{
						envoyerInfo("nomimpossibleaattribuerparcequilestdejapris");
					}
				}else if(commande.contains("SIGNOUT ")) {
					nomClient="";
					parent.envoyerInfo(commande); // tous les clientsThhreads le recoit sauf nous parce que on a plus de nomClient
					envoyerInfo(commande);        // donc on confirme a notre client qu'il est bien deco
					sauvegarderLigne(commande);
				}else if((commande.contains("MESSAGE FROM ") && commande.contains(" TO ") && commande.contains(" CONTENT "))) {
					String destinataire = commande.substring(commande.indexOf(" TO ") + 4, commande.indexOf(" CONTENT "));
					if(!destinataire.equals("all") && parent.estAbsent(destinataire)){
						envoyerInfo("cedestinatairenestpasconnudsl");
					}else{
						parent.envoyerInfo(commande);
						sauvegarderLigne(commande);
					}
				}else if(parent != null && !commande.isEmpty()) {
					parent.envoyerInfo(commande);
					sauvegarderLigne(commande);
				}
			}
    	}catch (Exception e) {
			parent.envoyerInfo("SIGNOUT "+nomClient);
			sauvegarderLigne("SIGNOUT "+nomClient);
			nomClient="";
			parent.remove(this);
        }
	}

	private void envoyerUtilisateurs() {
		String fin ="cestbonjetaienvoyetouslesutilisateurs";
		String[] users=parent.getUsersName();
		for(int i=0 ; i< users.length ; i++){
			if(!users[i].isEmpty())
				envoyerInfo(users[i]);
		}
		envoyerInfo(fin);
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
			parent.remove(this);
		} catch (IOException e) {
			System.out.println("erreur de fermeture de client cote serveur : " + e);
		}
	}

	private void envoyerHistorique() {
		try {
			List<String> fichierHistorique = Files.readAllLines(Paths.get(NOM_FICHIER_CONVERSATION), StandardCharsets.UTF_8);
			Iterator iterator = fichierHistorique.iterator();
			String cmd="";
			envoyerInfo("MESSAGE FROM server TO You CONTENT ----------Début de l'historique-----------");
			while (iterator.hasNext()) {
				cmd = (String)iterator.next();
				if(iterator.hasNext())  // on envoie pas la derniere commande car redondant
					envoyerInfo(cmd);
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

  
