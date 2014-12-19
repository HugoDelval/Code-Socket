package stream;

import java.io.IOException;

/**
 * Created by Hugo on 12/12/2014.
 */
public class ChatClient {
    private EchoClient monClient;

    ChatClient(String adresseIP, String port)
    {
        //monClient=new EchoClient(adresseIP,port);
    }

    public void lancer(){
        /*try {
            monClient.lancerClient();
        } catch (IOException e) {
            System.out.println("Erreur client.");
            e.printStackTrace();
            System.exit(1);
        }*/
    }

    public static void main(String[] args){
        // premier argument = adresse ip serveur
        // deuxieme argument = port du serveur

        if(args.length != 2){
            System.out.println("Deux arguments en entrée : l'adresse ip du serveur suivit du numéro de port du serveur.");
            System.exit(1);
        }
        ChatClient chatClient =new ChatClient(args[0],args[1]);
        chatClient.lancer();
    }
}
