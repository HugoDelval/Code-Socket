package stream;

/**
 * Created by Ophélie on 12/12/2014.
 */

// Pour créer des fenêtres
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;

public class InterfaceServeur extends JFrame {

    public InterfaceServeur () {
        /**
         *  Constructeur :
         *  Création d'une fenêtre pour lancer ou arrêter le serveur
         **/
        // Définition des variables
        String titreFenetre = new String("Serveur");

        // Création de la fenêtre
        JFrame fenetre = new JFrame();

        // Ajouter un titre à la fenêtre
        fenetre.setTitle(titreFenetre);
        // Dimensionner la fenêtre
        fenetre.setSize(500, 400);
        //Positionner au centre la fenêtre
        fenetre.setLocationRelativeTo(null);
        // Termine le processus quand on quitte la fenêtre
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Création d'un JPanel dont on va se servir pour ajouter des boutons...etc...
        JPanel panelPrincipal = new JPanel();

        // Définition de sa couleur
        panelPrincipal.setBackground(Color.black);

        // La JFrame doit être liée à ce JPanel !
        fenetre.setContentPane(panelPrincipal);

        // Rendre visible la fenêtre
        fenetre.setVisible(true);
    }
}
