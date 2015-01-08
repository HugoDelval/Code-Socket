package stream;

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.BindException;
import javax.swing.border.Border;

/**
 * Description de l'interface serveur
 *
 * @authors B3424
 */
public class InterfaceServeur extends JFrame {

    private JPanel panelPrincipal = new JPanel();
    private JLabel labelPort = new JLabel("Port du Serveur :");
    private JButton button = new JButton("Lancer le serveur");
    private JTextField portServeur = new JTextField("9547",27);

    /**
     * Cree une relation entre le serveur et cet interface
     */
    private EchoServerMultiThreaded monServeur;
    /**
     * Booleen qui permet de connaitre l'etat de la connexion du serveur
     */
    private boolean connecte = false;

    /**
     * Construit une instance de InterfaceServeur, initialise les composants graphiques et les listeners
     */
    public InterfaceServeur() {
        // CREATION DE LA FENETRE
        // Ajouter un titre à la fenêtre
        setTitle("Serveur");
        // Dimensionner la fenêtre
        setSize(400, 130);
        // Impossible de redimensionner la fenêtre
        setResizable(false);
        //Positionner au centre de l'écran la fenêtre
        setLocationRelativeTo(null);
        // Termine le processus quand on quitte la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelPrincipal.setLayout(new FlowLayout());

        panelPrincipal.add(labelPort);
        panelPrincipal.add(portServeur);
        panelPrincipal.add(button);

        // La JFrame doit être liée à ce JPanel !
        setContentPane(panelPrincipal);

        // RENDRE VISIBLE LA FENETRE
        setVisible(true);

        // GESTION DES EVENEMENTS SUR LE BOUTON
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clicSurBouton();
            }
        });

        // GESTION DE L'EVENEMENT FERMER FENETRE
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(connecte)
                    monServeur.decoServeur();
            }
        });
    }

    /**
     * Permet de lancer ou d'arreter le serveur lorsqu'on clique sur le bouton associe.
     */
    private void clicSurBouton()
    {
        // Si le serveur est lancé
        if (connecte)
        {
            //Changer le texte du boutton et déconnecter le serveur
            button.setText("Lancer le serveur");
            connecte = false;
            monServeur.decoServeur();

            //Rendre accessible les champs
            portServeur.setEnabled(true);
        }
        // Si le serveur est arrêté
        else
        {
            // Vérifier que le port entré n'est pas vide
            String port = portServeur.getText();
            // Changer le texte du boutton et connecter le serveur
            if (!port.isEmpty())
            {
                monServeur = new EchoServerMultiThreaded(port);
                if (monServeur.erreurPort != null)
                {
                    JOptionPane.showMessageDialog(this, "Le port auquel vous tentez d'accéder est déjà occupé. \n" +
                                    "Veuillez en utiliser un autre !");
                }
                else
                {
                    button.setText("Déconnecter le serveur");
                    connecte = true;

//                    System.out.println("Numero de port : " + port);
                    monServeur.start();

                    //Rendre inaccessible les champs
                    portServeur.setEnabled(false);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Aucun port renseigné !");
            }
        }
    }
}
