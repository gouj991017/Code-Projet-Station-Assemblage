package terminal_base;
import com.phidget22.*; //Librairies pour la communication avec le kit Phidget.
import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;
import java.util.*;
import java.io.*;
import javax.swing.JFileChooser;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * @author Guillaume Beaudoin
 * @brief Il s'agit d'une version de code adaptable selon le fichier d'instruction ouvert. Le contenu des étapes (nombre de pièce à prendre,
    bac où aller chercher la pièce, nom de la pièce et message affiché) n'est pas déterminé par ce programme-ci.
    Contient le code nécessaire à la création et à la gestion de l'interface usager de la base. Ceci comprend une fenêtre principale
    rapportant les messages d'étapes et le menu d'actions et quelques fenêtres secondaires présentant individuellement l'image de
    l'étape, l'état des bacs et le nombre de pièces dans les bacs.
 * @version 1.0     Editor: Apache NetBeans IDE 11.3    System: dev: Windows 10 Familly  
 */
public class UI_Base extends javax.swing.JFrame
{
    //Constantes.
    static final int NB_BACS_INTERFACE = 10; //Nombre de bacs portrayé dans l'interface usager.
    static final String EXT_IMAGE = ".png";
    
    //Variables globales.
    static List<Etape> l_Etape = new ArrayList<Etape>();
    static List<Piece> l_Piece = new ArrayList<Piece>();
    static List<Box> l_Box = new ArrayList<Box>();  //Liste d'objets de bacs.
    static List<javax.swing.ImageIcon> l_images = new ArrayList<javax.swing.ImageIcon>();  //Liste d'images.
    static Panel[] t_panelInterne = new Panel[NB_BACS_INTERFACE];   //Tableau des panels internes pour l'état des bacs.
    static Panel[] t_panelExterne = new Panel[NB_BACS_INTERFACE];   //Tableau des panels externes pour l'état des bacs.
    static TextField[] t_tfPoid = new TextField[NB_BACS_INTERFACE]; //Tableau de textfields affichant le poid des bacs.
    static int nb_Bacs;
    static int nb_etapes = 1;
    static int etape_courante = 0;
    static int bacActif = 0;
    static BlockingConnection connection;
    static final UTF8Buffer TOPIC_CASQUE = new UTF8Buffer("/com/station_reponse"); //Topic de communication vers le casque.
    static final UTF8Buffer TOPIC_COMMANDE = new UTF8Buffer("/com/station_requete_message"); //Topic de récéption de la commande.
    static MQTT mqtt = new MQTT(); //Objet MQTT pour la communication.
    static double weightConvertionRatio = 0.0203; //Valeur par défaut: 0.0203v/v*10e-5 / g.
    static int nb_pieces_temp;
    static boolean erreur = false;  //Si il y a erreur, cette variable sera fausse.
    
    /**
     * Creates new form UI_Base
     */
    public UI_Base()
    {
        String host = env("ACTIVEMQ_HOST", "127.0.0.1");    //10.240.9.22
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "1883"));
        
        initComponents();
        //Association des objets selon leur tableaux.
        //Panels externes:
        t_panelExterne[0] = panelBorder1;
        t_panelExterne[1] = panelBorder2;
        t_panelExterne[2] = panelBorder3;
        t_panelExterne[3] = panelBorder4;
        t_panelExterne[4] = panelBorder5;
        t_panelExterne[5] = panelBorder6;
        t_panelExterne[6] = panelBorder7;
        t_panelExterne[7] = panelBorder8;
        t_panelExterne[8] = panelBorder9;
        t_panelExterne[9] = panelBorder10;
        //Panels Internes:
        t_panelInterne[0] = panelCenter1;
        t_panelInterne[1] = panelCenter2;
        t_panelInterne[2] = panelCenter3;
        t_panelInterne[3] = panelCenter4;
        t_panelInterne[4] = panelCenter5;
        t_panelInterne[5] = panelCenter6;
        t_panelInterne[6] = panelCenter7;
        t_panelInterne[7] = panelCenter8;
        t_panelInterne[8] = panelCenter9;
        t_panelInterne[9] = panelCenter10;
        //Text fields (poid):
        t_tfPoid[0] = tbPoids_Bac1;
        t_tfPoid[1] = tbPoids_Bac2;
        t_tfPoid[2] = tbPoids_Bac3;
        t_tfPoid[3] = tbPoids_Bac4;
        t_tfPoid[4] = tbPoids_Bac5;
        t_tfPoid[5] = tbPoids_Bac6;
        t_tfPoid[6] = tbPoids_Bac7;
        t_tfPoid[7] = tbPoids_Bac8;
        t_tfPoid[8] = tbPoids_Bac9;
        t_tfPoid[9] = tbPoids_Bac10;    
        
        try
        {
            mqtt.setHost(host, port);
        }
        catch(Exception ex)
        {
            System.out.println("Impossible d'établir la connection");
        }
        connection = mqtt.blockingConnection();
        //Setup dynamique de la taille du panneau d'image et de la fenêtre.
        UI_Base.t_image.setSize(UI_Base.t_image.getPreferredSize());
        
    }
    /*
        Méthode d'intérruption (event) des capteurs IR.
    */
    public static VoltageInputVoltageChangeListener iR_Change =
        new VoltageInputVoltageChangeListener() {
        @Override
        public void onVoltageChange(VoltageInputVoltageChangeEvent e){
            try
            {
                if(etape_courante != 0) //La logique d'étape n'est active que si l'étape vaut plus que 0.
                {
                    int bacCourant = l_Piece.get(l_Etape.get(etape_courante-1).num_piece).n_bac;   //Récupération du bac courant.
                    if(e.getVoltage() > l_Box.get(0).TRIGGER_IR)  //Si niveau haut...
                    {
                        nb_pieces_temp = 0; //Remise à zero.
                        bacActif = 0;   //Remise à zero.
                        while(!l_Box.get(bacActif).update_IR()) {bacActif++;}  //Recherche de la boîte concernée.
                        t_panelExterne[bacActif].setBackground(Color.yellow);//Allumer le bac concerné en jaune.
                        if(checkErreurBac(bacCourant))    //Vérification bon bac.
                        {
                            erreur = true;  //Bon bac.
                            try
                            {   //Ligne plus suceptible de causer des exeptions (durant le développement).
                                nb_pieces_temp = l_Box.get(bacActif).getItemCount(weightConvertionRatio);  //Sauvegarde le nombre de pièces pour une comparaison ultérieure.
                            }catch (Exception ex){System.out.println("[Error] Impossible de récupérer le nombre d'items: "+ ex.getMessage());}
                        }
                    }
                    else    //Si niveau bas...
                    {
                        if(checkErreurPoid(bacCourant) && erreur) //Vérification du bon nombre de pièces pigés et si une erreur a d'abord été commise.
                        {
                            //Bonne étape, prochine étape + message.
                            etape_courante++;
                            resetBac(); //Remise des panels internes à défault (blanc).
                            logiqueEtape(true);
                        }
                        t_panelExterne[bacActif].setBackground(Color.white);    //Dé-jauner le bac.
                        erreur = false; //Remise à défaut.
                    }
                }
            }catch(Exception ex){}
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        d_calib = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        d_etape0 = new javax.swing.JDialog();
        b_etape0_ok = new javax.swing.JButton();
        b_etape0_cancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        t_bacs = new javax.swing.JFrame();
        panel15 = new java.awt.Panel();
        label32 = new java.awt.Label();
        panelBorder1 = new java.awt.Panel();
        panelCenter1 = new java.awt.Panel();
        label33 = new java.awt.Label();
        panelBorder2 = new java.awt.Panel();
        panelCenter2 = new java.awt.Panel();
        label34 = new java.awt.Label();
        panelBorder3 = new java.awt.Panel();
        panelCenter3 = new java.awt.Panel();
        label35 = new java.awt.Label();
        panelBorder4 = new java.awt.Panel();
        panelCenter4 = new java.awt.Panel();
        label36 = new java.awt.Label();
        panelBorder5 = new java.awt.Panel();
        panelCenter5 = new java.awt.Panel();
        label37 = new java.awt.Label();
        panelBorder6 = new java.awt.Panel();
        panelCenter6 = new java.awt.Panel();
        label38 = new java.awt.Label();
        panelBorder7 = new java.awt.Panel();
        panelCenter7 = new java.awt.Panel();
        label39 = new java.awt.Label();
        panelBorder8 = new java.awt.Panel();
        panelCenter8 = new java.awt.Panel();
        label40 = new java.awt.Label();
        panelBorder9 = new java.awt.Panel();
        panelCenter9 = new java.awt.Panel();
        label41 = new java.awt.Label();
        panelBorder10 = new java.awt.Panel();
        panelCenter10 = new java.awt.Panel();
        label42 = new java.awt.Label();
        t_inv = new javax.swing.JFrame();
        panel17 = new java.awt.Panel();
        textField13 = new java.awt.TextField();
        label50 = new java.awt.Label();
        tbPoids_Bac6 = new java.awt.TextField();
        panel18 = new java.awt.Panel();
        textField14 = new java.awt.TextField();
        label22 = new java.awt.Label();
        tbPoids_Bac1 = new java.awt.TextField();
        panel19 = new java.awt.Panel();
        textField15 = new java.awt.TextField();
        label51 = new java.awt.Label();
        tbPoids_Bac5 = new java.awt.TextField();
        panel20 = new java.awt.Panel();
        textField16 = new java.awt.TextField();
        label52 = new java.awt.Label();
        tbPoids_Bac7 = new java.awt.TextField();
        panel21 = new java.awt.Panel();
        textField17 = new java.awt.TextField();
        tbPoids_Bac8 = new java.awt.TextField();
        label53 = new java.awt.Label();
        panel22 = new java.awt.Panel();
        textField18 = new java.awt.TextField();
        label54 = new java.awt.Label();
        tbPoids_Bac3 = new java.awt.TextField();
        panel23 = new java.awt.Panel();
        textField19 = new java.awt.TextField();
        label55 = new java.awt.Label();
        tbPoids_Bac10 = new java.awt.TextField();
        panel24 = new java.awt.Panel();
        textField20 = new java.awt.TextField();
        label56 = new java.awt.Label();
        tbPoids_Bac4 = new java.awt.TextField();
        panel25 = new java.awt.Panel();
        textField21 = new java.awt.TextField();
        label57 = new java.awt.Label();
        tbPoids_Bac9 = new java.awt.TextField();
        panel26 = new java.awt.Panel();
        textField22 = new java.awt.TextField();
        label20 = new java.awt.Label();
        tbPoids_Bac2 = new java.awt.TextField();
        jLabel3 = new javax.swing.JLabel();
        t_image = new javax.swing.JFrame();
        l_image = new javax.swing.JLabel();
        jOuvrir_Fichier = new javax.swing.JFileChooser();
        panel5 = new java.awt.Panel();
        label17 = new java.awt.Label();
        tbEtapes = new java.awt.TextField();
        button_Next = new java.awt.Button();
        button_Previous = new java.awt.Button();
        panel3 = new java.awt.Panel();
        label24 = new java.awt.Label();
        tb_affiche = new java.awt.List();
        label9 = new java.awt.Label();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        b_quitter = new javax.swing.JMenuItem();
        b_ouvrir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        b_calibre = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        b_etatBacs = new javax.swing.JMenuItem();
        b_Inv = new javax.swing.JMenuItem();
        b_image = new javax.swing.JMenuItem();

        d_calib.setTitle("Info");
        d_calib.setMinimumSize(new java.awt.Dimension(500, 167));

        jButton1.setText("ok");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("Veuillez retirer toutes les pièces des bacs avant de calibrer, puis appuyez sur 'ok'.");

        javax.swing.GroupLayout d_calibLayout = new javax.swing.GroupLayout(d_calib.getContentPane());
        d_calib.getContentPane().setLayout(d_calibLayout);
        d_calibLayout.setHorizontalGroup(
            d_calibLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d_calibLayout.createSequentialGroup()
                .addGroup(d_calibLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(d_calibLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(d_calibLayout.createSequentialGroup()
                        .addGap(169, 169, 169)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        d_calibLayout.setVerticalGroup(
            d_calibLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, d_calibLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(d_calibLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(34, 34, 34))
        );

        d_etape0.setMinimumSize(new java.awt.Dimension(500, 167));

        b_etape0_ok.setText("ok");
        b_etape0_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_etape0_okActionPerformed(evt);
            }
        });

        b_etape0_cancel.setText("cancel");

        jLabel2.setText("Veuillez mettre les pièces dans leur bac respectifs puis appuyer sur 'ok'.");

        javax.swing.GroupLayout d_etape0Layout = new javax.swing.GroupLayout(d_etape0.getContentPane());
        d_etape0.getContentPane().setLayout(d_etape0Layout);
        d_etape0Layout.setHorizontalGroup(
            d_etape0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d_etape0Layout.createSequentialGroup()
                .addGroup(d_etape0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(d_etape0Layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(b_etape0_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(b_etape0_cancel))
                    .addGroup(d_etape0Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel2)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        d_etape0Layout.setVerticalGroup(
            d_etape0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, d_etape0Layout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(39, 39, 39)
                .addGroup(d_etape0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_etape0_ok)
                    .addComponent(b_etape0_cancel))
                .addGap(35, 35, 35))
        );

        t_bacs.setMinimumSize(new java.awt.Dimension(732, 300));

        panel15.setBackground(new java.awt.Color(204, 204, 204));

        label32.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label32.setText("État des bacs");

        panelBorder1.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter1.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter1Layout = new javax.swing.GroupLayout(panelCenter1);
        panelCenter1.setLayout(panelCenter1Layout);
        panelCenter1Layout.setHorizontalGroup(
            panelCenter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter1Layout.setVerticalGroup(
            panelCenter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label33.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label33.setText("Bac 1");

        panelBorder2.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter2.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter2Layout = new javax.swing.GroupLayout(panelCenter2);
        panelCenter2.setLayout(panelCenter2Layout);
        panelCenter2Layout.setHorizontalGroup(
            panelCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter2Layout.setVerticalGroup(
            panelCenter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label34.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label34.setText("Bac 2");

        panelBorder3.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter3.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter3Layout = new javax.swing.GroupLayout(panelCenter3);
        panelCenter3.setLayout(panelCenter3Layout);
        panelCenter3Layout.setHorizontalGroup(
            panelCenter3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter3Layout.setVerticalGroup(
            panelCenter3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder3Layout = new javax.swing.GroupLayout(panelBorder3);
        panelBorder3.setLayout(panelBorder3Layout);
        panelBorder3Layout.setHorizontalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder3Layout.setVerticalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label35.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label35.setText("Bac 3");

        panelBorder4.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter4.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter4Layout = new javax.swing.GroupLayout(panelCenter4);
        panelCenter4.setLayout(panelCenter4Layout);
        panelCenter4Layout.setHorizontalGroup(
            panelCenter4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter4Layout.setVerticalGroup(
            panelCenter4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder4Layout = new javax.swing.GroupLayout(panelBorder4);
        panelBorder4.setLayout(panelBorder4Layout);
        panelBorder4Layout.setHorizontalGroup(
            panelBorder4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder4Layout.setVerticalGroup(
            panelBorder4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label36.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label36.setText("Bac 4");

        panelBorder5.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter5.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter5Layout = new javax.swing.GroupLayout(panelCenter5);
        panelCenter5.setLayout(panelCenter5Layout);
        panelCenter5Layout.setHorizontalGroup(
            panelCenter5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter5Layout.setVerticalGroup(
            panelCenter5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder5Layout = new javax.swing.GroupLayout(panelBorder5);
        panelBorder5.setLayout(panelBorder5Layout);
        panelBorder5Layout.setHorizontalGroup(
            panelBorder5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder5Layout.setVerticalGroup(
            panelBorder5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label37.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label37.setText("Bac 5");

        panelBorder6.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter6.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter6Layout = new javax.swing.GroupLayout(panelCenter6);
        panelCenter6.setLayout(panelCenter6Layout);
        panelCenter6Layout.setHorizontalGroup(
            panelCenter6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter6Layout.setVerticalGroup(
            panelCenter6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder6Layout = new javax.swing.GroupLayout(panelBorder6);
        panelBorder6.setLayout(panelBorder6Layout);
        panelBorder6Layout.setHorizontalGroup(
            panelBorder6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder6Layout.setVerticalGroup(
            panelBorder6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label38.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label38.setText("Bac 6");

        panelBorder7.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter7.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter7Layout = new javax.swing.GroupLayout(panelCenter7);
        panelCenter7.setLayout(panelCenter7Layout);
        panelCenter7Layout.setHorizontalGroup(
            panelCenter7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter7Layout.setVerticalGroup(
            panelCenter7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder7Layout = new javax.swing.GroupLayout(panelBorder7);
        panelBorder7.setLayout(panelBorder7Layout);
        panelBorder7Layout.setHorizontalGroup(
            panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder7Layout.setVerticalGroup(
            panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label39.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label39.setText("Bac 7");

        panelBorder8.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter8.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter8Layout = new javax.swing.GroupLayout(panelCenter8);
        panelCenter8.setLayout(panelCenter8Layout);
        panelCenter8Layout.setHorizontalGroup(
            panelCenter8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter8Layout.setVerticalGroup(
            panelCenter8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder8Layout = new javax.swing.GroupLayout(panelBorder8);
        panelBorder8.setLayout(panelBorder8Layout);
        panelBorder8Layout.setHorizontalGroup(
            panelBorder8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder8Layout.setVerticalGroup(
            panelBorder8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label40.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label40.setText("Bac 8");

        panelBorder9.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter9.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter9Layout = new javax.swing.GroupLayout(panelCenter9);
        panelCenter9.setLayout(panelCenter9Layout);
        panelCenter9Layout.setHorizontalGroup(
            panelCenter9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter9Layout.setVerticalGroup(
            panelCenter9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder9Layout = new javax.swing.GroupLayout(panelBorder9);
        panelBorder9.setLayout(panelBorder9Layout);
        panelBorder9Layout.setHorizontalGroup(
            panelBorder9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder9Layout.setVerticalGroup(
            panelBorder9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label41.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label41.setText("Bac 9");

        panelBorder10.setBackground(new java.awt.Color(246, 246, 246));

        panelCenter10.setBackground(new java.awt.Color(246, 246, 246));

        javax.swing.GroupLayout panelCenter10Layout = new javax.swing.GroupLayout(panelCenter10);
        panelCenter10.setLayout(panelCenter10Layout);
        panelCenter10Layout.setHorizontalGroup(
            panelCenter10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        panelCenter10Layout.setVerticalGroup(
            panelCenter10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelBorder10Layout = new javax.swing.GroupLayout(panelBorder10);
        panelBorder10.setLayout(panelBorder10Layout);
        panelBorder10Layout.setHorizontalGroup(
            panelBorder10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder10Layout.setVerticalGroup(
            panelBorder10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCenter10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label42.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label42.setText("Bac 10");

        javax.swing.GroupLayout panel15Layout = new javax.swing.GroupLayout(panel15);
        panel15.setLayout(panel15Layout);
        panel15Layout.setHorizontalGroup(
            panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel15Layout.createSequentialGroup()
                .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel15Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(label32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelBorder6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel15Layout.createSequentialGroup()
                                .addComponent(label33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel15Layout.createSequentialGroup()
                                .addComponent(label38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        panel15Layout.setVerticalGroup(
            panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel15Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panel15Layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addGroup(panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(label40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panel15Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addComponent(label42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelBorder6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel15Layout.createSequentialGroup()
                        .addComponent(label38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel15Layout.createSequentialGroup()
                        .addComponent(label39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGap(59, 59, 59))
        );

        javax.swing.GroupLayout t_bacsLayout = new javax.swing.GroupLayout(t_bacs.getContentPane());
        t_bacs.getContentPane().setLayout(t_bacsLayout);
        t_bacsLayout.setHorizontalGroup(
            t_bacsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 665, Short.MAX_VALUE)
            .addGroup(t_bacsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(t_bacsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        t_bacsLayout.setVerticalGroup(
            t_bacsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(t_bacsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(t_bacsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        t_inv.setMinimumSize(new java.awt.Dimension(500, 444));

        textField13.setText("textField2");

        label50.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label50.setText("Bac #6");

        tbPoids_Bac6.setEditable(false);

        javax.swing.GroupLayout panel17Layout = new javax.swing.GroupLayout(panel17);
        panel17.setLayout(panel17Layout);
        panel17Layout.setHorizontalGroup(
            panel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel17Layout.setVerticalGroup(
            panel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField14.setText("textField2");

        label22.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label22.setText("Bac #1");

        tbPoids_Bac1.setEditable(false);

        javax.swing.GroupLayout panel18Layout = new javax.swing.GroupLayout(panel18);
        panel18.setLayout(panel18Layout);
        panel18Layout.setHorizontalGroup(
            panel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel18Layout.setVerticalGroup(
            panel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField15.setText("textField2");

        label51.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label51.setText("Bac #5");

        tbPoids_Bac5.setEditable(false);

        javax.swing.GroupLayout panel19Layout = new javax.swing.GroupLayout(panel19);
        panel19.setLayout(panel19Layout);
        panel19Layout.setHorizontalGroup(
            panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel19Layout.setVerticalGroup(
            panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        textField16.setText("textField2");

        label52.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label52.setText("Bac #7");

        tbPoids_Bac7.setEditable(false);

        javax.swing.GroupLayout panel20Layout = new javax.swing.GroupLayout(panel20);
        panel20.setLayout(panel20Layout);
        panel20Layout.setHorizontalGroup(
            panel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel20Layout.setVerticalGroup(
            panel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField17.setText("textField2");

        tbPoids_Bac8.setEditable(false);

        label53.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label53.setText("Bac #8");

        javax.swing.GroupLayout panel21Layout = new javax.swing.GroupLayout(panel21);
        panel21.setLayout(panel21Layout);
        panel21Layout.setHorizontalGroup(
            panel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbPoids_Bac8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel21Layout.setVerticalGroup(
            panel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        textField18.setText("textField2");

        label54.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label54.setText("Bac #3");

        tbPoids_Bac3.setEditable(false);

        javax.swing.GroupLayout panel22Layout = new javax.swing.GroupLayout(panel22);
        panel22.setLayout(panel22Layout);
        panel22Layout.setHorizontalGroup(
            panel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel22Layout.setVerticalGroup(
            panel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        textField19.setText("textField2");

        label55.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label55.setText("Bac #10");

        tbPoids_Bac10.setEditable(false);

        javax.swing.GroupLayout panel23Layout = new javax.swing.GroupLayout(panel23);
        panel23.setLayout(panel23Layout);
        panel23Layout.setHorizontalGroup(
            panel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel23Layout.setVerticalGroup(
            panel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        textField20.setText("textField2");

        label56.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label56.setText("Bac #4");

        tbPoids_Bac4.setEditable(false);

        javax.swing.GroupLayout panel24Layout = new javax.swing.GroupLayout(panel24);
        panel24.setLayout(panel24Layout);
        panel24Layout.setHorizontalGroup(
            panel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel24Layout.setVerticalGroup(
            panel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(tbPoids_Bac4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        textField21.setText("textField2");

        label57.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label57.setText("Bac #9");

        tbPoids_Bac9.setEditable(false);

        javax.swing.GroupLayout panel25Layout = new javax.swing.GroupLayout(panel25);
        panel25.setLayout(panel25Layout);
        panel25Layout.setHorizontalGroup(
            panel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel25Layout.setVerticalGroup(
            panel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        textField22.setText("textField2");

        label20.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label20.setText("Bac #2");

        tbPoids_Bac2.setEditable(false);

        javax.swing.GroupLayout panel26Layout = new javax.swing.GroupLayout(panel26);
        panel26.setLayout(panel26Layout);
        panel26Layout.setHorizontalGroup(
            panel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel26Layout.setVerticalGroup(
            panel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel3.setText("Inventaire des bacs");

        javax.swing.GroupLayout t_invLayout = new javax.swing.GroupLayout(t_inv.getContentPane());
        t_inv.getContentPane().setLayout(t_invLayout);
        t_invLayout.setHorizontalGroup(
            t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(t_invLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(panel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(t_invLayout.createSequentialGroup()
                        .addComponent(panel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(t_invLayout.createSequentialGroup()
                        .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        t_invLayout.setVerticalGroup(
            t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(t_invLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(33, 33, 33)
                .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(t_invLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55))
        );

        t_image.setMinimumSize(new java.awt.Dimension(1920, 1080));

        l_image.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        l_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/terminal_base/images/0.PNG"))); // NOI18N
        l_image.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout t_imageLayout = new javax.swing.GroupLayout(t_image.getContentPane());
        t_image.getContentPane().setLayout(t_imageLayout);
        t_imageLayout.setHorizontalGroup(
            t_imageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, t_imageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(l_image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        t_imageLayout.setVerticalGroup(
            t_imageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(t_imageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(l_image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jOuvrir_Fichier.setApproveButtonText("Ouvrir");
        jOuvrir_Fichier.setApproveButtonToolTipText("Choisissez le fichier d'instruction");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        label17.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label17.setText("Contrôle des étapes");

        tbEtapes.setCaretPosition(3);
        tbEtapes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbEtapes.setEditable(false);

        button_Next.setLabel("Next");
        button_Next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_NextMouseClicked(evt);
            }
        });

        button_Previous.setLabel("Previous");
        button_Previous.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button_PreviousMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addGap(224, 224, 224)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addComponent(button_Previous, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbEtapes, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Next, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label17, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(button_Previous, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button_Next, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbEtapes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        label24.setText("label8");

        label9.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label9.setText("Étape en cours");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addComponent(tb_affiche, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(248, 248, 248)
                .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tb_affiche, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setText("Menu");

        b_quitter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        b_quitter.setText("Quitter");
        b_quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_quitterActionPerformed(evt);
            }
        });
        jMenu1.add(b_quitter);

        b_ouvrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        b_ouvrir.setText("Ouvrir");
        b_ouvrir.setEnabled(false);
        b_ouvrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_ouvrirActionPerformed(evt);
            }
        });
        jMenu1.add(b_ouvrir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edition");

        b_calibre.setText("Calibrer les bacs");
        b_calibre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_calibreActionPerformed(evt);
            }
        });
        jMenu2.add(b_calibre);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Onglets");

        b_etatBacs.setText("État des bacs");
        b_etatBacs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_etatBacsActionPerformed(evt);
            }
        });
        jMenu3.add(b_etatBacs);

        b_Inv.setText("Inventaire");
        b_Inv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_InvActionPerformed(evt);
            }
        });
        jMenu3.add(b_Inv);

        b_image.setText("Image");
        b_image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_imageActionPerformed(evt);
            }
        });
        jMenu3.add(b_image);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
    }//GEN-LAST:event_formWindowClosing

    private void button_NextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_NextMouseClicked
        if (etape_courante < nb_etapes)
        {
            etape_courante++;
            tbEtapes.setText(Integer.toString(etape_courante+1));          
            resetBac();
            logiqueEtape(true);
        }
    }//GEN-LAST:event_button_NextMouseClicked

    private void button_PreviousMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_PreviousMouseClicked
        if (etape_courante > 1) {
            etape_courante--;
            tbEtapes.setText(Integer.toString(etape_courante));
            resetBac();
            logiqueEtape(true);
        }
    }//GEN-LAST:event_button_PreviousMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        d_calib.setVisible(false);  //Faire disparaitre la fenêtre de calibration
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (int i = 0; i < nb_Bacs; i++)
        {   //Calibration de tous les bacs.
            l_Box.get(i).calibrer();
        }
        //nb_pieces_temp = l_Box.get(logiqueEtape(commande, false)).getItemCount(weightConvertionRatio);
        d_calib.setVisible(false);  //Faire disparaitre la fenêtre de calibration
    }//GEN-LAST:event_jButton1ActionPerformed

    private void b_quitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_quitterActionPerformed
        for (int i = 0; i < nb_Bacs; i++)
        {   //Fermeture des connections avec les phidgets.
            l_Box.get(i).stop();
        }
        System.exit(0);
    }//GEN-LAST:event_b_quitterActionPerformed

    private void b_calibreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_calibreActionPerformed
        d_calib.setVisible(true);
    }//GEN-LAST:event_b_calibreActionPerformed

    private void b_etape0_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_etape0_okActionPerformed
        d_etape0.setVisible(false);
    }//GEN-LAST:event_b_etape0_okActionPerformed

    private void b_etatBacsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_etatBacsActionPerformed
        t_bacs.setVisible(true);
    }//GEN-LAST:event_b_etatBacsActionPerformed

    private void b_InvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_InvActionPerformed
        t_inv.setVisible(true);
    }//GEN-LAST:event_b_InvActionPerformed

    private void b_imageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_imageActionPerformed
        t_image.setVisible(true);
    }//GEN-LAST:event_b_imageActionPerformed

    private void b_ouvrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_ouvrirActionPerformed
        //Extrait de code inspiré de la documentation de Netbeans: https://netbeans.org/kb/docs/java/gui-filechooser.html
        int returnVal = jOuvrir_Fichier.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)   //Si l'ouverture du fichier est confirmée...
        {
            File fichierCharge = jOuvrir_Fichier.getSelectedFile();
            try
            {
                JSONObject jotemp = new JSONObject();
                FileReader frLoad = new FileReader(fichierCharge);
                BufferedReader br = new BufferedReader(frLoad);
                boolean redondance = false;
                try //Extraction des étapes du JSON.
                {
                    l_Etape.clear();
                    nb_etapes = 0;
                    while(true)
                    {
                        jotemp = new JSONObject(br.readLine());   //Extraction du JSON des étapes.
                        l_Etape.add(new Etape(jotemp.getInt("numero"), jotemp.getString("nom"), jotemp.getString("message"), jotemp.getInt("num_piece"), jotemp.getInt("nb_pieces")));   //Ajout de l'étape.
                        nb_etapes++;
                        jotemp = new JSONObject();  //On efface le contenu du JSON.
                    }
                }catch(Exception ex){redondance = true;}    //Si la boucle échoue, on active la redondance.
                
                try //Extraction des pièces du JSON vers la liste.
                {
                    l_Piece.clear();
                    while(true)
                    {
                        if(!redondance) //Si la redondance est activée, on n'extrait pas de nouvel élément.
                        {
                            jotemp = new JSONObject(br.readLine());   //Extraction du JSON des pièces.
                        }else
                        {
                            redondance = false;
                        }
                        l_Piece.add(new Piece(jotemp.getInt("numero"), jotemp.getInt("n_bac"), jotemp.getDouble("poid"), jotemp.getString("nom"))); //Ajout de la pièce.
                        l_Box.get(jotemp.getInt("n_bac")).poidItem = jotemp.getDouble("poid");  //Configuration du poid des bacs.
                    }
                }catch(Exception ex){}
                //Fermeture du fichier.
                br.close();
                frLoad.close();
            }catch (Exception ex){System.out.println("[Erreur] Lecture du fichier impossible: "+ex.getMessage());}  //Message d'erreur.
        }
        try //Configuration du code et lancement de la séquance.
        {
            nb_etapes = l_Etape.size();
            etape_courante = 1;
            
            for (int i = 0; i < nb_etapes+1; i++) //Intégration des images du package à la liste d'images
            {
                l_images.add(new javax.swing.ImageIcon(getClass().getResource("/terminal_base/images/"+ i + EXT_IMAGE)));
            }
            
            logiqueEtape(true);
        }catch (Exception ex){}
    }//GEN-LAST:event_b_ouvrirActionPerformed

    /**
     * @brief Main
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        ArrayList<Integer> l_NumSerie = new ArrayList<Integer>(); //Liste de numéros de série.
        int nb_Hub = 0;
        int nb_BoxPerHub = 4;   //Valeur par défaut: 4 bacs par hub.
        double param_trigger = 1.5; //Valeur par défaut: 1.5v de trigger.
        int param_poidPiece = 1;    //Valeur par défaut: 1g/pièces.
        
        // Objet source de données pour l'accès en lecture aux fichiers
        FileInputStream fisFichier;
        // Objet pour la lecture des informations d'une source de données
        BufferedReader brFluxDonnees;
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UI_Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UI_Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UI_Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI_Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UI_Base().setVisible(true);
            }
        });
        //Lecture du fichier config:
        try
        {
            fisFichier = new FileInputStream("config.txt");
            brFluxDonnees = new BufferedReader(new InputStreamReader(fisFichier));
            try
            {
                boolean lectureFinie = false;
                // Lecture des données du fichier config.
                while(!brFluxDonnees.readLine().trim().equals("NUMBOXPERHUB ="));  //Attente de NUMBOXPERHUB.
                nb_BoxPerHub = new Integer(brFluxDonnees.readLine()).intValue();    //Lecture du nombre de bacs/hub.
                
                while(!brFluxDonnees.readLine().trim().equals("WeigthConvertionRatio ="));  //Attente de WeigthConvertionRatio.
                weightConvertionRatio = new Double(brFluxDonnees.readLine()).doubleValue();    //Lecture du ratio de conversion de poid.
                
                while(!brFluxDonnees.readLine().trim().equals("TRIGGER_IR ="));  //Attente de TRIGGER_IR.
                param_trigger = new Double(brFluxDonnees.readLine()).doubleValue();    //Lecture du paramètre de trigger.
                
                while(!brFluxDonnees.readLine().trim().equals("Hubs ="));  //Attente de Hubs.

                while(!lectureFinie) //Lecture jusqu'à la fin du fichier.
                {
                    try
                    {
                        l_NumSerie.add(Integer.parseInt(brFluxDonnees.readLine()));   //Lecture du numéro de série.
                        nb_Hub++;
                    } catch (Exception ex){lectureFinie = true;}
                }
            }catch(Exception ex){System.out.println("[Error] : " + ex.getMessage());}
            nb_Bacs = nb_Hub * nb_BoxPerHub;    //Calcul du nombre de bacs.
        }catch(FileNotFoundException ex){System.out.println("[Error] Fichier de configuration introuvable");}
        
        logiqueEtape(true);   //Charge l'étape 0.
        //Initialisation des objets "Box" pour le contôle des bacs.
        for(int i=0; i<nb_Hub; i++)
        {
            for(int y=0; y<nb_BoxPerHub; y++)
            {
                l_Box.add(new Box(l_NumSerie.get(i),y,iR_Change,param_trigger,param_poidPiece));  //Envoi des paramètres: numéro de série du hub, numéro de bac, pointeur de l'évent de détection infrarouge.
            }
        }
        
        d_etape0.setVisible(true);  //Affiche la boîte de dialogue pour commencer le programme.
        b_ouvrir.setEnabled(true);  //Permet d'ouvrir un fichier d'instructions.
        
        while(true) //Boucle infinie
        {
            //Lecture des capteurs de poid.
            for(int i=0;i<l_Box.size();i++)
            {
                t_tfPoid[i].setText(Integer.toString(l_Box.get(i).getItemCount(weightConvertionRatio)));
            }
            tbEtapes.setText(Integer.toString(etape_courante));
            try
            {
                Thread.sleep(250);  //Rafraichissement des capteurs de poid à tout les 250ms.
            } catch (Exception ex){}
        }
    }
    
    /*
    @brief: Remet les paneaux des bacs à leur état normal suite à l'accomplissement de l'étape en cours.
    @param: Aucun
    */
    private static void resetBac()
    {
        for(int i = 0;i<t_panelInterne.length;i++)
        {
            t_panelInterne[i].setBackground(Color.white);
        }
    }
    
    /*
    @brief: Met en évidence le bac nécessaire à l'accomplissement de l'étape en cours.
    @param: iBacActif: Contient le numéro du bac actuellement en cours d'utilisation
    */
    private static void surligneBac(int iBacActif)
    {
        t_panelInterne[iBacActif].setBackground(Color.green);
    }
    
    /*
    @brief: Compare un numéro de bac passé en paramètre avec le bac actif pour déterminer si une erreur à été commise.
    @param: BacCourant: Bac à comparer.
    */
    private static boolean checkErreurBac(int BacCourant)
    {
        if(bacActif != BacCourant)
        {
            t_panelInterne[bacActif].setBackground(Color.red);    //Met en évidence le bac fautif.
            mqttPublish("Ce n'est pas le bon bac!!! Allez au bac #" + (BacCourant+1), "Base", String.valueOf(etape_courante));  //Publie le message d'erreur.
            tb_affiche.add("Ce n'est pas le bon bac!!! Allez au bac #" + (BacCourant+1));
            return false;
        }
        return true;
    }
    
    /*
    @brief: Compare le nombre de pièces dans le bac passé en paramètre avec le nombre de pièces atendu pour déterminer si une erreur à été commise.
    @param: BacCourant: numéro de bac où on veut vérifier le poid.
    */
    public static boolean checkErreurPoid(int BacCourant)
    {
        int nb_pieces_etape = l_Etape.get(etape_courante).num_piece;
        try
        {
            int nb_pieces = l_Box.get(BacCourant).getItemCount(weightConvertionRatio);
            if(nb_pieces < nb_pieces_temp-nb_pieces_etape)    //Si l'usager a pris trop de pièces, on lui en avertis.
            {
                tb_affiche.add("Vous avez pris trop de pièces, veuillez n'en prendre qu'une pour terminer cette étape.");
                return false;
            }
            else if(nb_pieces >= nb_pieces_temp)    //Si l'usager ne prend pas assez de pièces, aucun message n'est envoyé mais on ne change pas d'étape.
            {
                return false;
            }
            else if(nb_pieces == nb_pieces_temp-nb_pieces_etape)    //Si le bon nombre de pièces ont été prises, on retourne 'true'.
            {
                return true;
            }
            return false;
        }catch(Exception ex)
        {
            System.out.println("[Error] checkErreurPoid: "+ ex.getMessage());
            return false;
        }
    }
    
    /*
    @brief: Méthode déduisant le bac actif et construisant le message à transmettre au casque et à l'interface usager.
    @param: publish: détermine si un message sera émit vers le casque ou non. À utiliser si on désire seulement recevoir la valeur de retour.
    @return: Position du bac où il faut prendre la pièce.
    */
    public static int logiqueEtape(boolean publish)
    {
        JSONObject messageBaseJsonObj = new JSONObject();
        int bacCourant = 0;
        messageBaseJsonObj.put("Source", "Base");
        if(etape_courante == 0)
        {
            messageBaseJsonObj.put("Message", "Debut de la communication. Suivez attentivement les instructions de votre guide a l'etape indiquee");
        }else
        {
            bacCourant = l_Piece.get(l_Etape.get(etape_courante-1).num_piece).n_bac;    //Déterminer le bac courant.
            surligneBac(bacCourant);    //Surligner le bac courant
            messageBaseJsonObj.put("Message", l_Etape.get(etape_courante-1).message);   //Récupérer le message à envoyer.
        }
        messageBaseJsonObj.put("Numetape", etape_courante);
        try
        {
            if (publish)
            {
                if(etape_courante != 0) //À l'étape 0, aucune nouvelle image n'est chargée.
                {
                    changeImage(etape_courante);
                }
                mqttPublish(messageBaseJsonObj);
                tb_affiche.add(messageBaseJsonObj.getString("Message"));    //Affiche l'instruction dans la textbox.
            }
        }catch(Exception ex) {System.out.println("[Error] logiqueEtape: "+ ex.getMessage());}
        return bacCourant;
    }
    
    /*
    @brief: Publie par MQTT le message d'étape destiné au casque.
    @param: jsMessage: objet JSON contenant le message à transmettre au casque.
    */
    private static void mqttPublish(JSONObject jsMessage)
    {
        try
        {
            Buffer msgBacs = new AsciiBuffer(jsMessage.toString(2));
            connection.connect();
            connection.publish(TOPIC_CASQUE, msgBacs, QoS.AT_LEAST_ONCE, false);
        }catch(Exception ex){}
    }
    /*
    @brief: (Surcharge) Publie par MQTT le message d'étape destiné au casque.
    @param: message: le message à transmettre au casque.
            source: source du message.
            numetape: numéro de l'étape courante.
    */
    private static void mqttPublish(String message, String source, String numetape) //Surchatge
    {
        JSONObject jsMessage = new JSONObject();
        jsMessage.put("Source", source);
        jsMessage.put("Message", message);
        jsMessage.put("Numetape", numetape);
        try
        {
            Buffer msgBacs = new AsciiBuffer(jsMessage.toString(2));
            connection.connect();
            connection.publish(TOPIC_CASQUE, msgBacs, QoS.AT_LEAST_ONCE, false);
        }catch(Exception ex){}
    }
    
    /*
    @brief: Affiche une image dans l'onglet d'image.
    @param: numImage: index de la liste d'image à afficher.
    */
    private static void changeImage(int numImage)
    {
        try
        {
            //Affichage d'une nouvelle image.
            l_image.setIcon(l_images.get(numImage));
            //Correction dynamique de la taille de la fenêtre d'image.
            t_image.setSize(UI_Base.t_image.getSize());
        }catch(Exception ex){System.out.println("[Erreur] Changement d'image impossible: " + ex.getMessage());}
    }
    
    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem b_Inv;
    private javax.swing.JMenuItem b_calibre;
    private javax.swing.JButton b_etape0_cancel;
    private javax.swing.JButton b_etape0_ok;
    private javax.swing.JMenuItem b_etatBacs;
    private javax.swing.JMenuItem b_image;
    private static javax.swing.JMenuItem b_ouvrir;
    private javax.swing.JMenuItem b_quitter;
    private java.awt.Button button_Next;
    private java.awt.Button button_Previous;
    private javax.swing.JDialog d_calib;
    private static javax.swing.JDialog d_etape0;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JFileChooser jOuvrir_Fichier;
    private static javax.swing.JLabel l_image;
    private java.awt.Label label17;
    private java.awt.Label label20;
    private java.awt.Label label22;
    private java.awt.Label label24;
    private java.awt.Label label32;
    private java.awt.Label label33;
    private java.awt.Label label34;
    private java.awt.Label label35;
    private java.awt.Label label36;
    private java.awt.Label label37;
    private java.awt.Label label38;
    private java.awt.Label label39;
    private java.awt.Label label40;
    private java.awt.Label label41;
    private java.awt.Label label42;
    private java.awt.Label label50;
    private java.awt.Label label51;
    private java.awt.Label label52;
    private java.awt.Label label53;
    private java.awt.Label label54;
    private java.awt.Label label55;
    private java.awt.Label label56;
    private java.awt.Label label57;
    private java.awt.Label label9;
    private java.awt.Panel panel15;
    public java.awt.Panel panel17;
    private java.awt.Panel panel18;
    private java.awt.Panel panel19;
    private java.awt.Panel panel20;
    private java.awt.Panel panel21;
    private java.awt.Panel panel22;
    private java.awt.Panel panel23;
    private java.awt.Panel panel24;
    private java.awt.Panel panel25;
    private java.awt.Panel panel26;
    private java.awt.Panel panel3;
    private java.awt.Panel panel5;
    public static java.awt.Panel panelBorder1;
    public static java.awt.Panel panelBorder10;
    public static java.awt.Panel panelBorder2;
    public static java.awt.Panel panelBorder3;
    public static java.awt.Panel panelBorder4;
    public static java.awt.Panel panelBorder5;
    public static java.awt.Panel panelBorder6;
    public static java.awt.Panel panelBorder7;
    public static java.awt.Panel panelBorder8;
    public static java.awt.Panel panelBorder9;
    public static java.awt.Panel panelCenter1;
    public static java.awt.Panel panelCenter10;
    public static java.awt.Panel panelCenter2;
    public static java.awt.Panel panelCenter3;
    public static java.awt.Panel panelCenter4;
    public static java.awt.Panel panelCenter5;
    public static java.awt.Panel panelCenter6;
    public static java.awt.Panel panelCenter7;
    public static java.awt.Panel panelCenter8;
    public static java.awt.Panel panelCenter9;
    public javax.swing.JFrame t_bacs;
    private static javax.swing.JFrame t_image;
    private javax.swing.JFrame t_inv;
    private static java.awt.TextField tbEtapes;
    private static java.awt.TextField tbPoids_Bac1;
    private static java.awt.TextField tbPoids_Bac10;
    private static java.awt.TextField tbPoids_Bac2;
    private static java.awt.TextField tbPoids_Bac3;
    private static java.awt.TextField tbPoids_Bac4;
    private static java.awt.TextField tbPoids_Bac5;
    private static java.awt.TextField tbPoids_Bac6;
    private static java.awt.TextField tbPoids_Bac7;
    private static java.awt.TextField tbPoids_Bac8;
    private static java.awt.TextField tbPoids_Bac9;
    private static java.awt.List tb_affiche;
    private java.awt.TextField textField13;
    private java.awt.TextField textField14;
    private java.awt.TextField textField15;
    private java.awt.TextField textField16;
    private java.awt.TextField textField17;
    private java.awt.TextField textField18;
    private java.awt.TextField textField19;
    private java.awt.TextField textField20;
    private java.awt.TextField textField21;
    private java.awt.TextField textField22;
    // End of variables declaration//GEN-END:variables
}
