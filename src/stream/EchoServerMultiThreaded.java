/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class EchoServerMultiThreaded  {

	private ServerSocket listenSocket;
	private LinkedList<ClientThread> mesClients;
	private final String NOM_FICHIER_CONVERSATION ="sauvegarde_conversations.txt";
	private PrintWriter writer;
 	/**
  	* main method
	* @param port, String
  	* 
  	**/
	EchoServerMultiThreaded(String port){
		try {
			// Création d'un Socket pour écouter les demandes de connexion sur le serveur
			listenSocket = new ServerSocket(Integer.parseInt(port)); //port
		} catch (IOException e) {
			System.err.println("Erreur de construction de EchoServerMultiThreaded :" + e);
		}
		mesClients=new LinkedList<ClientThread>();

	}

	public void envoyerInfo(String info){
		try {
			writer = new PrintWriter(new FileWriter(NOM_FICHIER_CONVERSATION, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		writer.println(info);
		ClientThread c;
		Iterator iterator = mesClients.iterator();
		while(iterator.hasNext()){
			c = (ClientThread)iterator.next();
			c.envoyerInfo(info);
		}
		writer.close();
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
				envoyerHistorique(ct);
				mesClients.add(ct);
				ct.start();
			}
		}catch (Exception e) {
			System.err.println("Erreur lors de l'execution du serveur :" + e);
		}
	}

	private void envoyerHistorique(ClientThread ct) {
		try {
			List<String> fichierHistorique = Files.readAllLines(Paths.get(NOM_FICHIER_CONVERSATION), StandardCharsets.UTF_8);
			Iterator iterator = fichierHistorique.iterator();
			String line;
			while (iterator.hasNext()) {
				line = (String)iterator.next();
				ct.envoyerInfo(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

  
