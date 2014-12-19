package stream;

/**
 * Created by Ophélie on 12/12/2014.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.text.DefaultCaret;

public class InterfaceClient extends JFrame {

    private JPanel panelPrincipal = new JPanel();
    private JTextField message = new JTextField("",41);
    private JTextArea historiqueMessages = new JTextArea(18, 40);
    JScrollPane scrollHistorique = new JScrollPane(historiqueMessages);
    private JButton button = new JButton("Connexion");
    private JTextField addresseServeur = new JTextField("127.0.0.1",27);
    private JLabel labelAdresse = new JLabel("Adresse IP Serveur :          ");
    private JTextField portServeur = new JTextField("9547",27);
    private JLabel labelPort = new JLabel("Port d'écoute du Serveur :");
    private JButton buttonEnvoi = new JButton("Envoyer");


    private boolean connecte=false;
    private EchoClient leClient;

    public InterfaceClient () {
        /**
         *  Constructeur :
         *  Création d'une fenêtre pour lancer ou arrêter le serveur
         **/

        // CREATION DE LA FENETRE
        // Ajouter un titre à la fenêtre
        setTitle("Client");
        // Dimensionner la fenêtre
        setSize(500, 480);
        setResizable(false);
        //Positionner au centre la fenêtre
        setLocationRelativeTo(null);
        // Termine le processus quand on quitte la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelPrincipal.setLayout(new FlowLayout());

        panelPrincipal.add(labelAdresse);
        panelPrincipal.add(addresseServeur);
        panelPrincipal.add(labelPort);
        panelPrincipal.add(portServeur);
        panelPrincipal.add(button);

        historiqueMessages.setEditable(false);
        scrollHistorique.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelPrincipal.add(scrollHistorique,BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret)historiqueMessages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        panelPrincipal.add(message);
        panelPrincipal.add(buttonEnvoi);


        /* events */
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCliked();
            }
        });
        buttonEnvoi.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonEnvoiCliked();
            }
        });
        message.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonEnvoiCliked();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(connecte)
                    leClient.deconnecter();
            }
        });


        setContentPane(panelPrincipal);

        setVisible(true);
    }

    private void buttonCliked(){
        // Bouton Connecter/Deconnecter a été cliqué
        if(connecte){
            button.setText("Connexion");
            leClient.deconnecter();
            connecte=false;
        }else {
            String ip = addresseServeur.getText();
            String port = portServeur.getText();
            if(!ip.isEmpty() && !port.isEmpty()){
                historiqueMessages.setText("");
                leClient=new EchoClient(ip,port,this);
                leClient.start();
                connecte=true;
                button.setText("Deconnexion");
            }else{
                JOptionPane.showMessageDialog(this,
                        "Les champs IP Serveur et Port Serveur doivent être remplis.",
                        "Inane error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buttonEnvoiCliked(){
        // Bouton envoye a été cliqué
        if(connecte){
            leClient.envoyerServeur(message.getText());
            message.setText("");
        }else{
            JOptionPane.showMessageDialog(this,
                    "Vous devez vous connecter pour envoyer un  message.");
        }
    }

    public void envoyerInfo(String info){
        historiqueMessages.setText(historiqueMessages.getText()+info);
    }
}
