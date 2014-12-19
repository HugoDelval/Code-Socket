/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;



public class EchoClient extends Thread {

    private Socket echoSocket = null;
    private PrintStream socOut = null;
    private BufferedReader socIn = null;
    private InterfaceClient interfaceC;
    private String nomUtilisateur="";
    private boolean connected=false;

    EchoClient(String adresseIP, String port, InterfaceClient interC){
        try {
            // Création d'une connexion entre le client et le serveur : précision d'une adresse et d'un port
            echoSocket = new Socket(adresseIP,new Integer(port).intValue());
            // Création d'un buffer qui va stocker ce qu'on recoie du serveur
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            // Création d'un buffer qui va stocker ce qu'on veut envoyer au serveur
            socOut= new PrintStream(echoSocket.getOutputStream());
            // interface de la classe :
            interfaceC=interC;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + adresseIP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: "+ adresseIP);
            System.exit(1);
        }
    }

    public void run() {
        try {
            while (true) {
                // Récupération du nom d'utilisateur qui envoie l'info
                String user = socIn.readLine();
                // Renvoie de la meme chose
                if(interfaceC != null && !user.isEmpty()){
                    // Récupération de la commande de l'utilisateur
                    String commandeUtilisateur = socIn.readLine();
                    // traiter commande utilisateur
                    if(interfaceC != null && !commandeUtilisateur.isEmpty()){
                        // Traiter ce qui est reçu par ClientThread (commandeUtilisateur)
                        if (commandeUtilisateur.contains("SIGNIN ")) {
                            String userSigning = commandeUtilisateur.substring(7);
                            if(userSigning.equals(nomUtilisateur)){
                                connected=true;
                            }
                            socOut.println("server > all : " +userSigning+ " signed in.");
                        }
                    }
                    if(connected){
                        if (commandeUtilisateur.contains("SIGNOUT ")) {
                            String userSignout = commandeUtilisateur.substring(8);
                            if(userSignout.equals(nomUtilisateur)){
                                connected=false;
                            }
                            socOut.println("server > all : " +userSignout+ " signed out.");
                        }
                        if(commandeUtilisateur.contains("MESSAGE FROM ") && commandeUtilisateur.contains(" TO ") && commandeUtilisateur.contains(" CONTENT ")){
                            String expediteur = commandeUtilisateur.substring(13);
                            String destinataire = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" TO ")+4,commandeUtilisateur.indexOf(" CONTENT "));
                            if(expediteur.equals(nomUtilisateur))
                                expediteur = "me";
                            if(destinataire.equals(nomUtilisateur))
                                expediteur = "me";
                            String msg = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" CONTENT ")+8);
                            socOut.println(expediteur+" > "+destinataire+" : " +msg);
                        }
                    }
                }

            }
        }catch (Exception e) {
            System.err.println("Error in ClientThread:" + e);
        }
    }


    public void deconnecter(){
        socOut.close();
        try {
            socIn.close();
            echoSocket.close();
        } catch (IOException e) {
            System.out.println("Erreur deconnexion client : " + e);
        }
    }

    public void envoyerServeur(String commande){
        if(!commande.isEmpty()){
            String retour="";
            String userName="";
            if(commande.contains("CONNECT ")){
                userName = commande.substring(8);
                if(userName.equals("all") || userName.equals("CONNECT ") ||
                        userName.equals("SENDTO ")|| userName.equals(" CONTENT ") ||
                        userName.equals("QUIT")|| userName.equals("SIGNIN ") ||
                        userName.equals("SIGNOUT ")|| userName.equals("MESSAGE FROM ") ||
                        userName.equals(" TO ")){//.................................................. ATTENTION, interdire doublons
                    //error !!!!!!!!!
                }else{
                    retour = "SIGNIN "+userName;
                    nomUtilisateur = userName;
                }
            }
            if(connected && commande.contains("SENDTO ") && commande.contains(" CONTENT ")){
                String destinataire = commande.substring(7,commande.indexOf(" CONTENT "));
                String message = commande.substring(commande.indexOf(" CONTENT ")+8);
                retour = "MESSAGE FROM " + nomUtilisateur + " TO " +destinataire +" CONTENT " + message;
            }
            if(connected && commande.equals("QUIT")){
                retour = "SIGNOUT "+nomUtilisateur;
            }
            socOut.println(retour);
        }
    }
}


