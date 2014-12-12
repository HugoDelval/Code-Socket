/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;



public class EchoClient {

    private Socket echoSocket = null;
    private PrintStream socOut = null;
    private BufferedReader stdIn = null;
    private BufferedReader socIn = null;

    EchoClient(String adresseIP, String port){
        try {
            // Création d'une connexion entre le client et le serveur : précision d'une adresse et d'un port
            echoSocket = new Socket(adresseIP,new Integer(port).intValue());
            // Création d'un buffer qui va stocker ce qu'on recoie du serveur
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            // Création d'un buffer qui va stocker ce qu'on veut envoyer au serveur
            socOut= new PrintStream(echoSocket.getOutputStream());
            // Création d'un buffer qui va stocker ce qu'on tape dans la console
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + adresseIP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: "+ adresseIP);
            System.exit(1);
        }
    }

    public void lancerClient() throws IOException {
        String line;
        while (true) {
            // On lit ce que rentre l'utilisateur
        	line=stdIn.readLine();
        	if (line.equals(".")) break;
            // On envoie au serveur
        	socOut.println(line);
            // On lit ce qu'on reçoie du serveur
        	System.out.println("echo: " + socIn.readLine());
        }
        deconnecter();
    }

    public void deconnecter(){
        socOut.close();
        try {
            socIn.close();
            stdIn.close();
            echoSocket.close();
        } catch (IOException e) {
            System.out.println("Erreur deconnexion client : "+e);
        }
    }
}


