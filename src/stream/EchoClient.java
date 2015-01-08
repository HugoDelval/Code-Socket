package stream;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * Client TCP
 *
 * @author B3424
 */
public class EchoClient extends Thread {

    /**
     * Cree une socket qui va permettre de connecter le client et le serveur ensemble.
     */
    private Socket echoSocket = null;

    /**
     * Cree un flux qui va permettre de stocker les informations a envoyer au serveur.
     */
    private PrintStream socOut = null;

    /**
     * Cree un flux qui va permettre de stocker les informations recues de la part du serveur.
     */
    private BufferedReader socIn = null;

    /**
     * Lie une interface client pour chaque objet EchoClient.
     */
    private InterfaceClient interfaceC;

    /**
     * Contient un nom d'utilisateur actuellement connecte sur la machine.
     *
     * @see EchoClient#afficherUtilisateurs()
     */
    private String nomUtilisateur="";

    /**
     * Permet de savoir si le client s'est connecte au serveur.
     */
    private boolean connected=false;

    /**
     * Permet de savoir si l'historique s'affiche a un moment donne a l'ecran.
     *
     * @see EchoClient#run()
     */
    private boolean historiqueEnCours = false;

    /**
     * Construit un objet EchoClient.
     *
     * @param adresseIP L'adresse IP du serveur auquel le client souhaite se connecter
     * @param port Le port sur lequel le serveur est lance
     * @param interC L'interface client auquel est lie cet objet
     * @throws IOException
     */
    EchoClient(String adresseIP, String port, InterfaceClient interC) throws IOException {
        // Creation d'une connexion entre le client et le serveur : precision d'une adresse et d'un port
        echoSocket = new Socket(adresseIP,new Integer(port).intValue());
        // Creation d'un buffer qui va stocker ce qu'on recoie du serveur
        socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        // Creation d'un buffer qui va stocker ce qu'on veut envoyer au serveur
        socOut= new PrintStream(echoSocket.getOutputStream());
        // Interface de la classe :
        interfaceC=interC;
    }

    /**
     * Permet de creer un thread infini qui va constamment communiquer avec le serveur en se servant des buffers
     * definis lors de la construction de l'objet.
     */
    public void run() {
        try {
            while (true) {
                // Recuperation de la commande de l'utilisateur envoye par le serveur
                String commandeUtilisateur = socIn.readLine();

                // Traiter les differentes commandes reçues par le serveur (de la part de ClientThread car canal
                // de communication unique entre le client et le serveur)
                if(interfaceC != null && !commandeUtilisateur.isEmpty()) {

                    // Mot-cle de connexion : SIGNIN
                    if (commandeUtilisateur.contains("SIGNIN ")) {
                        // Extraction du pseudo de l'utilisateur qui souhaite se connecter
                        String userSigning = commandeUtilisateur.substring(7);

                        // Affichage de l'historique de la conversation dans un premier temps
                        if(userSigning.equals(nomUtilisateur) && !connected && !historiqueEnCours) {
                            interfaceC.envoyerInfo("------- Début de votre session -------\r\n");
                            connected=true;
                            historiqueEnCours=true;
                            afficherUtilisateurs();
                            socOut.println("ilveutlhistoriquealorsenvoielui");
                        }

                        // Faire savoir à l'utilisateur qu'il est bien connecte
                        if(connected) {
                            if(userSigning.equals(nomUtilisateur)) {
                                userSigning = "You've";
                            }
                            // Faire savoir aux autres membres du chat que le client vient de se connecter au chat
                            interfaceC.envoyerInfo( "server > all : " + userSigning + " signed in.\r\n");
                        }

                        // Permet qu client de connaître la liste des utilisateurs connectes
                        if(connected && !historiqueEnCours){
                            afficherUtilisateurs();
                        }

                    }

                    // Si le nom d'utilisateur est dejà pris dans le chat, le client ne peut pas prendre ce nom et il
                    // doit en choisir un autre
                    else if(commandeUtilisateur.contains("nomimpossibleaattribuerparcequilestdejapris")) {
                        JOptionPane.showMessageDialog(interfaceC,"Vous ne pouvez pas choisir cet username, il est déjà pris.");
                    }

                    // Action uniquement realisees si le client est connecte
                    else if(connected) {

                        // Mot-cle de deconnexion : SIGNOUT
                        if (commandeUtilisateur.contains("SIGNOUT ")) {
                            // Recuperation du nom du client qui se deconnecte
                            String userSignout = commandeUtilisateur.substring(8);
                            String user=userSignout;

                            // Si le client qui se deconnecte est l'utilisateur lui-même
                            if (userSignout.equals(nomUtilisateur)) {
                                user = "You've";
                            }
                            // Faire savoir aux autres membres du chat que l'utilisateur s'est deconnecte
                            interfaceC.envoyerInfo("server > all : " + user + " signed out.\r\n");

                            // Informer l'utilisateur que c'est la fin de sa session (il n'est plus connecte)
                            if (userSignout.equals(nomUtilisateur) && !historiqueEnCours) {
                                interfaceC.envoyerInfo("--------- Fin de votre session -------\r\n");
                                connected = false;
                            }

                            // Affichage des utilisateurs connectes
                            if(!historiqueEnCours){
                                afficherUtilisateurs();
                            }
                        }

                        // Si les mots-cles designent un message envoye à une personne en particulier : MESSAGE FROM ... TO ... CONTENT ...
                        else if (commandeUtilisateur.contains("MESSAGE FROM ") && commandeUtilisateur.contains(" TO ") && commandeUtilisateur.contains(" CONTENT ")) {
                            // Recuperation des differentes chaînes de caractères interessantes (expediteur, destinataire et message)
                            String expediteur = commandeUtilisateur.substring(commandeUtilisateur.indexOf("MESSAGE FROM ") + 13, commandeUtilisateur.indexOf(" TO "));
                            String destinataire = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" TO ") + 4, commandeUtilisateur.indexOf(" CONTENT "));
                            String msg = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" CONTENT ") + 8);

                            // Si le message est envoye de la part de l'utilisateur lui-même
                            if (expediteur.equals(nomUtilisateur))
                                expediteur = "me";

                            // Si le message est destine à l'utilisateur lui-même
                            if (destinataire.equals(nomUtilisateur))
                                destinataire = "me";

                            // Envoyer cette info au serveur pour qu'il la redistribue correctement
                            interfaceC.envoyerInfo(expediteur + " > " + destinataire + " : " + msg + '\r' + '\n');
                        }

                        // Si c'est la fin de la transmission de l'historique
                        else if (commandeUtilisateur.contains("cestlafindelhistoriquetupeuxarreterdesimuler")) {
                            historiqueEnCours = false;
                        }

                        // Si le destinataire n'est pas connu dans le chat (c'est-à-dire qu'il ne fait partie des
                        // connectes !)
                        else if (commandeUtilisateur.contains("cedestinatairenestpasconnudsl")) {
                            JOptionPane.showMessageDialog(interfaceC,"Le destinataire que vous avez demandé pas n'existe ou plus.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // on s'est deconnecte du serveur
            connected=false;
            interfaceC.premiereEtape();
            JOptionPane.showMessageDialog(interfaceC,"Déconnecté");
        }
    }

    /**
     * Permet d'afficher la liste des utilisateurs connectes sur l'interface client.
     *
     * @throws IOException
     */
    private void afficherUtilisateurs() throws IOException {
        socOut.println("onveutlalistedesutilisateursconnectestp");
        LinkedList<String> utilisateursCo = new LinkedList<String>();
        String utilisateur = socIn.readLine();
        // Raccourci pour le client qui lui permettra de generer un message qui sera lu par tout le mondeS
        utilisateursCo.add("all");
        while(!utilisateur.contains("cestbonjetaienvoyetouslesutilisateurs")){
            utilisateursCo.add(utilisateur);
            utilisateur = socIn.readLine();
        }
        interfaceC.envoyerClientsCo(utilisateursCo.toArray(new String[utilisateursCo.size()]));
    }

    /**
     * Permet de deconnecter le client du serveur.
     */
    public void deconnecter() {
        if (connected) {
            envoyerServeur("QUIT");
            connected=false;
        }
        try {
            socOut.close();
            socIn.close();
            echoSocket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(interfaceC, "Erreur déconnexion : " + e);
        }
    }

    /**
     * Permet d'envoyer differentes informations au serveur qui va ensuite se charger de les redistribuer a tous les
     * clients connectes.
     *
     * @param commande String qui permet d'indiquer au serveur quelle action il doit realiser.
     */
    public void envoyerServeur(String commande){
        // La commande ne doit pas être vide !
        if(!commande.isEmpty()){
            String retour="";
            String userName="";

            // Requête de connexion au serveur
            if(commande.contains("CONNECT ") && !connected) {
                // Extraction du pseudo de l'utilisateur
                userName = commande.substring(8);

                // Interdiction de certains pseudos qui correspondent à des mots-cles dans notre application
                if (userName.equals("all") || userName.equals("CONNECT ") ||
                        userName.equals("SENDTO ") || userName.equals(" CONTENT ") ||
                        userName.equals("QUIT") || userName.equals("SIGNIN ") ||
                        userName.equals("SIGNOUT ") || userName.equals("MESSAGE FROM ") ||
                        userName.equals(" TO ")) {
                    JOptionPane.showMessageDialog(interfaceC, "Vous ne pouvez pas choisir cet username, il contient des mot interdits.");
                } else {
                    retour = "SIGNIN " + userName;
                    nomUtilisateur = userName;
                }
            }else if(connected && commande.contains("SENDTO ") && commande.contains(" CONTENT ")){    // Requête pour envoyer un message
                String destinataire = commande.substring(7,commande.indexOf(" CONTENT "));
                String message = commande.substring(commande.indexOf(" CONTENT ")+8);
                retour = "MESSAGE FROM " + nomUtilisateur + " TO " +destinataire +" CONTENT " + message;
            }else if(connected && commande.equals("QUIT")){   // Requête de deconnexion
                retour = "SIGNOUT "+nomUtilisateur;
            }
            // Envoyer l'information au serveur
            socOut.println(retour);
        }
    }
}


