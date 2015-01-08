package stream;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * EchoServerMultiThreaded
 * Exemple de serveur TCP multithread
 *
 * @authors B3424
 * @see stream.ClientThread
 */
public class EchoServerMultiThreaded extends Thread {

	/**
	 * Crée une socket qui va permettre aux serveurs d'écouter et d'accepter les connexions clients.
	 */
	private ServerSocket listenSocket;

	/**
	 * Crée une liste de 'ClientThread' qui va stocker tous les clients connectés au serveur.
	 */
	private LinkedList<ClientThread> mesClients;

	/**
	 * Crée un stockage pour une exception de type BindException
	 */
	public BindException erreurPort;

 	/**
  	* Constructeur de EchoServerMultiThreaded :
	 *
	* @param port, String
  	* 
  	**/
	EchoServerMultiThreaded(String port){
		erreurPort = null;
		mesClients = new LinkedList<ClientThread>();
		try {
			// Création d'un Socket pour écouter les demandes de connexion sur le serveur
			listenSocket = new ServerSocket(Integer.parseInt(port));
		} catch (BindException e) {
			// Traitement de l'erreur <BindException> dans l'interface serveur
			erreurPort = e;
		} catch (IOException e) {
			// Traitement du reste des erreurs
			System.err.println("Erreur de construction de EchoServerMultiThreaded :" + e);
		}
	}

	/**
	 * Méthode envoyerInfo :
	 * Permet d'envoyer une information sous la forme d'une chaîne de caractères
	 * à tous les clients connectés.
	 *
	 * @param commande, String comportant la commande à envoyer.
	 *
	 **/
	public void envoyerInfo(String commande){
		ClientThread c;
		Iterator iterator = mesClients.iterator();
		while(iterator.hasNext()){
			c = (ClientThread)iterator.next();
			//
			if(!c.getNomClient().isEmpty()) {
				c.envoyerInfo(commande);
			}
		}
	}

	/**
	 * Méthode run :
	 * Permet de démarrer un thread infini qui va attendre des demandes de connexions au serveur.
	 * A chaque fois qu'un client demande à se connecter, un nouveau canal de communication entre le serveur et le
	 * client va être créé (communication unique pour chaque client = un thread par client).
	 */
	public void run() {
		try {
			//System.out.println("Server ready...");
			// Ecoute infinie pour savoir si un client se connecte au serveur
			while (true) {
				Socket clientSocket = listenSocket.accept();
				//System.out.println("Connexion from: " + clientSocket.getInetAddress());
				// Création d'un thread par client (communication unique pour chaque client)
				ClientThread ct = new ClientThread(clientSocket,this);
				mesClients.add(ct);
				ct.start();
			}
		}
		catch (SocketException e) {
			// Pas besoin d'envoyer une erreur, on se déconnecte correctement
			//System.out.println("Le serveur est bien déconnecté : " + e);
		}
		catch (Exception e) {
			// Traitement du reste des erreurs
			//System.err.println("Erreur lors de l'execution du serveur : " + e);
		}
	}

	/**
	 * Méthode decoServeur :
	 * Permet de déconnecter proprement le serveur en déconnectant dans un premier temps tous les
	 * clients.
	 */
	public void decoServeur()
	{
		try {
			//System.out.println("Server disconnected...");
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

		} catch (Exception e) {
			// Traitement du reste des erreurs
			System.err.println("Erreur lors de la déconnexion du serveur : " + e);
		}
	}

	/**
	 * Méthode estAbsent :
	 * Permet de savoir si un client est présent dans la liste de clients connectés au serveur.
	 *
	 * @param nomDesire, String représentant le nom du client.
	 * @return Un booleen qui vaut 'true' si le client est absent de la liste des clients connectés
	 * et qui vaut 'faux' s'il y est présent.
	 */
	public boolean estAbsent(String nomDesire) {
		boolean res=true;
		ClientThread c;
		Iterator iterator = mesClients.iterator();
		while(iterator.hasNext() && res){
			c = (ClientThread)iterator.next();
			res = !((c.getNomClient()).equals(nomDesire));
		}
		return res;
	}

	/**
	 * Méthode remove :
	 * Permet de retirer un client de la liste des client connectés.
	 *
	 * @param ct, ClientThread
	 */
	public void remove(ClientThread ct) {
		mesClients.remove(ct);
	}

	/**
	 * Méthode getUserName :
	 * Permet d'envoyer les noms des clients connectés afin que le client puisse voir quelles personnes
	 * sont connectées au chat.
	 *
	 * @return Un tableau de String qui stocke tous les noms des clients connectés.
	 * @see stream.ClientThread
	 * @see stream.EchoClient
	 */
	public String[] getUsersName() {
		// Création d'un tableau de la taille de la liste des clients connectés stockés dans un LinkedList
		String [] res = new String[mesClients.size()];
		int i=0;
		ClientThread c;
		Iterator iterator = mesClients.iterator();
		while(iterator.hasNext()){
			c = (ClientThread)iterator.next();
			res[i] = c.getNomClient();
			i++;
		}
		return res;
	}
}

  
