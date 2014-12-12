package stream;

/**
 * Created by Hugo on 12/12/2014.
 */
public class ChatServeur {
    private EchoServerMultiThreaded monServeur;

    ChatServeur(EchoServerMultiThreaded serv)
    {
        monServeur=serv;
    }

    public void lancer(){
        monServeur.lancerServeur();
    }

    public static void main(String[] args){
        // premier argument = port du serveur
        if(args.length != 1){
            System.out.println("Un argument en entrée : le numéro de port du serveur.");
            System.exit(1);
        }
        ChatServeur chatServeur =new ChatServeur(new EchoServerMultiThreaded(args[0]));
        chatServeur.lancer();
    }
}
