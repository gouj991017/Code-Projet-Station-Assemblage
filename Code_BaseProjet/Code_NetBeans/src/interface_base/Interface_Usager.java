/*
    PROJECT TITLE: Code de gestion pour la base d'une station d'assemblage
    Brief: S'occupe d'envoyer les différentes informations liées aux étapes de montage d'un produit à une lunette de réalité augmenté via une communnication MQTT entre
    deux RaspBerry Pi Zéro W et affiche l'étape actuel à laquelle l'opérateur est arrivé ainsi qu'une copie des étapes envoyées à la lunette sur une interface usager. Les bacs
    contenant les pièces nécessaires à la réalisation du produit sont mis en évidence selon l'étape de montage à laquelle l'opérateur se trouve, cette information sera également affiché
    sur l'interface usager.
    Les informations typiques d'une commande seront également affichées sur l'interface usager de la base (la commande sera, en d'autres mots, décortiquée selon ses spécifications).
    On peut passer à l'étape suivante ou revenir à l'étape précédente à l'aide de boutons sur l'interface usager.
    Les informations concernant le poids des bacs sont également affichées sur l'interface.
    VERSION : V2.8
    AUTHORS: Jérémy Goulet
 */
package interface_base;

/**
 * Write a description of class AppBase here.
 * 
 * @author (Jeremy Goulet) 
 * @version (29-11-2019)
 */

/* Control des GPIOs
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.*;
*/
import com.phidget22.PhidgetException;
import com.phidget22.*;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URISyntaxException;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Interface_Usager extends javax.swing.JFrame {

    static final int NB_BACS = 8; //Constante du nombre de bacs relié au Raspberry pi.
    static final double TRIGGER_INFRAROUGE = 2.00; //Constante du trigger faisant varier la tension du capteur infrarouge.
    static final double PENTE_MOYENNE_CAPTEUR = 0.0203; //Constante contenant la pente moyenne du capteur de poids
    static final double TENSION_ORIGINE = -2.5; //Constante contenant la valeur de tension d'origine du capteur de poids lorsqu'aucun poids n'y est appliqué
    //static GpioPinDigitalOutput t_outputIOs[] = new GpioPinDigitalOutput[NB_BACS]; //***********************************************
    //static GpioPinDigitalInput t_inputIOs[] = new GpioPinDigitalInput[NB_BACS]; //***********************************************
    
    static List<String> m_listeObjList = new ArrayList<String>();
    
    enum enumBacs { //Attribut des bacs pour chaque composants d'un produit
        init,
        baseType1Couleur1,
        baseType1Couleur2,
        baseType2Couleur1,
        baseType2Couleur2,
        crayonType1,
        crayonType2,
        crayonType3,
        supportCourt,
        supportMoyen;
        
    }
    static int numPageCourante = 0; //Contient l'étape courante de l'assemblage d'un produit
    static int choixProduit = 0; //Contient le type de produit à assembler
    static boolean exit = false; //Permet de quiter la boucle lorsque une commande est terminé
    static String Str = ""; //Contient les informations d'une commande
    static int choixBase = 0; //Contient les informations concernant la base du produit selon la commande reçue
    static int choixCrayon = 0; //Contient les informations concernant les portes-crayons du produit selon la commande reçue
    static int choixSupports = 0; //Contient les informations concernant les supports du produit selon la commande reçue
    static int choixCouleur = 0; //Contient les informations concernant la couleur du produit selon la commande reçue
    static int quantite = 0; //Contient la quantité de produit à assembler dans une commande
    static int i = 0;
    static String produit = ""; //Indique quel produit il faut assembler
    static String base = ""; //Contient les informations du type de base à utiliser pour l'assemblage d'un produit
    static String crayon = ""; //Contient les informations du type de porte-crayon à utiliser pour l'assemblage d'un produit
    static String qteCrayon = ""; //Contient les informations du type de supports à utiliser pour l'assemblage d'un produit
    static String supports = ""; //Contient les informations du type de supports à utiliser pour l'assemblage d'un produit
    static String couleur = ""; //Contient les informations de la couleur du composant à utiliser pour l'assemblage d'un produit
    static boolean multiCrayon = false; //Indique si le produit possède plusieurs porte-crayon
    static int bacActif = 0; //Indique quel bac est actuellement sélectionné
    static boolean rechargeListe = true; //Indique que la liste d'instructions dans l'interface usager a besoin d'être rafraichit.
    static boolean messageBac1 = false; //Empêche les capteurs infrarouges du bac 1 d'envoyer plusieurs fois le même message.
    static boolean messageBac2 = false; //Empêche les capteurs infrarouges du bac 2 d'envoyer plusieurs fois le même message.
    static double poidCellule0 = 0.00; //Contient la valeur en gramme du poids du bac 1.
    static double poidCellule1 = 0.00; //Contient la valeur en gramme du poids du bac 2.
    
    //Create your Phidget channels
    static VoltageInput vInput0;
    static VoltageInput vInput1;
    static VoltageInput vInput2;
    static VoltageInput vInput3;
    static DigitalOutput digitalOut0;
    static DigitalOutput digitalOut1;
    static VoltageRatioInput vRatioInput0;
    static VoltageRatioInput vRatioInput1;
    
    /**
     * Constructor for objects of class Interface_Usager
     * Creates new form Interface_Usager
     */
    public Interface_Usager() {
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        label6 = new java.awt.Label();
        label7 = new java.awt.Label();
        textField1 = new java.awt.TextField();
        tbProduit = new java.awt.TextField();
        tbBase = new java.awt.TextField();
        tbCouleur = new java.awt.TextField();
        tbCrayon = new java.awt.TextField();
        tbSupports = new java.awt.TextField();
        tbQuantite = new java.awt.TextField();
        panel2 = new java.awt.Panel();
        label10 = new java.awt.Label();
        panelBorder1 = new java.awt.Panel();
        panelCenter1 = new java.awt.Panel();
        label8 = new java.awt.Label();
        panelBorder2 = new java.awt.Panel();
        panelCenter2 = new java.awt.Panel();
        label12 = new java.awt.Label();
        panelBorder3 = new java.awt.Panel();
        panelCenter3 = new java.awt.Panel();
        label11 = new java.awt.Label();
        panelBorder4 = new java.awt.Panel();
        panelCenter4 = new java.awt.Panel();
        label13 = new java.awt.Label();
        panelBorder5 = new java.awt.Panel();
        panelCenter5 = new java.awt.Panel();
        label14 = new java.awt.Label();
        panelBorder6 = new java.awt.Panel();
        panelCenter6 = new java.awt.Panel();
        label15 = new java.awt.Label();
        panelBorder7 = new java.awt.Panel();
        panelCenter7 = new java.awt.Panel();
        label16 = new java.awt.Label();
        panelBorder8 = new java.awt.Panel();
        panelCenter8 = new java.awt.Panel();
        label18 = new java.awt.Label();
        panelBorder9 = new java.awt.Panel();
        panelCenter9 = new java.awt.Panel();
        label20 = new java.awt.Label();
        panelBorder10 = new java.awt.Panel();
        panelCenter10 = new java.awt.Panel();
        label22 = new java.awt.Label();
        panel3 = new java.awt.Panel();
        label24 = new java.awt.Label();
        list1 = new java.awt.List();
        label9 = new java.awt.Label();
        panel4 = new java.awt.Panel();
        textField2 = new java.awt.TextField();
        label19 = new java.awt.Label();
        tbPoids_Bac2 = new java.awt.TextField();
        panel5 = new java.awt.Panel();
        label17 = new java.awt.Label();
        tbEtapes = new java.awt.TextField();
        button_Next = new java.awt.Button();
        button_Previous = new java.awt.Button();
        panel6 = new java.awt.Panel();
        textField10 = new java.awt.TextField();
        label21 = new java.awt.Label();
        tbPoids_Bac1 = new java.awt.TextField();
        panel7 = new java.awt.Panel();
        textField3 = new java.awt.TextField();
        label23 = new java.awt.Label();
        tbPoids_Bac5 = new java.awt.TextField();
        panel8 = new java.awt.Panel();
        textField4 = new java.awt.TextField();
        label25 = new java.awt.Label();
        tbPoids_Bac6 = new java.awt.TextField();
        panel9 = new java.awt.Panel();
        textField5 = new java.awt.TextField();
        label26 = new java.awt.Label();
        tbPoids_Bac3 = new java.awt.TextField();
        panel10 = new java.awt.Panel();
        textField6 = new java.awt.TextField();
        label27 = new java.awt.Label();
        tbPoids_Bac7 = new java.awt.TextField();
        panel11 = new java.awt.Panel();
        textField7 = new java.awt.TextField();
        label28 = new java.awt.Label();
        tbPoids_Bac9 = new java.awt.TextField();
        panel12 = new java.awt.Panel();
        textField8 = new java.awt.TextField();
        tbPoids_Bac8 = new java.awt.TextField();
        label29 = new java.awt.Label();
        panel13 = new java.awt.Panel();
        textField9 = new java.awt.TextField();
        label30 = new java.awt.Label();
        tbPoids_Bac4 = new java.awt.TextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        label1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label1.setText("Infos Commande");

        label2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label2.setText("Choix du produit");

        label3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label3.setText("Choix de la base");

        label4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label4.setText("Couleur");

        label5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label5.setText("Type porte crayon");

        label6.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label6.setText("Choix des supports");

        label7.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label7.setText("Quantité");

        textField1.setText("textField1");

        tbProduit.setEditable(false);
        tbProduit.setName("tbProduit"); // NOI18N

        tbBase.setEditable(false);
        tbBase.setName("tbBase"); // NOI18N

        tbCouleur.setEditable(false);
        tbCouleur.setName("tbCrayon"); // NOI18N

        tbCrayon.setEditable(false);
        tbCrayon.setName("tbSupports"); // NOI18N

        tbSupports.setEditable(false);
        tbSupports.setName("tbQuantite"); // NOI18N

        tbQuantite.setEditable(false);
        tbQuantite.setName("tbCouleur"); // NOI18N

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbSupports, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(tbProduit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbQuantite, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbCrayon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbCouleur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(131, Short.MAX_VALUE)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbSupports, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbProduit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbCouleur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbCrayon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbQuantite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        panel2.setBackground(new java.awt.Color(204, 204, 204));

        label10.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label10.setText("État des bacs");

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

        label8.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label8.setText("Bac 1");

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

        label12.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label12.setText("Bac 2");

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

        label11.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label11.setText("Bac 3");

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

        label13.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label13.setText("Bac 4");

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

        panelCenter5.getAccessibleContext().setAccessibleParent(panelBorder4);

        label14.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label14.setText("Bac 5");

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

        label15.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label15.setText("Bac 6");

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

        label16.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label16.setText("Bac 7");

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

        label18.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label18.setText("Bac 8");

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

        label20.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label20.setText("Bac 9");

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

        label22.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label22.setText("Bac 10");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(label10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelBorder6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(label15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelBorder10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelBorder10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(label18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addComponent(label22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelBorder6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addComponent(label15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addComponent(label16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGap(59, 59, 59))
        );

        panelBorder5.getAccessibleContext().setAccessibleName("panelBorder4");
        panelBorder5.getAccessibleContext().setAccessibleParent(panelBorder4);

        label24.setText("label8");

        label9.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label9.setText("Étape en cours");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addComponent(list1, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(248, 248, 248)
                .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
        );

        textField2.setText("textField2");

        label19.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label19.setText("Bac #2");

        tbPoids_Bac2.setEditable(false);

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        label17.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label17.setText("Contrôle des étapes");

        tbEtapes.setCaretPosition(3);
        tbEtapes.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                        .addComponent(label17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                        .addComponent(button_Previous, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(tbEtapes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(58, 58, 58)
                        .addComponent(button_Next, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))))
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label17, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbEtapes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Previous, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Next, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        textField10.setText("textField2");

        label21.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label21.setText("Bac #1");

        tbPoids_Bac1.setEditable(false);

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPoids_Bac1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField3.setText("textField2");

        label23.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label23.setText("Bac #5");

        tbPoids_Bac5.setEditable(false);

        javax.swing.GroupLayout panel7Layout = new javax.swing.GroupLayout(panel7);
        panel7.setLayout(panel7Layout);
        panel7Layout.setHorizontalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel7Layout.setVerticalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        textField4.setText("textField2");

        label25.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label25.setText("Bac #6");

        tbPoids_Bac6.setEditable(false);

        javax.swing.GroupLayout panel8Layout = new javax.swing.GroupLayout(panel8);
        panel8.setLayout(panel8Layout);
        panel8Layout.setHorizontalGroup(
            panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel8Layout.setVerticalGroup(
            panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField5.setText("textField2");

        label26.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label26.setText("Bac #3");

        tbPoids_Bac3.setEditable(false);

        javax.swing.GroupLayout panel9Layout = new javax.swing.GroupLayout(panel9);
        panel9.setLayout(panel9Layout);
        panel9Layout.setHorizontalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel9Layout.setVerticalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        textField6.setText("textField2");

        label27.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label27.setText("Bac #7");

        tbPoids_Bac7.setEditable(false);

        javax.swing.GroupLayout panel10Layout = new javax.swing.GroupLayout(panel10);
        panel10.setLayout(panel10Layout);
        panel10Layout.setHorizontalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel10Layout.setVerticalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textField7.setText("textField2");

        label28.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label28.setText("Bac #9");

        tbPoids_Bac9.setEditable(false);

        javax.swing.GroupLayout panel11Layout = new javax.swing.GroupLayout(panel11);
        panel11.setLayout(panel11Layout);
        panel11Layout.setHorizontalGroup(
            panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel11Layout.setVerticalGroup(
            panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(tbPoids_Bac9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        textField8.setText("textField2");

        tbPoids_Bac8.setEditable(false);

        label29.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label29.setText("Bac #8");

        javax.swing.GroupLayout panel12Layout = new javax.swing.GroupLayout(panel12);
        panel12.setLayout(panel12Layout);
        panel12Layout.setHorizontalGroup(
            panel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbPoids_Bac8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel12Layout.setVerticalGroup(
            panel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbPoids_Bac8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        textField9.setText("textField2");

        label30.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label30.setText("Bac #4");

        tbPoids_Bac4.setEditable(false);

        javax.swing.GroupLayout panel13Layout = new javax.swing.GroupLayout(panel13);
        panel13.setLayout(panel13Layout);
        panel13Layout.setHorizontalGroup(
            panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbPoids_Bac4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        panel13Layout.setVerticalGroup(
            panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(tbPoids_Bac4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(panel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(panel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(panel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(panel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(panel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 5, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
    brief : Retourne à l'étape d'assemblage précédente manuellement
    */
    private void button_PreviousMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_PreviousMouseClicked
        if (numPageCourante > 1) {
            numPageCourante--;
            tbEtapes.setText(Integer.toString(numPageCourante));
            resetBac();
        }
    }//GEN-LAST:event_button_PreviousMouseClicked

    /*
    brief : Passe à l'étape d'assemblage suivante manuellement.
    */
    private void button_NextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button_NextMouseClicked
        if (numPageCourante < 4) {
            numPageCourante++;
            tbEtapes.setText(Integer.toString(numPageCourante));
            resetBac();
        }
    }//GEN-LAST:event_button_NextMouseClicked

    /*
    brief : Ferme tous les ports et canaux avant d'éteindre le reste du programme.
    */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            //Close your Phidgets once the program is done.
            vInput0.close();
            vInput1.close();
            digitalOut0.close();
            vRatioInput0.close();
        } catch (PhidgetException ex) {
            //We will catch Phidget Exceptions here, and print the error informaiton.
            ex.printStackTrace();
            System.out.println("");
            System.out.println("PhidgetException " + ex.getErrorCode() + " (" + ex.getDescription() + "): " + ex.getDetail());
        }
    }//GEN-LAST:event_formWindowClosing
    
    /*
    brief : S'occupe des capteurs infrarouges du bac #1
    */
    public static VoltageInputVoltageChangeListener onBac1_VoltageChange =
        new VoltageInputVoltageChangeListener() {
        @Override
        public void onVoltageChange(VoltageInputVoltageChangeEvent e) {
            try {
                double tensionCapteur = e.getVoltage();
                boolean envoieMessage = false;
                
                String user = "admin";
                String password = "admin";
                String host = "127.0.0.1"; // Possiblement a modifier  192.168.137.171********************************************************************************************
                int port = Integer.parseInt("1883");
                final String destination = "/scal/scal_reponse_requete";

                JSONObject messageBaseJsonObj = new JSONObject();

                MQTT mqtt = new MQTT();
                mqtt.setHost(host, port);
                mqtt.setUserName(user);
                mqtt.setPassword(password);
                BlockingConnection connectionBac1 = mqtt.blockingConnection();
                connectionBac1.connect();
                
                String TOPIC_REPONSE = "/scal/scal_requete_acces";
                
                messageBaseJsonObj.put("Source", new String[] { "Base"});
                messageBaseJsonObj.put("Numetape", numPageCourante);
                
                System.out.println("Voltage: " + e.getSource().getChannel() + " " + tensionCapteur); //****************************************************************************************
                
                
                if(tensionCapteur > 2.00)  //L'opérateur a mis sa main dans le bac
                {
                    //digitalOut0.setState(true); //Allume la LED rouge du bac courant //************************************************************************************
                    panelBorder1.setBackground(Color.yellow); //Couleur déclarant la détection d'une manipulation de la part d'un opérateur dans un bac
                    if(messageBac1 == false)
                    {
                        if(bacActif != 1) //S'il ne s'agit pas du bac indiqué dans les instructions
                        {
                            envoieMessage = true;
                            panelCenter1.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        messageBac1 = true;
                    }
                }
                else if(tensionCapteur < 2.00) //L'opérateur a retiré sa main du bac
                {
                    if(messageBac1)
                    {
                        if(bacActif == 1) //S'il s'agit du bac indiqué dans les instructions
                        {
                            envoieMessage = true;
                            numPageCourante++; //On incrémente la variable et on passe à l'étape suivante
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                            resetBac();
                        }
                        messageBac1 = false;
                    }
                    panelBorder1.setBackground(Color.white);
                    //digitalOut0.setState(false); //Éteind la LED rouge du bac courant //**********************************************************************************
                }
                
                if (envoieMessage) {
                    envoieMessage = false;
                    rechargeListe = true;
                    String DATA = messageBaseJsonObj.toString(2);
                    Buffer msgErreur = new AsciiBuffer(DATA);
                    UTF8Buffer topic = new UTF8Buffer(destination);
                    connectionBac1.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                }
                
            } catch (PhidgetException ex) {
                //We will catch Phidget Exceptions here, and print the error informaiton.
                ex.printStackTrace();
                System.out.println("");
                System.out.println("PhidgetException " + ex.getErrorCode() + " (" + ex.getDescription() + "): " + ex.getDetail());
            } catch (Exception ex) {
                Logger.getLogger(Interface_Usager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    /*
    brief : S'occupe des capteurs infrarouges du bac #2
    */
    public static VoltageInputVoltageChangeListener onBac2_VoltageChange =
        new VoltageInputVoltageChangeListener() {
        @Override
        public void onVoltageChange(VoltageInputVoltageChangeEvent e) {
            try {
                double tensionCapteur = e.getVoltage();
                boolean envoieMessage = false;
                
                String user = "admin";
                String password = "admin";
                String host = "127.0.0.1"; // Possiblement a modifier  192.168.137.171********************************************************************************************
                int port = Integer.parseInt("1883");
                final String destination = "/scal/scal_reponse_requete";

                JSONObject messageBaseJsonObj = new JSONObject();

                MQTT mqtt = new MQTT();
                mqtt.setHost(host, port);
                mqtt.setUserName(user);
                mqtt.setPassword(password);
                BlockingConnection connectionBac2 = mqtt.blockingConnection();
                connectionBac2.connect();
                
                String TOPIC_REPONSE = "/scal/scal_requete_acces";
                
                messageBaseJsonObj.put("Source", new String[] { "Base"});
                messageBaseJsonObj.put("Numetape", numPageCourante);
                
                System.out.println("Voltage: " + e.getSource().getChannel() + " " + tensionCapteur); //****************************************************************************************
                
                
                if(tensionCapteur > 2.00)  //L'opérateur a mis sa main dans le bac
                {
                    //digitalOut1.setState(true); //Allume la LED rouge du bac courant //************************************************************************************
                    panelBorder2.setBackground(Color.yellow); //Couleur déclarant la détection d'une manipulation de la part d'un opérateur dans un bac
                    if(messageBac2 == false)
                    {
                        if(bacActif != 2) //S'il ne s'agit pas du bac indiqué dans les instructions
                        {
                            envoieMessage = true;
                            panelCenter2.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        messageBac2 = true;
                    }
                }
                else if(tensionCapteur < 2.00) //L'opérateur a retiré sa main du bac
                {
                    if(messageBac2)
                    {
                        if(bacActif == 2) //S'il s'agit du bac indiqué dans les instructions
                        {
                            envoieMessage = true;
                            numPageCourante++; //On incrémente la variable et on passe à l'étape suivante
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                            resetBac();
                        }
                        messageBac2 = false;
                    }
                    panelBorder2.setBackground(Color.white);
                    //digitalOut1.setState(false); //Éteind la LED rouge du bac courant //**********************************************************************************
                }
                
                if (envoieMessage) {
                    envoieMessage = false;
                    rechargeListe = true;
                    String DATA = messageBaseJsonObj.toString(2);
                    Buffer msgErreur = new AsciiBuffer(DATA);
                    UTF8Buffer topic = new UTF8Buffer(destination);
                    connectionBac2.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                }
                
            } catch (PhidgetException ex) {
                //We will catch Phidget Exceptions here, and print the error informaiton.
                ex.printStackTrace();
                System.out.println("");
                System.out.println("PhidgetException " + ex.getErrorCode() + " (" + ex.getDescription() + "): " + ex.getDetail());
            } catch (Exception ex) {
                Logger.getLogger(Interface_Usager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    /*
    brief : S'occupe de mettre à jour l'interface usager
    */
    static Runnable runnable = new Runnable(){
        @Override
        public void run() {
            int j = 0;
            
            //Create gpio controller
            //GpioController gpio = GpioFactory.getInstance(); //*************************************************************************

            try
            {
                /*
                //Provision gpio pin 12,3,5,7 as an output pin
                t_outputIOs[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED1", PinState.HIGH);   //pin 12 du header
                t_outputIOs[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "LED2", PinState.LOW);    //pin 5 du header
                t_outputIOs[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, "LED3", PinState.LOW);    //pin 29 du header
                t_outputIOs[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "LED4", PinState.LOW);    //pin 26 du header

                //Provision gpio pin 27,11,7,31 as an input pin with its internal pull down resistor enabled
                t_inputIOs[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_30, "Bac1", PinPullResistance.OFF); //pin 27 du header
                t_inputIOs[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Bac2", PinPullResistance.OFF); //pin 11 du header
                t_inputIOs[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "Bac3", PinPullResistance.OFF); //pin 7 du header
                t_inputIOs[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, "Bac4", PinPullResistance.OFF); //pin 31 du header
                */
                //Pin  28,11,13 non-controlable
            }catch(Exception ex){}
            /*
            String user = "admin";
            String password = "admin";
            String host = "192.168.137.171"; // Possiblement a modifier  192.168.137.171********************************************************************************************
            int port = Integer.parseInt("1883");
            final String destination = "/scal/scal_reponse_requete";

            JSONObject messageBaseJsonObj = new JSONObject();

            MQTT mqtt = new MQTT();
            try {
                mqtt.setHost(host, port);
            } catch (URISyntaxException ex) {
                Logger.getLogger(Interface_Usager.class.getName()).log(Level.SEVERE, null, ex);
            }
            mqtt.setUserName(user);
            mqtt.setPassword(password);
            BlockingConnection connectionThread = mqtt.blockingConnection();

            String TOPIC_REPONSE = "/scal/scal_requete_acces";*/
            
            while(true){
                try
                {
                    if (rechargeListe) {
                        rechargeListe = false;
                        //Met à jour la liste des instructions de l'assemblage du produit
                        list1.removeAll();
                        for (int i = 0; i < m_listeObjList.size(); i++) {
                            list1.add(m_listeObjList.get(i));
                            j++;
                        }
                        list1.select(j-1);
                        j = 0;
                    }
                    
                    //Envoi les informations d'une commande à l'interface
                    tbProduit.setText(produit);
                    tbBase.setText(base);
                    tbCouleur.setText(couleur);
                    tbCrayon.setText(qteCrayon + crayon);
                    tbSupports.setText(supports);
                    tbQuantite.setText(Integer.toString(quantite));
                    tbEtapes.setText(Integer.toString(numPageCourante));
                    
                    //Envoi les informations des cellules de charge à l'interface
                    poidCellule0 = ((vRatioInput0.getVoltageRatio() - TENSION_ORIGINE)/PENTE_MOYENNE_CAPTEUR)* 100;
                    poidCellule1 = ((vRatioInput1.getVoltageRatio() - TENSION_ORIGINE)/PENTE_MOYENNE_CAPTEUR)* 100;
                    if (poidCellule0 < 0 || poidCellule1 < 0) {
                        poidCellule0 = 0;
                        poidCellule1 = 0;
                    }
                    
                    tbPoids_Bac1.setText(Double.toString(poidCellule));
                    tbPoids_Bac2.setText(Double.toString(0.00));
                    tbPoids_Bac3.setText(Double.toString(0.00));
                    tbPoids_Bac4.setText(Double.toString(0.00));
                    tbPoids_Bac5.setText(Double.toString(0.00));
                    tbPoids_Bac6.setText(Double.toString(0.00));
                    tbPoids_Bac7.setText(Double.toString(0.00));
                    tbPoids_Bac8.setText(Double.toString(0.00));
                    tbPoids_Bac9.setText(Double.toString(0.00));
                    
                    /*
                    t_outputIOs[bacActif].high(); //Allume la LED du bac courant

                    if(t_inputIOs[0].isHigh()) //Bac #1
                    {
                        panelBorder1.setBackground(Color.yellow); //Couleur déclarant la détection d'une manipulation de la part d'un opérateur dans un bac
                        if(bacActif != 1) //S'il ne s'agit pas du bac indiqué dans les instructions
                        {
                            panelCenter1.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low(); //Éteind la LED du bac courant
                            numPageCourante++; //On incrémente la variable et on passe à l'étape suivante
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[0].isHigh()); //On attend que l'opérateur ait retiré sa main du bac avant de poursuivre l'assemblage
                        panelBorder1.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[1].isHigh()) //Bac #2
                    {
                        panelBorder2.setBackground(Color.yellow);
                        if(bacActif != 2)
                        {
                            panelCenter2.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[1].isHigh());
                        panelBorder2.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[2].isHigh()) //Bac #3
                    {
                        panelBorder3.setBackground(Color.yellow);
                        if(bacActif != 3)
                        {
                            panelCenter3.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[2].isHigh());
                        panelBorder3.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[3].isHigh()) //Bac #4
                    {
                        panelBorder4.setBackground(Color.yellow);
                        if(bacActif != 4)
                        {
                            panelCenter4.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[3].isHigh());
                        panelBorder4.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[4].isHigh()) //Bac #5
                    {
                        panelBorder5.setBackground(Color.yellow);
                        if(bacActif != 5)
                        {
                            panelCenter5.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[4].isHigh());
                        panelBorder5.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[5].isHigh()) //Bac #6
                    {
                        panelBorder6.setBackground(Color.yellow);
                        if(bacActif != 6)
                        {
                            panelCenter6.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[5].isHigh());
                        panelBorder6.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[6].isHigh()) //Bac #7
                    {
                        panelBorder7.setBackground(Color.yellow);
                        if(bacActif != 7)
                        {
                            panelCenter7.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[6].isHigh());
                        panelBorder7.setBackground(Color.white);
                        resetBac();
                    }
                    else if(t_inputIOs[7].isHigh()) //Bac #8
                    {
                        panelBorder8.setBackground(Color.yellow);
                        if(bacActif != 8)
                        {
                            panelCenter8.setBackground(Color.red);
                            messageBaseJsonObj.put("Message", new String[] { "Ce n'est pas le bon bac!!!", "Allez au bac #" + bacActif});
                            m_listeObjList.add("Ce n'est pas le bon bac!!!");
                            m_listeObjList.add("Allez au bac #" + bacActif);
                        }
                        else
                        {
                            t_outputIOs[bacActif].low();
                            numPageCourante++;
                            messageBaseJsonObj.put("Message", "Poursuivez avec l'étape numéro " + numPageCourante);
                            m_listeObjList.add("Poursuivez avec l'étape numéro " + numPageCourante);
                        }
                        while(t_inputIOs[7].isHigh());
                        panelBorder8.setBackground(Color.white);
                        resetBac();
                    }
                    String DATA = messageBaseJsonObj.toString(2);
                    Buffer msgErreur = new AsciiBuffer(DATA);
                    UTF8Buffer topic = new UTF8Buffer(destination);
                    connectionThread.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);*/
                    
                    
                    Thread.sleep(750);
                }catch(Exception ex){}
            }
        }
    };
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        
        boolean etapeTermine = false; //Variable servant à confirmer que l'étape courante est bien terminé
        int varTampon = 0; //Garde en mémoire l'étape de production courante 
        
        String user = "admin";
        String password = "admin";
        String host = "127.0.0.1"; // Possiblement a modifier  192.168.137.171********************************************************************************************
        int port = Integer.parseInt("1883");
        final String destination = "/scal/scal_reponse_requete";

        JSONObject messageBaseJsonObj = new JSONObject();
        

        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        mqtt.setUserName(user);
        mqtt.setPassword(password);

        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        String TOPIC_REPONSE = "/scal/scal_requete_acces";

        // *********************Publishing du message d'initialisation******************* 
        //Note: Trouver comment mettre des accents*******************************************************************************************
        messageBaseJsonObj.put("Source", new String[] { "Base"});
        messageBaseJsonObj.put("Numetape", numPageCourante);
        messageBaseJsonObj.put("Message", new String[] { "Début de la communication", "Suivez attentivement les instructions de votre guide à l'étape indiquée"});
        String DATA = messageBaseJsonObj.toString(3);
        Buffer msg = new AsciiBuffer(DATA);

        UTF8Buffer topic = new UTF8Buffer(destination);
        connection.publish(topic, msg, QoS.AT_LEAST_ONCE, false);
        
        m_listeObjList.add("Début de la communication");
        m_listeObjList.add("Suivez attentivement les instructions de votre guide a l'étape indiquée");

        numPageCourante = 1;
        // **********************Préparation de la publication du message de sélection des bacs********************** 
        
        
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
            java.util.logging.Logger.getLogger(Interface_Usager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interface_Usager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interface_Usager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interface_Usager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Interface_Usager().setVisible(true);
                
            }
        });
        
        //<editor-fold defaultstate="collapsed" desc=" Initialisation des appareils Phidget ">
        try {
            //Enable server discovery to allow your program to find other Phidgets on the local network.
            Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);

            //Create your Phidget channels
            vInput0 = new VoltageInput(); //Capteur infrarouge du bac 1
            vInput1 = new VoltageInput(); //Capteur infrarouge du bac 1
            vInput2 = new VoltageInput(); //Capteur infrarouge du bac 2
            vInput3 = new VoltageInput(); //Capteur infrarouge du bac 2
            digitalOut0 = new DigitalOutput(); //LED du bac 1
            digitalOut1 = new DigitalOutput(); //LED du bac 1
            vRatioInput0 = new VoltageRatioInput(); //Capteur de poids du bac 1
            vRatioInput1 = new VoltageRatioInput(); //Capteur de poids du bac 2

            //Set addressing parameters to specify which channel to open (if any)
            vInput0.setHubPort(0);
            vInput0.setIsRemote(true);
            vInput0.setDeviceSerialNumber(597862);
            vInput0.setChannel(0);
            vInput1.setHubPort(0);
            vInput1.setIsRemote(true);
            vInput1.setDeviceSerialNumber(597862);
            vInput1.setChannel(1);
            vInput2.setHubPort(0);
            vInput2.setIsRemote(true);
            vInput2.setDeviceSerialNumber(597862);
            vInput2.setChannel(2);
            vInput3.setHubPort(0);
            vInput3.setIsRemote(true);
            vInput3.setDeviceSerialNumber(597862);
            vInput3.setChannel(3);
            digitalOut0.setHubPort(1);
            digitalOut0.setIsRemote(true);
            digitalOut0.setDeviceSerialNumber(597862);
            digitalOut0.setChannel(0);
            digitalOut1.setHubPort(1);
            digitalOut1.setIsRemote(true);
            digitalOut1.setDeviceSerialNumber(597862);
            digitalOut0.setChannel(1);
            vRatioInput0.setHubPort(2);
            vRatioInput0.setIsRemote(true);
            vRatioInput0.setDeviceSerialNumber(597862);
            vRatioInput0.setChannel(0);
            vRatioInput1.setHubPort(2);
            vRatioInput1.setIsRemote(true);
            vRatioInput1.setDeviceSerialNumber(597862);
            vRatioInput1.setChannel(1);

            //Assign any event handlers you need before calling open so that no events are missed.
            vInput0.addVoltageChangeListener(onBac1_VoltageChange);
            vInput1.addVoltageChangeListener(onBac1_VoltageChange);
            vInput2.addVoltageChangeListener(onBac2_VoltageChange);
            vInput3.addVoltageChangeListener(onBac2_VoltageChange);

            //Open your Phidgets and wait for attachment
            vInput0.open(5000);
            vInput1.open(5000);
            //vInput2.open(5000);
            //vInput3.open(5000);
            //digitalOut0.open(5000);
            vRatioInput0.open(5000);
            vRatioInput1.open(5000);

            vInput0.setVoltageChangeTrigger(TRIGGER_INFRAROUGE);
            vInput1.setVoltageChangeTrigger(TRIGGER_INFRAROUGE);
            vInput2.setVoltageChangeTrigger(TRIGGER_INFRAROUGE);
            vInput3.setVoltageChangeTrigger(TRIGGER_INFRAROUGE);
            vRatioInput0.setBridgeGain(BridgeGain.GAIN_128X);
            vRatioInput1.setBridgeGain(BridgeGain.GAIN_128X);

        } catch (PhidgetException ex) {
            //We will catch Phidget Exceptions here, and print the error informaiton.
            ex.printStackTrace();
            System.out.println("");
            System.out.println("PhidgetException " + ex.getErrorCode() + " (" + ex.getDescription() + "): " + ex.getDetail());
        }
        //</editor-fold>
        
        /*
        //Séquence test des GPIO's
        System.out.println(t_inputIOs[2].isHigh());
        System.out.println(t_inputIOs[2].isLow());
        System.out.println(t_inputIOs[3].isHigh());
        System.out.println(t_inputIOs[3].isLow());

        System.out.println(t_outputIOs[0].isHigh());
        System.out.println(t_outputIOs[0].isLow());
        System.out.println(t_outputIOs[3].isHigh());
        System.out.println(t_outputIOs[3].isLow());

        t_outputIOs[0].high();
        t_outputIOs[1].low();
        t_outputIOs[2].low();
        t_outputIOs[3].low();

        System.out.println(t_outputIOs[0].isHigh());
        System.out.println(t_outputIOs[1].isHigh());
        System.out.println(t_outputIOs[2].isHigh());
        System.out.println(t_outputIOs[3].isHigh());
        */
        
        Thread myThread = new Thread(runnable);
        myThread.start();
        
        while(true)
        {
            Topic[] topics = {new Topic(TOPIC_REPONSE, QoS.AT_LEAST_ONCE)};
            connection.subscribe(topics);

            exit = false;
            while(!exit)
            {
                try
                {
                    Message message = connection.receive(150, TimeUnit.SECONDS);
                    if (message.getPayload() != null) {
                        Str = new String(message.getPayload());
                        JSONObject messageJsonObject = new JSONObject(Str);
                        
                        choixProduit = messageJsonObject.getInt("Produit"); //Choix du produit
                        choixBase = messageJsonObject.getInt("Base");    //Choix de la base
                        choixCouleur = messageJsonObject.getInt("Couleur"); //Choix de la couleur
                        choixCrayon = messageJsonObject.getInt("Crayon");  //Choix porte Crayon
                        choixSupports = messageJsonObject.getInt("Supports"); //Choix des supports
                        quantite = messageJsonObject.getInt("Quantite");     //Quantite
                        
                        message = null;
                        
                        numPageCourante = 1;
                        
                        //<editor-fold defaultstate="collapsed" desc=" Initialisation des infos de la commandes (optional) ">
                        if(choixProduit == 1) //Porte-cellulaire
                        {
                            produit = "Porte-cellulaire";
                        }
                        else if(choixProduit == 2) //Porte-carte d'affaire
                        {
                            produit = "Porte-carte d'affaire";
                        }
                        if(choixBase == 1) //Base type 1
                        {
                            multiCrayon = false;
                            base = "Base de type 1 ";
                        }
                        else if(choixBase == 2) //Base type 2
                        {
                            multiCrayon = true;
                            base = "Base de type 2 ";
                        }
                        if(choixCouleur == 1)
                        {
                            couleur = "couleur blanche ";
                        }
                        else if(choixCouleur == 2)
                        {
                            couleur = "couleur noir ";
                        }
                        if(multiCrayon){
                            qteCrayon = "deux ";
                        }
                        else{
                            qteCrayon = "un seul ";
                        }  
                        switch (choixCrayon) {
                            case 1:
                                crayon = "Porte-crayon de type 1 ";
                                break;
                            case 2:
                                crayon = "Porte-crayon de type 2 ";
                                break;
                            case 3:
                                crayon = "Porte-crayon de type 3 ";
                                break;
                            default:
                                break;
                        }
                        if(choixSupports == 1) //Supports courts
                        {
                            supports = "Supports courts ";
                        }
                        else if(choixSupports == 2) //Supports moyens
                        {
                            supports = "Supports moyens ";
                        }
                        else //Aucun support
                        {
                            supports = "Aucun support ";
                        }
                        //</editor-fold>

                        while(i < quantite)
                        {
                            switch (numPageCourante) {
                                case 1:
                                    if (varTampon != 1 && varTampon != numPageCourante) {
                                        etapeTermine = true;
                                        resetBac();
                                        varTampon = numPageCourante;
                                    }
                                    messageBaseJsonObj.put("Numetape", numPageCourante);
                                    if(choixBase == 1) //Base type 1
                                    {
                                        if(choixCouleur == 1)
                                        {
                                            bacActif = enumBacs.baseType1Couleur1.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", baseProd(base,couleur,bacActif));
                                            }
                                        }
                                        else if(choixCouleur == 2)
                                        {
                                            bacActif = enumBacs.baseType1Couleur2.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                messageBaseJsonObj.put("Message", baseProd(base,couleur,bacActif));
                                            }
                                        }
                                    }
                                    else if(choixBase == 2) //Base type 2
                                    {
                                        if(choixCouleur == 1)
                                        {
                                            bacActif = enumBacs.baseType2Couleur1.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", baseProd(base,couleur,bacActif));
                                            }
                                        }
                                        else if(choixCouleur == 2)
                                        {
                                            bacActif = enumBacs.baseType2Couleur2.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", baseProd(base,couleur,bacActif));
                                            }
                                        }
                                    }   break;
                                case 2:
                                    if (varTampon != 2 && varTampon != numPageCourante) {
                                        etapeTermine = true;
                                        resetBac();
                                        varTampon = numPageCourante;
                                    }
                                    messageBaseJsonObj.put("Numetape", numPageCourante);
                                      
                                    switch (choixCrayon) {
                                    //Porte-crayon type 1
                                        case 1:
                                            bacActif = enumBacs.crayonType1.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                messageBaseJsonObj.put("Message", crayonProd(qteCrayon,crayon,bacActif));
                                            }
                                            break;
                                    //Porte-crayon type 2
                                        case 2:
                                            bacActif = enumBacs.crayonType2.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                messageBaseJsonObj.put("Message", crayonProd(qteCrayon,crayon,bacActif));
                                            }
                                            break;
                                    //Porte-crayon type 3
                                        case 3:
                                            bacActif = enumBacs.crayonType3.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", crayonProd(qteCrayon,crayon,bacActif));
                                            }
                                            break;
                                        default:
                                            break;
                                    }   break;
                                case 3:
                                    messageBaseJsonObj.put("Numetape", numPageCourante);
                                    if (varTampon != 3 && varTampon != numPageCourante) {
                                        etapeTermine = true;
                                        resetBac();
                                        varTampon = numPageCourante;
                                    }
                                    switch (choixSupports) {
                                    //Supports courts
                                        case 1:
                                            bacActif = enumBacs.supportCourt.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", supportsProd(supports,bacActif));
                                            }
                                            break;
                                    //Supports moyen
                                        case 2:
                                            bacActif = enumBacs.supportMoyen.ordinal();
                                            detectBac(bacActif);
                                            if (etapeTermine){
                                                
                                                messageBaseJsonObj.put("Message", supportsProd(supports,bacActif));
                                            }
                                            break;
                                    //Aucun support
                                        default:
                                            if (etapeTermine) {
                                                messageBaseJsonObj.put("Message", new String[] { "Ce produit ne comporte " + supports,
                                                "Assurez-vous que le produit est bien assemblé et passez au prochain produit"});
                                                m_listeObjList.add("Ce produit ne comporte aucun support");
                                                m_listeObjList.add("Assurez-vous que le produit est bien assemblé et passez au prochain produit");
                                            }
                                            
                                            break;
                                    }   break;
                                    case 4:
                                    if (varTampon != 4 && varTampon != numPageCourante) {
                                        resetBac();
                                        i++;
                                        varTampon = numPageCourante;
                                        numPageCourante = 1;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (etapeTermine) {
                                etapeTermine = false;
                                rechargeListe = true;
                                DATA = messageBaseJsonObj.toString(2);
                                Buffer msgBacs = new AsciiBuffer(DATA);
                                connection.publish(topic, msgBacs, QoS.AT_LEAST_ONCE, false);
                            }
                        }
                    }
                    
                    if (varTampon == 4) {
                        i = 0;
                        exit = true;
                    }
                }
                catch(Exception e)
                {

                }
            }
        }
    }
    
    /*
    @brief: Génère une suite d'instructions selon le type de base demandé.
    @variables: iBase: Contient le type de base à utiliser
                iCouleur: Contient la couleur du produit
                iBacActif: Contient le numéro du bac actuellement en cours d'utilisation
                etape: Contient l'étape courante d'assemblage
    */
    private static String[] baseProd(String iBase, String iCouleur, int iBacActif)
    {
        String[] statement = new String[] { "Prenez la " + iBase + "de " + iCouleur + "se trouvant dans le bac #" + iBacActif};
        m_listeObjList.add("Prenez la " + iBase + "de " + iCouleur + "se trouvant dans le bac #" + iBacActif);
        return statement;
    }
    
    /*
    @brief: Génère une suite d'instructions selon le type de base demandé.
    @variables: iqteCrayon: Contient la quantité de port-crayon à utiliser
                iCrayon: Contient le type de porte-crayon à utiliser
                iBacActif: Contient le numéro du bac actuellement en cours d'utilisation
                etape: Contient l'étape courante d'assemblage
    */
    private static String[] crayonProd(String iqteCrayon, String iCrayon, int iBacActif)
    {
        String[] statement = new String[] { "Prenez " + iqteCrayon + iCrayon + "dans le bac #" + iBacActif};
        m_listeObjList.add("Prenez " + iqteCrayon + iCrayon + "dans le bac #" + iBacActif);
        return statement;
    }
    
    /*
    @brief: Génère une suite d'instructions selon le type de supports demandé.
    @variables: iSupports: Contient le type de supports à utiliser
                iBacActif: Contient le numéro du bac actuellement en cours d'utilisation
    */
    private static String[] supportsProd(String iSupports, int iBacActif)
    {
        String[] statement = new String[] { "Prenez deux " + iSupports + "dans le bac #" + iBacActif, "Assurez-vous que le produit est bien assemblé et passez au prochain produit"};
        m_listeObjList.add("Prenez deux " + iSupports + "dans le bac #" + iBacActif);
        m_listeObjList.add("Assurez-vous que le produit est bien assemblé et passez au prochain produit");
        return statement;
    }
    
    /*
    @brief: Met en évidence le bac nécessaire à l'accomplissement de l'étape en cours.
    @variables: iBacActif: Contient le numéro du bac actuellement en cours d'utilisation
    */
    private static void detectBac(int iBacActif)
    {
        switch (iBacActif) {
        //Bac #1
            case 1:
                panelCenter1.setBackground(Color.green);
                break;
        //Bac #2
            case 2:
                panelCenter2.setBackground(Color.green);
                break;
        //Bac #3
            case 3:
                panelCenter3.setBackground(Color.green);
                break;
        //Bac #4
            case 4:
                panelCenter4.setBackground(Color.green);
                break;
        //Bac #5
            case 5:
                panelCenter5.setBackground(Color.green);
                break;
        //Bac #6
            case 6:
                panelCenter6.setBackground(Color.green);
                break;
        //Bac #7
            case 7:
                panelCenter7.setBackground(Color.green);
                break;
        //Bac #8
            case 8:
                panelCenter8.setBackground(Color.green);
                break;
            default:
                break;
        }
    }
    
    /*
    @brief: Remet les bacs à leur état normale suite à l'accomplissement de l'étape en cours.
    @variables: Aucun
    */
    private static void resetBac()
    {
        panelCenter1.setBackground(Color.white);
        panelCenter2.setBackground(Color.white);
        panelCenter3.setBackground(Color.white);
        panelCenter4.setBackground(Color.white);
        panelCenter5.setBackground(Color.white);
        panelCenter6.setBackground(Color.white);
        panelCenter7.setBackground(Color.white);
        panelCenter8.setBackground(Color.white);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button_Next;
    private java.awt.Button button_Previous;
    private java.awt.Label label1;
    private java.awt.Label label10;
    private java.awt.Label label11;
    private java.awt.Label label12;
    private java.awt.Label label13;
    private java.awt.Label label14;
    private java.awt.Label label15;
    private java.awt.Label label16;
    private java.awt.Label label17;
    private java.awt.Label label18;
    private java.awt.Label label19;
    private java.awt.Label label2;
    private java.awt.Label label20;
    private java.awt.Label label21;
    private java.awt.Label label22;
    private java.awt.Label label23;
    private java.awt.Label label24;
    private java.awt.Label label25;
    private java.awt.Label label26;
    private java.awt.Label label27;
    private java.awt.Label label28;
    private java.awt.Label label29;
    private java.awt.Label label3;
    private java.awt.Label label30;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.Label label7;
    private java.awt.Label label8;
    private java.awt.Label label9;
    private static java.awt.List list1;
    private java.awt.Panel panel1;
    private java.awt.Panel panel10;
    private java.awt.Panel panel11;
    private java.awt.Panel panel12;
    private java.awt.Panel panel13;
    private java.awt.Panel panel2;
    private java.awt.Panel panel3;
    private java.awt.Panel panel4;
    private java.awt.Panel panel5;
    private java.awt.Panel panel6;
    private java.awt.Panel panel7;
    private java.awt.Panel panel8;
    private java.awt.Panel panel9;
    private static java.awt.Panel panelBorder1;
    private static java.awt.Panel panelBorder10;
    private static java.awt.Panel panelBorder2;
    private static java.awt.Panel panelBorder3;
    private static java.awt.Panel panelBorder4;
    private static java.awt.Panel panelBorder5;
    private static java.awt.Panel panelBorder6;
    private static java.awt.Panel panelBorder7;
    private static java.awt.Panel panelBorder8;
    private static java.awt.Panel panelBorder9;
    private static java.awt.Panel panelCenter1;
    private static java.awt.Panel panelCenter10;
    private static java.awt.Panel panelCenter2;
    private static java.awt.Panel panelCenter3;
    private static java.awt.Panel panelCenter4;
    private static java.awt.Panel panelCenter5;
    private static java.awt.Panel panelCenter6;
    private static java.awt.Panel panelCenter7;
    private static java.awt.Panel panelCenter8;
    private static java.awt.Panel panelCenter9;
    private static java.awt.TextField tbBase;
    private static java.awt.TextField tbCouleur;
    private static java.awt.TextField tbCrayon;
    private static java.awt.TextField tbEtapes;
    private static java.awt.TextField tbPoids_Bac1;
    private static java.awt.TextField tbPoids_Bac2;
    private static java.awt.TextField tbPoids_Bac3;
    private static java.awt.TextField tbPoids_Bac4;
    private static java.awt.TextField tbPoids_Bac5;
    private static java.awt.TextField tbPoids_Bac6;
    private static java.awt.TextField tbPoids_Bac7;
    private static java.awt.TextField tbPoids_Bac8;
    private static java.awt.TextField tbPoids_Bac9;
    private static java.awt.TextField tbProduit;
    private static java.awt.TextField tbQuantite;
    private static java.awt.TextField tbSupports;
    private java.awt.TextField textField1;
    private java.awt.TextField textField10;
    private java.awt.TextField textField2;
    private java.awt.TextField textField3;
    private java.awt.TextField textField4;
    private java.awt.TextField textField5;
    private java.awt.TextField textField6;
    private java.awt.TextField textField7;
    private java.awt.TextField textField8;
    private java.awt.TextField textField9;
    // End of variables declaration//GEN-END:variables


    
    
}
