package stream;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * Client TCP
 *
 * @authors B3424
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
     * @see private void afficherUtilisateurs()
     */
    private String nomUtilisateur="";

    /**
     * Permet de savoir si le client s'est connecte au serveur.
     */
    private boolean connected=false;

    /**
     * Permet de savoir si l'historique s'affiche a un moment donne a l'ecran.
     *
     * @see public void run()
     */
    private boolean historiqueEnCours = false;

    /**
     * Construit un objet EchoClient.
     *
     * @param adresseIP , L'adresse IP du serveur auquel le client souhaite se connecter
     * @param port , Le port sur lequel le serveur est lance
     * @param interC , L'interface client auquel est lie cet objet
     * @throws IOException
     */
    EchoClient(String adresseIP, String port, InterfaceClient interC) throws IOException {
        // Création d'une connexion entre le client et le serveur : précision d'une adresse et d'un port
        echoSocket = new Socket(adresseIP,new Integer(port).intValue());
        // Création d'un buffer qui va stocker ce qu'on recoie du serveur
        socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        // Création d'un buffer qui va stocker ce qu'on veut envoyer au serveur
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
                // Récupération de la commande de l'utilisateur envoye par le serveur
                String commandeUtilisateur = socIn.readLine();

                // Traiter les différentes commandes reçues par le serveur (de la part de ClientThread car canal
                // de communication unique entre le client et le serveur)
                if(interfaceC != null && !commandeUtilisateur.isEmpty()) {

                    // Mot-clé de connexion : SIGNIN
                    if (commandeUtilisateur.contains("SIGNIN ")) {
                        // Extraction du pseudo de l'utilisateur qui souhaite se connecter
                        String userSigning = commandeUtilisateur.substring(7);

                        // Affichage de l'historique de la conversation dans un premier temps
                        if(userSigning.equals(nomUtilisateur) && !connected && !historiqueEnCours) {
                            interfaceC.envoyerInfo("------- Debut de votre session -------\r\n");
                            connected=true;
                            historiqueEnCours=true;
                            afficherUtilisateurs();
                            socOut.println("ilveutlhistoriquealorsenvoielui");
                        }

                        // Faire savoir à l'utilisateur qu'il est bien connecté
                        if(connected) {
                            if(userSigning.equals(nomUtilisateur)) {
                                userSigning = "You've";
                            }
                            // Faire savoir aux autres membres du chat que le client vient de se connecter au chat
                            interfaceC.envoyerInfo( "server > all : " + userSigning + " signed in.\r\n");
                        }

                        // Permet qu client de connaître la liste des utilisateurs connectés
                        if(connected && !historiqueEnCours){
                            afficherUtilisateurs();
                        }

                    }

                    // Si le nom d'utilisateur est déjà pris dans le chat, le client ne peut pas prendre ce nom et il
                    // doit en choisir un autre
                    else if(commandeUtilisateur.contains("nomimpossibleaattribuerparcequilestdejapris")) {
                        JOptionPane.showMessageDialog(interfaceC,"Vous ne pouvez pas choisir cet username, il est déjà pris.");
                    }

                    // Action uniquement réalisées si le client est connecté
                    else if(connected) {

                        // Mot-clé de déconnexion : SIGNOUT
                        if (commandeUtilisateur.contains("SIGNOUT ")) {
                            // Récupération du nom du client qui se déconnecte
                            String userSignout = commandeUtilisateur.substring(8);
                            String user=userSignout;

                            // Si le client qui se déconnecte est l'utilisateur lui-même
                            if (userSignout.equals(nomUtilisateur)) {
                                user = "You've";
                            }
                            // Faire savoir aux autres membres du chat que l'utilisateur s'est déconnecté
                            interfaceC.envoyerInfo("server > all : " + user + " signed out.\r\n");

                            // Informer l'utilisateur que c'est la fin de sa session (il n'est plus connecté)
                            if (userSignout.equals(nomUtilisateur) && !historiqueEnCours) {
                                interfaceC.envoyerInfo("--------- Fin de votre session -------\r\n");
                                connected = false;
                            }

                            // Affichage des utilisateurs connectés
                            if(!historiqueEnCours){
                                afficherUtilisateurs();
                            }
                        }

                        // Si les mots-clés désignent un message envoyé à une personne en particulier : MESSAGE FROM ... TO ... CONTENT ...
                        else if (commandeUtilisateur.contains("MESSAGE FROM ") && commandeUtilisateur.contains(" TO ") && commandeUtilisateur.contains(" CONTENT ")) {
                            // Récupération des différentes chaînes de caractères intéressantes (expéditeur, destinataire et message)
                            String expediteur = commandeUtilisateur.substring(commandeUtilisateur.indexOf("MESSAGE FROM ") + 13, commandeUtilisateur.indexOf(" TO "));
                            String destinataire = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" TO ") + 4, commandeUtilisateur.indexOf(" CONTENT "));
                            String msg = commandeUtilisateur.substring(commandeUtilisateur.indexOf(" CONTENT ") + 8);

                            // Si le message est envoyé de la part de l'utilisateur lui-même
                            if (expediteur.equals(nomUtilisateur))
                                expediteur = "me";

                            // Si le message est destiné à l'utilisateur lui-même
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
                        // connectés !)
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
     * Permet d'afficher la liste des utilisateurs connectés sur l'interface client.
     *
     * @throws IOException
     */
    private void afficherUtilisateurs() throws IOException {
        socOut.println("onveutlalistedesutilisateursconnectestp");
        LinkedList<String> utilisateursCo = new LinkedList<String>();
        String utilisateur = socIn.readLine();
        // Raccourci pour le client qui lui permettra de générer un message qui sera lu par tout le mondeS
        utilisateursCo.add("all");
        while(!utilisateur.contains("cestbonjetaienvoyetouslesutilisateurs")){
            utilisateursCo.add(utilisateur);
            utilisateur = socIn.readLine();
        }
        interfaceC.envoyerClientsCo(utilisateursCo.toArray(new String[utilisateursCo.size()]));
    }

    /**
     * Permet de déconnecter le client du serveur.
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
            JOptionPane.showMessageDialog(interfaceC, "Erreur deconnexion : " + e);
        }
    }

    /**
     * Permet d'envoyer differentes informations au serveur qui va ensuite se charger de les redistribuer a tous les
     * clients connectes.
     *
     * @param commande , String qui permet d'indiquer au serveur quelle action il doit realiser.
     */
    public void envoyerServeur(String commande){
        // La commande ne doit pas être vide !
        if(!commande.isEmpty()){
            String retour="";
            String userName="";

            // Requête de connexion au serveur
            if(commande.contains("CONNECT ") && !connected){
                // Extraction du pseudo de l'utilisateur
                userName = commande.substring(8);

                // Interdiction de certains pseudos qui correspondent à des mots-clés dans notre application
                if(userName.equals("all") || userName.equals("CONNECT ") ||
                        userName.equals("SENDTO ")|| userName.equals(" CONTENT ") ||
                        userName.equals("QUIT")|| userName.equals("SIGNIN ") ||
                        userName.equals("SIGNOUT ")|| userName.equals("MESSAGE FROM ") ||
                        userName.equals(" TO ")){
                    JOptionPane.showMessageDialog(interfaceC,"Vous ne pouvez pas choisir cet username, il contient des mot interdits.");
                }

                else {
                    retour = "SIGNIN "+userName;
                    nomUtilisateur = userName;
                }
            }

            // Requête pour envoyer un message
            if(connected && commande.contains("SENDTO ") && commande.contains(" CONTENT ")){
                String destinataire = commande.substring(7,commande.indexOf(" CONTENT "));
                String message = commande.substring(commande.indexOf(" CONTENT ")+8);
                retour = "MESSAGE FROM " + nomUtilisateur + " TO " +destinataire +" CONTENT " + message;
            }

            // Requête de déconnexion
            if(connected && commande.equals("QUIT")){
                retour = "SIGNOUT "+nomUtilisateur;
            }

            // Envoyer l'information au serveur
            socOut.println(retour);
        }
    }
}


