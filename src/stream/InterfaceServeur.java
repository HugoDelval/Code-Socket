package stream;

/**
 * Created by Ophélie on 12/12/2014.
 */

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.BindException;
import javax.swing.border.Border;

public class InterfaceServeur extends JFrame {

    private JPanel panelPrincipal = new JPanel();
    private JLabel labelPort = new JLabel("Port du Serveur :");
    private JButton button = new JButton("Lancer le serveur");
    private JTextField portServeur = new JTextField("9547",27);

    private EchoServerMultiThreaded monServeur;
    private boolean connecte = false;

    public InterfaceServeur() {
//        /**
//         *  Constructeur :
//         *  Création d'une fenêtre pour lancer ou arrêter le serveur
//         **/

        // CREATION DE LA FENETRE
        // Ajouter un titre à la fenêtre
        setTitle("Serveur");
        // Dimensionner la fenêtre
        setSize(400, 130);
        setResizable(false);
        //Positionner au centre la fenêtre
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(connecte)
                    monServeur.decoServeur();
            }
        });
    }

    // Méthode clicSurBouton
    private void clicSurBouton()
    {
        if (connecte)
        {
            //Changer le texte du boutton et déconnecter le serveur
            button.setText("Lancer le serveur");
            connecte = false;
            monServeur.decoServeur();

            //Rendre accessible les champs
            portServeur.setEnabled(true);
        }
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
                    JOptionPane.showMessageDialog(this,
                            "Le port auquel vous tentez d'accéder est déjà occupé. \n" +
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
                JOptionPane.showMessageDialog(this,
                        "Aucun port renseigné !");
            }
        }
    }
}
