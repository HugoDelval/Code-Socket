package stream;

/**
 * Created by Ophélie on 12/12/2014.
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.border.Border;

public class InterfaceServeur extends JFrame {

    private JFrame fenetre = new JFrame();
    private JPanel panelPrincipal = new JPanel();
    private JPanel sousPanelTexte = new JPanel();
    private JPanel sousPanelChamp = new JPanel();
    private JPanel sousPanelBouton = new JPanel();
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

        // MODIFICATION DES JPANEL dont on va se servir pour ajouter des boutons...etc...
        // Définition du panel principal
        panelPrincipal.setLayout(new GridLayout(3, 1));
        // Définition de la couleur des trois panels
        sousPanelTexte.setBackground(Color.black);
        sousPanelChamp.setBackground(Color.black);
        sousPanelBouton.setBackground(Color.black);
        // Définition du panel du bouton
        sousPanelBouton.setLayout(new FlowLayout());

        // CREATION DES BOUTONS
        // Création du boutton pour mettre en ligne ou non le serveur
        boutonServeur.setText("Lancement");
        boutonServeur.setPreferredSize(new Dimension(100, 50));
        // Dimensionner le bouton

        // PLACEMENT DES DIFFERENTS JPANEL
        // Le sousPanelTexte prend le texte

        // Le sousPanelChamp prend le champ

        // Le sousPanelBouton prend le bouton
        sousPanelBouton.add(boutonServeur);

        // Le panelPrincipal prend les trois JPanel
        panelPrincipal.add(sousPanelTexte);
        panelPrincipal.add(sousPanelChamp);
        panelPrincipal.add(sousPanelBouton);

        // La JFrame doit être liée à ce JPanel !
        fenetre.setContentPane(panelPrincipal);

        // RENDRE VISIBLE LA FENETRE
        fenetre.setVisible(true);
    }
}
