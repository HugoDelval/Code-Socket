/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
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


public class EchoClient extends Thread {

    private Socket echoSocket = null;
    private PrintStream socOut = null;
    private BufferedReader socIn = null;
    private InterfaceClient interfaceC;
    private String nomUtilisateur="";
    private boolean connected=false;
    private final String NOM_FICHIER_CONVERSATION ="sauvegarde_conversations.txt";

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
                // Récupération de la commande de l'utilisateur
                String commandeUtilisateur = socIn.readLine();
                // traiter commande utilisateur
                if(interfaceC != null && !commandeUtilisateur.isEmpty()){
                    // Traiter ce qui est reçu par ClientThread (commandeUtilisateur)
                    if (commandeUtilisateur.contains("SIGNIN ")) {
                        String userSigning = commandeUtilisateur.substring(7);
                        if(userSigning.equals(nomUtilisateur)){
                            connected=true;
                            envoyerHistorique();
                        }
                        if(connected) {
                            //interfaceC.envoyerInfo(ligne);
                            interfaceC.envoyerInfo("------- Debut de votre connection -------");
                            sauvegarderLigne( "server > all : " + userSigning + " signed in.\n");
                            if(userSigning.equals(nomUtilisateur)){
                                userSigning = "You've";
                            }
                            interfaceC.envoyerInfo( "server > all : " + userSigning + " signed in.\n");
                        }
                    }
                }
                if(connected){
                    if (commandeUtilisateur.contains("SIGNOUT ")) {
                        String userSignout = commandeUtilisateur.substring(8);
                        if(userSignout.equals(nomUtilisateur)){
                            connected=false;
                        }
                        sauvegarderLigne( "server > all : " + userSignout + " signed out.\n");
                        if(userSignout.equals(nomUtilisateur)){
                            userSignout = "You've";
                        }
                        interfaceC.envoyerInfo( "server > all : " + userSignout + " signed out.\n");
                    }
                    if(commandeUtilisateur.contains("MESSAGE FROM ") && commandeUtilisateur.contains(" TO ") && commandeUtilisateur.contains(" CONTENT ")){
                        String expediteur = commandeUtilisateur.substring(13);
                        String destinataire = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" TO ")+4,commandeUtilisateur.indexOf(" CONTENT "));
                        String msg = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" CONTENT ")+8);
                        sauvegarderLigne(expediteur + " > " + destinataire + " : " + msg + '\n');
                        if(expediteur.equals(nomUtilisateur))
                            expediteur = "me";
                        if(destinataire.equals(nomUtilisateur))
                            expediteur = "me";
                        interfaceC.envoyerInfo(expediteur+" > "+destinataire+" : " +msg+'\n');
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
            envoyerServeur("QUIT");
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
                        userName.equals(" TO ")){                             //.................................................. ATTENTION, interdire doublons
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

    private void envoyerHistorique() {
        try {
            List<String> fichierHistorique = Files.readAllLines(Paths.get(NOM_FICHIER_CONVERSATION), StandardCharsets.UTF_8);
            Iterator iterator = fichierHistorique.iterator();
            String cmd;
            while (iterator.hasNext()) {
                cmd = (String)iterator.next();
                interfaceC.envoyerInfo(cmd + '\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sauvegarderLigne(String ligne){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(NOM_FICHIER_CONVERSATION, true));
            writer.print(ligne);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}


