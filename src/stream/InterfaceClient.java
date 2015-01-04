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
import java.io.IOException;
import javax.swing.text.DefaultCaret;

public class InterfaceClient extends JFrame {

    private JPanel panelPrincipal = new JPanel();
    private JTextField message = new JTextField("",41);
    private JTextArea historiqueMessages = new JTextArea(18, 40);
    JScrollPane scrollHistorique = new JScrollPane(historiqueMessages);
    private JButton buttonConnect = new JButton("Connexion Serveur");
    private JTextField addresseServeur = new JTextField("127.0.0.1",27);
    private JLabel labelAdresse = new JLabel("Adresse IP Serveur :          ");
    private JTextField portServeur = new JTextField("9547",27);
    private JLabel labelPort = new JLabel("Port d'écoute du Serveur :");
    private JLabel labelMessage = new JLabel("Votre Commande :");
    private JButton buttonEnvoi = new JButton("Envoyer");
    private JButton buttonCmdConnect = new JButton("cmd CONNECT");
    private JButton buttonCmdSend = new JButton("cmd SEND");
    private JButton buttonCmdDisconnect = new JButton("cmd DISCONNECT");


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
        setSize(500, 530);
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
        panelPrincipal.add(buttonConnect);

        historiqueMessages.setEditable(false);
        scrollHistorique.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelPrincipal.add(scrollHistorique,BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret)historiqueMessages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        panelPrincipal.add(labelMessage);
        panelPrincipal.add(message);
        panelPrincipal.add(buttonCmdConnect);
        panelPrincipal.add(buttonCmdSend);
        panelPrincipal.add(buttonCmdDisconnect);
        panelPrincipal.add(buttonEnvoi);


        /* events */
        buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonConnectCliked();
            }
        });
        buttonCmdConnect.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCmdConnectCliked();
            }
        });
        buttonCmdSend.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCmdSendCliked();
            }
        });
        buttonCmdDisconnect.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCmdDisconnectCliked();
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
        premiereEtape();
        setVisible(true);
    }

    private void buttonCmdSendCliked() {
        message.setText("SENDTO [all | username] CONTENT message");
        message.requestFocus();
    }

    private void buttonCmdConnectCliked() {
        message.setText("CONNECT username");
        message.requestFocus();
    }

    private void buttonCmdDisconnectCliked() {
        message.setText("QUIT");
        message.requestFocus();
    }

    private void buttonConnectCliked(){
        // Bouton Connecter/Deconnecter a été cliqué
        if(connecte){
            leClient.deconnecter();
            connecte=false;
            premiereEtape();
        }else {
            String ip = addresseServeur.getText();
            String port = portServeur.getText();
            if(!ip.isEmpty() && !port.isEmpty()){
                try {
                    leClient=new EchoClient(ip,port,this);
                    leClient.start();
                    historiqueMessages.setText("");
                    secondeEtape();
                    connecte = true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de se connecter au Serveur demandé.",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this,
                        "Les champs IP Serveur et Port Serveur doivent être remplis.",
                        "Inane error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void premiereEtape() {
        buttonConnect.setText("Connexion Serveur");
        addresseServeur.setEnabled(true);
        portServeur.setEnabled(true);
        buttonCmdDisconnect.setEnabled(false);
        buttonEnvoi.setEnabled(false);
        buttonCmdSend.setEnabled(false);
        buttonCmdConnect.setEnabled(false);
        message.setEnabled(false);
    }

    private void secondeEtape() {
        buttonConnect.setText("Deconnexion");
        addresseServeur.setEnabled(false);
        portServeur.setEnabled(false);
        buttonCmdDisconnect.setEnabled(true);
        buttonEnvoi.setEnabled(true);
        buttonCmdSend.setEnabled(true);
        buttonCmdConnect.setEnabled(true);
        message.setEnabled(true);
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
