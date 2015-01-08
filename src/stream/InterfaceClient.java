package stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.text.DefaultCaret;

/**
 * Interface Graphique de la classe Client
 *
 * @author B3424
 */
public class InterfaceClient extends JFrame {

    private JList<String> utilisateursCo = new JList<String>();
    JScrollPane scrollUtilisateursCo = new JScrollPane(utilisateursCo);
    private JPanel histoPlusUtilisateurs = new JPanel();
    private JPanel panelPrincipal = new JPanel();
    private JTextField message = new JTextField("",41);
    private JTextArea historiqueMessages = new JTextArea(18, 30);
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

    /**
     * boolean permettant de savoir si le client est connu par le serveur (connecte en termes de socket)
     */
    private boolean connecte=false;
    /**
     * L'objet représentant le Client avec lequel l'interface interagit
     */
    private EchoClient leClient;

    /**
     * constructeur de l'interface, initialise les composants graphiques et les evenements
     */
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
        //panelPrincipal.add(scrollHistorique,BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret)historiqueMessages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        utilisateursCo.setFixedCellWidth(115);
        utilisateursCo.setVisibleRowCount(18);
        utilisateursCo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajout du panel qui va contenir histo + utilisateurs
        histoPlusUtilisateurs.add(scrollUtilisateursCo, BorderLayout.WEST);
        histoPlusUtilisateurs.add(scrollHistorique, BorderLayout.EAST);
        panelPrincipal.add(histoPlusUtilisateurs);

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
                //if(connecte)
                  //  leClient.deconnecter();
            }
        });
        utilisateursCo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if( (evt.getClickCount() == 2 || evt.getClickCount() == 3) && list.isEnabled() ) {
                    String user = (String)list.getSelectedValue();
                    message.setText("SENDTO "+user+" CONTENT message");

                }
            }
        });

        setContentPane(panelPrincipal);
        premiereEtape();
        setVisible(true);
    }

    /**
     * Pre-complete la zone de saisie avec la commande sendto
     */
    private void buttonCmdSendCliked() {
        message.setText("SENDTO [all | username] CONTENT message");
        message.requestFocus();
    }

    /**
     * Pre-complete la zone de saisie avec la commande connect
     */
    private void buttonCmdConnectCliked() {
        message.setText("CONNECT username");
        message.requestFocus();
    }

    /**
     * Pre-complete la zone de saisie avec la commande disconnect
     */
    private void buttonCmdDisconnectCliked() {
        message.setText("QUIT");
        message.requestFocus();
    }

    /**
     * Connecte ou deconnecte le client du serveur (en terme de socket)
     * change les composants graphiques en fonction de si le client est connecte/deconnecte
     */
    private void buttonConnectCliked(){
        // Bouton Connecter/Deconnecter a été cliqué
        if(connecte){
            leClient.deconnecter();
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

    /**
     * composants graphiques tels qu'ils sont avant la connexion au serveur (connexion en terme de socket)
     */
    public void premiereEtape() {
        connecte=false;
        buttonConnect.setText("Connexion Serveur");
        historiqueMessages.setText("");
        utilisateursCo.setListData(new String[0]);
        utilisateursCo.setEnabled(false);
        addresseServeur.setEnabled(true);
        portServeur.setEnabled(true);
        buttonCmdDisconnect.setEnabled(false);
        buttonEnvoi.setEnabled(false);
        buttonCmdSend.setEnabled(false);
        buttonCmdConnect.setEnabled(false);
        message.setEnabled(false);
    }

    /**
     * composants graphiques tels qu'ils sont pendant connexion au serveur (connexion en terme de socket)
     */
    public void secondeEtape() {
        connecte = true;
        buttonConnect.setText("Deconnexion");
        utilisateursCo.setEnabled(true);
        addresseServeur.setEnabled(false);
        portServeur.setEnabled(false);
        buttonCmdDisconnect.setEnabled(true);
        buttonEnvoi.setEnabled(true);
        buttonCmdSend.setEnabled(true);
        buttonCmdConnect.setEnabled(true);
        message.setEnabled(true);
    }

    /**
     * Envoi au serveur la commande tape dans la zone de saisie en verifiant que vous pouvez bien l'envoyer
     */
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

    /**
     * Complete la zone d'historique des messages avec la chaine 'info'
     * @param info , La chaine de caractere a ajouter dans la zone des anciens messages
     */
    public void envoyerInfo(String info){
        historiqueMessages.setText(historiqueMessages.getText()+info);
    }

    /**
     * Permet d'afficher les Clients connectes
     * @param info , Le tableau de String correspondant aux clients connectes qui apparaitront dans la zone de la jList 'utilisateursCo'
     */
    public void envoyerClientsCo (String[] info) {
        utilisateursCo.setListData(info);

    }

}
