/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class EchoClient extends Thread {

    private Socket echoSocket = null;
    private PrintStream socOut = null;
    private BufferedReader socIn = null;
    private InterfaceClient interfaceC;
    private String nomUtilisateur="";
    //au chat
    private boolean connected=false;
    private boolean historiqueEnCours = false;

    EchoClient(String adresseIP, String port, InterfaceClient interC) throws IOException {
        // Création d'une connexion entre le client et le serveur : précision d'une adresse et d'un port
        echoSocket = new Socket(adresseIP,new Integer(port).intValue());
        // Création d'un buffer qui va stocker ce qu'on recoie du serveur
        socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        // Création d'un buffer qui va stocker ce qu'on veut envoyer au serveur
        socOut= new PrintStream(echoSocket.getOutputStream());
        // interface de la classe :
        interfaceC=interC;
    }

    public void run() {
        try {
            while (true) {
                // Récupération de la commande de l'utilisateur envoye par le serveur
                String commandeUtilisateur = socIn.readLine();
                // traiter commande utilisateur
                if(interfaceC != null && !commandeUtilisateur.isEmpty()){
                    // Traiter ce qui est reçu par ClientThread (commandeUtilisateur)
                    if (commandeUtilisateur.contains("SIGNIN ")) {
                        String userSigning = commandeUtilisateur.substring(7);
                        if(userSigning.equals(nomUtilisateur) && !connected && !historiqueEnCours){
                            interfaceC.envoyerInfo("------- Debut de votre session -------\r\n");
                            connected=true;
                            historiqueEnCours=true;
                            afficherUtilisateurs();
                            socOut.println("ilveutlhistoriquealorsenvoielui");
                        }
                        if(connected) {
                            if(userSigning.equals(nomUtilisateur)){
                                userSigning = "You've";
                            }
                            interfaceC.envoyerInfo( "server > all : " + userSigning + " signed in.\r\n");
                        }
                        if(connected && !historiqueEnCours){
                            // Un utilisateur vient de se connecter ! Ajout dans la liste
                            afficherUtilisateurs();
                        }
                    }else if(commandeUtilisateur.contains("nomimpossibleaattribuerparcequilestdejapris")) {
                        JOptionPane.showMessageDialog(interfaceC,"Vous ne pouvez pas choisir cet username, il est déjà pris.");
                    }else if(connected) {
                        if (commandeUtilisateur.contains("SIGNOUT ")) {
                            String userSignout = commandeUtilisateur.substring(8);
                            String user=userSignout;
                            if (userSignout.equals(nomUtilisateur)) {
                                user = "You've";
                            }
                            interfaceC.envoyerInfo("server > all : " + user + " signed out.\r\n");
                            if (userSignout.equals(nomUtilisateur) && !historiqueEnCours) {
                                interfaceC.envoyerInfo("--------- Fin de votre session -------\r\n");
                                connected = false;
                            }
                            if(connected && !historiqueEnCours){
                                // Un utilisateur vient de se deconnecter ! Suppression dans la liste
                                afficherUtilisateurs();
                            }
                        }else if (commandeUtilisateur.contains("MESSAGE FROM ") && commandeUtilisateur.contains(" TO ") && commandeUtilisateur.contains(" CONTENT ")) {
                            String expediteur = commandeUtilisateur.substring(commandeUtilisateur.indexOf("MESSAGE FROM ") + 13, commandeUtilisateur.indexOf(" TO "));
                            String destinataire = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" TO ") + 4, commandeUtilisateur.indexOf(" CONTENT "));
                            String msg = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" CONTENT ") + 8);
                            if (expediteur.equals(nomUtilisateur))
                                expediteur = "You";
                            if (destinataire.equals(nomUtilisateur))
                                destinataire = "You";
                            interfaceC.envoyerInfo(expediteur + " > " + destinataire + " : " + msg + '\r' + '\n');
                        }else if (commandeUtilisateur.contains("cestlafindelhistoriquetupeuxarreterdesimuler")) {
                            historiqueEnCours = false;
                        }else if (commandeUtilisateur.contains("cedestinatairenestpasconnudsl")) {
                            JOptionPane.showMessageDialog(interfaceC,"Le destinataire que vous avez demandé pas n'existe ou plus.");
                        }
                    }
                }
            }
        }catch (Exception e) {
            // on s'est deconnecte du serveur
            connected=false;
            interfaceC.premiereEtape();
            JOptionPane.showMessageDialog(interfaceC,"Déconnecté");
        }
    }

    private void afficherUtilisateurs() throws IOException {
        socOut.println("onveutlalistedesutilisateursconnectestp");
        LinkedList<String> utilisateursCo = new LinkedList<String>();
        String utilisateur = socIn.readLine();
        utilisateursCo.add("all");
        while(!utilisateur.contains("cestbonjetaienvoyetouslesutilisateurs")){
            utilisateursCo.add(utilisateur);
            utilisateur = socIn.readLine();
        }
        interfaceC.envoyerClientsCo(utilisateursCo.toArray(new String[utilisateursCo.size()]));
    }


    public void deconnecter() {
        if (connected) { // au chat
            envoyerServeur("QUIT");
            connected=false;
        }
        try {
            socOut.close();
            socIn.close();
            echoSocket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(interfaceC, "Erreur deconnexion : " + e);
        }
    }

    public void envoyerServeur(String commande){
        if(!commande.isEmpty()){
            String retour="";
            String userName="";
            if(commande.contains("CONNECT ") && !connected){
                userName = commande.substring(8);
                if(userName.equals("all") || userName.equals("CONNECT ") ||
                        userName.equals("SENDTO ")|| userName.equals(" CONTENT ") ||
                        userName.equals("QUIT")|| userName.equals("SIGNIN ") ||
                        userName.equals("SIGNOUT ")|| userName.equals("MESSAGE FROM ") ||
                        userName.equals(" TO ")){                             //.................................................. ATTENTION, interdire doublons
                    JOptionPane.showMessageDialog(interfaceC,"Vous ne pouvez pas choisir cet username, il contient des mot interdits.");
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


