package stream;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * Serveur sous forme de thread, cree par EchoServerMultiThreaded
 *
 * @authors B3424
 * @see stream.EchoServerMultiThreaded
 */
public class ClientThread extends Thread {
	/**
	 * Le socket du client, permettant de communique avec lui
	 */
	private Socket clientSocket;
	/**
	 * le createur de ce thread, permettant d'appeler des methodes de 'EchoServerMultiThreaded' , relayant des informations a tous les threads
	 */
	private EchoServerMultiThreaded parent;
	/**
	 * Flux d'entrée de l'information venant du Client
	 */
	private BufferedReader socIn = null;
	/**
	 * Flux de sortie de l'information vers le Client
	 */
	private PrintStream socOut = null;
	/**
	 * Nom du fichier contenant l'historique des conversations
	 */
	private final String NOM_FICHIER_CONVERSATION ="sauvegarde_conversations.txt";
	/**
	 * Le nom du client avec lequel on communique (si connu)
	 */
	private String nomClient="";

	/**
	 * Constructeur de la classe. Initialise les Flux et les autres attributs
	 * @param s , Le socket permettant de communiquer avec le Client
	 * @param p , Serveur Global, le parent de ce thread
	 */
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
	 * Methode principale du thread, ecoute le flux d'entree et en deduit des commandes a envoyer au Client et/ou au serveur global.
	 */
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
			parent.envoyerInfo("SIGNOUT " + nomClient);
			sauvegarderLigne("SIGNOUT "+nomClient);
			nomClient="";
			deconnecter();
        }
	}

	/**
	 * Envoie la liste des utilisateurs connectes au Client
	 */
	private void envoyerUtilisateurs() {
		String fin ="cestbonjetaienvoyetouslesutilisateurs";
		String[] users=parent.getUsersName();
		for(int i=0 ; i< users.length ; i++){
			if(!users[i].isEmpty())
				envoyerInfo(users[i]);
		}
		envoyerInfo(fin);
	}

	/**
	 * Envoie une chaine de caractere au Client
	 * @param commande , la chaine de caractere a envoyer
	 */
	public void envoyerInfo(String commande)
	{
		if(!commande.isEmpty()){
			socOut.println(commande);
		}
	}

	/**
	 * Deconnecte cet objet du client puis demande au Serveur global de le supprimer
	 */
	public void deconnecter(){
		//System.out.println("Deconnection ClientThread");
		try {
			socOut.close();
			socIn.close();
			clientSocket.close();
			parent.remove(this);
		} catch (IOException e) {
			System.out.println("erreur de fermeture de client cote serveur : " + e);
		}
	}

	/**
	 * Envoie l'historique des messages contenu dans le fichier 'NOM_FICHIER_CONVERSATION' au Client
	 */
	private void envoyerHistorique() {
		try {
			List<String> fichierHistorique = Files.readAllLines(Paths.get(NOM_FICHIER_CONVERSATION), StandardCharsets.UTF_8);
			Iterator iterator = fichierHistorique.iterator();
			String cmd="";
			envoyerInfo("MESSAGE FROM server TO me CONTENT ----------Début de l'historique-----------");
			while (iterator.hasNext()) {
				cmd = (String)iterator.next();
				if(iterator.hasNext())  // on envoie pas la derniere commande car redondant
					envoyerInfo(cmd);
			}
			envoyerInfo("MESSAGE FROM server TO me CONTENT -----------Fin de l'historique------------");
			envoyerInfo("cestlafindelhistoriquetupeuxarreterdesimuler");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enregistre une chaine de caracteres dans le fichier de sauvegarde : 'NOM_FICHIER_CONVERSATION'
	 * @param ligne , la chaine de caracteres a sauvegarder
	 */
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

	/**
	 * Getteur du nomClient
	 * @return le nom du client
	 */
	public String getNomClient(){
		return nomClient;
	}

}

  
