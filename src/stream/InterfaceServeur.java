package stream;

/**
 * Created by Ophélie on 12/12/2014.
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.border.Border;
import java.awt.BorderLayout;

public class InterfaceServeur extends JFrame {

    private JFrame fenetre = new JFrame();
    private JPanel panelPrincipal = new JPanel();
    private JButton boutonServeur = new JButton();

    public InterfaceServeur () {
        /**
         *  Constructeur :
         *  Création d'une fenêtre pour lancer ou arrêter le serveur
         **/
        // Définition des variables
        String titreFenetre = new String("Serveur");

        // CREATION DE LA FENETRE
        // Ajouter un titre à la fenêtre
        fenetre.setTitle(titreFenetre);
        // Dimensionner la fenêtre
        fenetre.setSize(500, 400);
        //Positionner au centre la fenêtre
        fenetre.setLocationRelativeTo(null);
        // Termine le processus quand on quitte la fenêtre
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //CREATION DU JPANEL dont on va se servir pour ajouter des boutons...etc...
        // Définition de sa couleur
        panelPrincipal.setBackground(Color.black);

        // La JFrame doit être liée à ce JPanel !
        fenetre.setContentPane(panelPrincipal);

        // CREATION DES BOUTONS
        // Création du boutton pour mettre en ligne ou non le serveur
        boutonServeur.setText("Lancement");
        // Définition du layout à utiliser
        fenetre.setLayout(new BorderLayout());
        // Placement du bouton sur le panel principal
        fenetre.getContentPane().add(boutonServeur, BorderLayout.SOUTH);

        // RENDRE VISIBLE LA FENETRE
        fenetre.setVisible(true);
    }
}
