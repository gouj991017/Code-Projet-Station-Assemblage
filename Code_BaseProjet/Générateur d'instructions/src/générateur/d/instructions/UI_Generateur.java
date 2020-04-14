package générateur.d.instructions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.JFileChooser;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.*;
import org.fusesource.mqtt.client.*;
import org.fusesource.hawtbuf.*;
import org.json.JSONArray;

/**
 *
 * @author Guim
 */
public class UI_Generateur extends javax.swing.JFrame
{
    //Variables globales
    List<Etape> l_Etape = new ArrayList<Etape>();   //Liste des étapes.
    List<Piece> l_Piece = new ArrayList<Piece>();   //Liste des pièces.
    boolean modif = false;
    int nb_etapes = 0;

    /** Creates new form UI_Generateur */
    public UI_Generateur() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jEtape = new javax.swing.JDialog();
        tb_numEtape = new javax.swing.JTextField();
        tb_nomEtape = new javax.swing.JTextField();
        tb_nbPieces = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_message = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cb_nomPieceEtape = new javax.swing.JComboBox<>();
        b_okEtape = new javax.swing.JButton();
        jPiece = new javax.swing.JDialog();
        tb_nomPiece = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tb_poidPiece = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tb_numBac = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        b_okPiece = new javax.swing.JButton();
        jOuvrir_Fichier = new javax.swing.JFileChooser();
        jSauver_Fichier = new javax.swing.JFileChooser();
        b_nouvEtape = new javax.swing.JButton();
        b_modifEtape = new javax.swing.JButton();
        b_suppEtape = new javax.swing.JButton();
        b_nouvPieces = new javax.swing.JButton();
        b_modifPieces = new javax.swing.JButton();
        b_suppPieces = new javax.swing.JButton();
        lb_etapes = new java.awt.List();
        lb_pieces = new java.awt.List();
        jMenuBar1 = new javax.swing.JMenuBar();
        m_fichier = new javax.swing.JMenu();
        b_enregistrer = new javax.swing.JMenuItem();
        b_ouvrir = new javax.swing.JMenuItem();
        b_quitter = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jEtape.setMinimumSize(new java.awt.Dimension(350, 360));

        tb_numEtape.setEditable(false);

        tb_message.setColumns(20);
        tb_message.setRows(5);
        jScrollPane1.setViewportView(tb_message);

        jLabel1.setText("Numéro d'étape");

        jLabel2.setText("Nom de l'étape");

        jLabel3.setText("Nom de la pièce");

        jLabel4.setText("Nombre de pièces / poid (g) à retirer");

        jLabel5.setText("Message");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel6.setText("Étape");

        b_okEtape.setText("OK");
        b_okEtape.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_okEtapeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jEtapeLayout = new javax.swing.GroupLayout(jEtape.getContentPane());
        jEtape.getContentPane().setLayout(jEtapeLayout);
        jEtapeLayout.setHorizontalGroup(
            jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEtapeLayout.createSequentialGroup()
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jEtapeLayout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(jLabel6))
                    .addGroup(jEtapeLayout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(b_okEtape, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(129, Short.MAX_VALUE))
            .addGroup(jEtapeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jEtapeLayout.createSequentialGroup()
                        .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tb_nomEtape)
                            .addComponent(cb_nomPieceEtape, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(90, 90, 90))
                    .addGroup(jEtapeLayout.createSequentialGroup()
                        .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jEtapeLayout.createSequentialGroup()
                                .addComponent(tb_numEtape, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1))
                            .addGroup(jEtapeLayout.createSequentialGroup()
                                .addComponent(tb_nbPieces, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jEtapeLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jEtapeLayout.setVerticalGroup(
            jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEtapeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_numEtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_nomEtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cb_nomPieceEtape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_nbPieces, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jEtapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(b_okEtape)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPiece.setMinimumSize(new java.awt.Dimension(270, 225));

        jLabel7.setText("Poid (g) de la pièce si applicable");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel8.setText("Pièce");

        jLabel9.setText("Nom de la pièce");

        jLabel10.setText("Numéro du bac associé");

        b_okPiece.setText("OK");
        b_okPiece.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_okPieceMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPieceLayout = new javax.swing.GroupLayout(jPiece.getContentPane());
        jPiece.getContentPane().setLayout(jPieceLayout);
        jPieceLayout.setHorizontalGroup(
            jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPieceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPieceLayout.createSequentialGroup()
                        .addComponent(tb_poidPiece, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addGroup(jPieceLayout.createSequentialGroup()
                        .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(tb_nomPiece, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPieceLayout.createSequentialGroup()
                        .addComponent(tb_numBac, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPieceLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(b_okPiece, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPieceLayout.setVerticalGroup(
            jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPieceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(3, 3, 3)
                .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_nomPiece, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_poidPiece, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPieceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_numBac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(b_okPiece)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jOuvrir_Fichier.setApproveButtonText("Ouvrir");
        jOuvrir_Fichier.setToolTipText("Séléctionnez le fichier d'instructions à ouvrir");

        jSauver_Fichier.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        jSauver_Fichier.setApproveButtonText("Enregistrer");
        jSauver_Fichier.setToolTipText("Enregistrez le fichier d'instruction");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        b_nouvEtape.setText("Nouvelle étape");
        b_nouvEtape.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_nouvEtapeMouseClicked(evt);
            }
        });

        b_modifEtape.setText("Modifier");
        b_modifEtape.setEnabled(false);
        b_modifEtape.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_modifEtapeMouseClicked(evt);
            }
        });

        b_suppEtape.setText("Supprimer étape");
        b_suppEtape.setEnabled(false);
        b_suppEtape.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_suppEtapeMouseClicked(evt);
            }
        });

        b_nouvPieces.setText("Nouvelle pièces");
        b_nouvPieces.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_nouvPiecesMouseClicked(evt);
            }
        });

        b_modifPieces.setText("Modifier");
        b_modifPieces.setEnabled(false);
        b_modifPieces.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_modifPiecesMouseClicked(evt);
            }
        });

        b_suppPieces.setText("Supprimer pièces");
        b_suppPieces.setEnabled(false);
        b_suppPieces.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b_suppPiecesMouseClicked(evt);
            }
        });

        lb_etapes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_etapesMouseClicked(evt);
            }
        });

        lb_pieces.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_piecesMouseClicked(evt);
            }
        });

        m_fichier.setText("Fichier");

        b_enregistrer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        b_enregistrer.setText("Enregistrer");
        b_enregistrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_enregistrerActionPerformed(evt);
            }
        });
        m_fichier.add(b_enregistrer);

        b_ouvrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        b_ouvrir.setText("Ouvrir");
        b_ouvrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_ouvrirActionPerformed(evt);
            }
        });
        m_fichier.add(b_ouvrir);

        b_quitter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        b_quitter.setText("Quitter");
        b_quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_quitterActionPerformed(evt);
            }
        });
        m_fichier.add(b_quitter);

        jMenuBar1.add(m_fichier);

        jMenu2.setText("Option");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lb_etapes, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addComponent(lb_pieces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(b_nouvEtape, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(b_modifEtape, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(b_suppEtape, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(b_nouvPieces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(b_modifPieces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(b_suppPieces, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(b_nouvPieces)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b_modifPieces)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b_suppPieces)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_pieces, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(b_nouvEtape)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b_modifEtape)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b_suppEtape))
                    .addComponent(lb_etapes, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void b_nouvPiecesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_nouvPiecesMouseClicked
        jPiece.setVisible(true);    //Affichage de la fenêtre d'édition de pièce.
    }//GEN-LAST:event_b_nouvPiecesMouseClicked

    private void b_nouvEtapeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_nouvEtapeMouseClicked
        tb_numEtape.setText(String.valueOf(nb_etapes+1));
        chargeCb_NomPieceEtape();   //Charge les éléments de cb_NomPieceEtape.
        jEtape.setVisible(true);    //Affichage de la fenêtre d'édition d'étape.
    }//GEN-LAST:event_b_nouvEtapeMouseClicked

    private void b_okEtapeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_okEtapeMouseClicked
        if(!modif)
        {
            try
            {
            l_Etape.add(new Etape(Integer.parseInt(tb_numEtape.getText()), tb_nomEtape.getText(), tb_message.getText(), cb_nomPieceEtape.getSelectedIndex(), Integer.parseInt(tb_nbPieces.getText())));   //Récupération des infos sur l'étape.
            nb_etapes++;
            lb_etapes.add(nb_etapes + " - " + tb_nomEtape.getText());
            }catch(Exception ex){System.out.println("[Erreur] Impossible de créer la nouvelle étape: " + ex.getMessage());} //Message d'erreur.
        }
        else
        {
            int index = lb_etapes.getSelectedIndex();
            l_Etape.get(index).modifEtape(tb_nomEtape.getText(), tb_message.getText(), cb_nomPieceEtape.getSelectedIndex(), Integer.parseInt(tb_nbPieces.getText())); //Récupération des infos sur l'étape.
            lb_etapes.remove(index);    //On retire l'étape de la liste pour la remetre au même index
            lb_etapes.add(l_Etape.get(index).numero + " - " + tb_nomEtape.getText(), index);
            modif = false;  //Remise à défaut de modif.
        }
        jEtape.setVisible(false);   //Fermeture de la boîte de dialogue
        b_modifEtape.setEnabled(false);
        b_suppEtape.setEnabled(false);
        videDialogEtape();  //Éfface les paramètres entrés.
    }//GEN-LAST:event_b_okEtapeMouseClicked

    private void lb_etapesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_etapesMouseClicked
        try //Test d'obtention d'un index.
        {
            int test = lb_etapes.getSelectedIndex();    //Si aucun items n'est séléctionné, l'index (test) vaut -1.
            if(!(test<0))   //si un item est séléctionné, on permet de modifier ou suprimmer.
            {
                b_modifEtape.setEnabled(true);
                b_suppEtape.setEnabled(true);
            }
        }catch(Exception ex){System.out.println("[Erreur] Aucun élément selectionné?");}
    }//GEN-LAST:event_lb_etapesMouseClicked

    private void b_modifEtapeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_modifEtapeMouseClicked
        chargeDialogEtape();    //Charge les paramètres de l'étape séléctionnée.
        jEtape.setVisible(true);    //Affichage de la fenêtre d'édition d'étape.
        modif = true;
    }//GEN-LAST:event_b_modifEtapeMouseClicked

    private void b_suppEtapeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_suppEtapeMouseClicked
        int index = lb_etapes.getSelectedIndex();
        lb_etapes.remove(index); //On retire l'étape de la liste.
        l_Etape.remove(index);
        for(int i = index; i < l_Etape.size(); i++) //Tant qu'il y a un élément suivant, on change son numéro.
        {
            l_Etape.get(i).numero--;
            //mise à jour du numéro d'étape affiché.
            lb_etapes.remove(i);    //On retire l'étape de la liste pour la remetre au même index
            lb_etapes.add(l_Etape.get(i).numero + " - " + l_Etape.get(i).nom, i);
        }
        nb_etapes--;
        b_modifEtape.setEnabled(false);
        b_suppEtape.setEnabled(false);
    }//GEN-LAST:event_b_suppEtapeMouseClicked

    private void b_modifPiecesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_modifPiecesMouseClicked
        chargeDialogPiece();    //Charge les paramètres de la pièce séléctionnée.
        jPiece.setVisible(true);    //Affichage de la fenêtre d'édition de pièces.
        modif = true;
    }//GEN-LAST:event_b_modifPiecesMouseClicked

    private void b_suppPiecesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_suppPiecesMouseClicked
        int index = lb_pieces.getSelectedIndex();
        lb_pieces.remove(index); //On retire la pièce de la liste.
        l_Piece.remove(index);
        b_modifPieces.setEnabled(false);
        b_suppPieces.setEnabled(false);
    }//GEN-LAST:event_b_suppPiecesMouseClicked

    private void b_okPieceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b_okPieceMouseClicked
        if(!modif)
        {
            try
            {
            l_Piece.add(new Piece(l_Piece.size()+1, Integer.parseInt(tb_numBac.getText()), Double.parseDouble(tb_poidPiece.getText()), tb_nomPiece.getText()));   //Récupération des infos sur la pièce.
            lb_pieces.add(tb_nomPiece.getText());   //Ajout de l'objet à la liste.
            }catch(Exception ex){System.out.println("[Erreur] Impossible de créer la nouvelle pièce: " + ex.getMessage());} //Message d'erreur.
        }
        else
        {
            int index = lb_pieces.getSelectedIndex();
            l_Piece.get(index).modifPiece(Integer.parseInt(tb_numBac.getText()), Double.parseDouble(tb_poidPiece.getText()), tb_nomPiece.getText());    //Récupération des infos sur la pièce.
            lb_pieces.remove(index);    //On retire la pièce de la liste pour la remetre au même index
            lb_pieces.add(tb_nomPiece.getText(), index);
            modif = false;  //Remise à défaut de modif.
        }
        jPiece.setVisible(false);   //Fermeture de la boîte de dialogue
        b_modifPieces.setEnabled(false);
        b_suppPieces.setEnabled(false);
        videDialogPiece();  //Éfface les paramètres entrés.
    }//GEN-LAST:event_b_okPieceMouseClicked

    private void lb_piecesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_piecesMouseClicked
        try //Test d'obtention d'un index.
        {
            int test = lb_pieces.getSelectedIndex();    //Si aucun items n'est séléctionné, l'index (test) vaut -1.
            if(!(test<0))   //si un item est séléctionné, on permet de modifier ou suprimmer.
            {
                b_modifPieces.setEnabled(true);
                b_suppPieces.setEnabled(true);
            }
        }catch(Exception ex){System.out.println("[Erreur] Aucun élément selectionné?");}
    }//GEN-LAST:event_lb_piecesMouseClicked

    private void b_quitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_quitterActionPerformed
        System.exit(0); //Fin du programme
    }//GEN-LAST:event_b_quitterActionPerformed

    private void b_enregistrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_enregistrerActionPerformed
        //Extrait de code inspiré de stackoverflow: https://stackoverflow.com/questions/13905298/how-to-save-a-txt-file-using-jfilechooser
        int returnVal = jSauver_Fichier.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File fileToSave = jSauver_Fichier.getSelectedFile();
            try
            {
                JSONObject joSave = new JSONObject();
                JSONArray jaEtapes = new JSONArray();
                JSONArray jaPieces = new JSONArray();
                PrintWriter pwSave = new PrintWriter(new File(fileToSave+""));
                //Insertion en JSON.
                for(int i = 0; i < l_Etape.size(); i++)
                {
                    pwSave.write(l_Etape.get(i).toJSON().toString()+" \r\n");   //Insère tous les éléments des étapes dans le JSON
                }
                for(int i = 0; i < l_Piece.size(); i++)
                {
                    pwSave.write(l_Piece.get(i).toJSON().toString()+" \r\n");   //Insère tous les éléments des pièces dans le JSON
                }                
                
                pwSave.close(); //Fermeture du fichier.
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            }catch(Exception ex){System.out.println("[Erreur] Écriture du fichier impossible: "+ex.getMessage());}  //Message d'erreur.
        }
    }//GEN-LAST:event_b_enregistrerActionPerformed

    private void b_ouvrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_ouvrirActionPerformed
        //Extrait de code inspiré de la documentation de Netbeans: https://netbeans.org/kb/docs/java/gui-filechooser.html
        int returnVal = jOuvrir_Fichier.showOpenDialog(this);
        File fichierCharge;
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            fichierCharge = jOuvrir_Fichier.getSelectedFile();
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
                    lb_etapes.removeAll();
                    while(true)
                    {
                        jotemp = new JSONObject(br.readLine());   //Extraction du JSON des étapes.
                        l_Etape.add(new Etape(jotemp.getInt("numero"), jotemp.getString("nom"), jotemp.getString("message"), jotemp.getInt("num_piece"), jotemp.getInt("nb_pieces")));   //Ajout de l'étape.
                        lb_etapes.add(jotemp.getInt("numero") + " - " +jotemp.getString("nom"));    //Affiche l'étape sur l'interface.
                        nb_etapes++;
                        jotemp = new JSONObject();  //On efface le contenu du JSON.
                    }
                }catch(Exception ex){redondance = true;}    //Si la boucle échoue, on active la redondance.
                
                try //Extraction des pièces du JSON vers la liste.
                {
                    l_Piece.clear();
                    lb_pieces.removeAll();
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
                        lb_pieces.add(jotemp.getString("nom")); //Affiche la pièce sur l'interface.
                    }
                }catch(Exception ex){}
                //Fermeture du fichier.
                br.close();
                frLoad.close();
            }catch (Exception ex){System.out.println("[Erreur] Lecture du fichier impossible: "+ex.getMessage());}  //Message d'erreur.
        }
    }//GEN-LAST:event_b_ouvrirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
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
            java.util.logging.Logger.getLogger(UI_Generateur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UI_Generateur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UI_Generateur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI_Generateur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new UI_Generateur().setVisible(true);
            }
        });
    }
    
    private void videDialogEtape()
    {
        tb_message.setText("");
        tb_nbPieces.setText("");
        tb_nomEtape.setText("");
        tb_numEtape.setText("");
        cb_nomPieceEtape.removeAllItems();  //Supprime tous les éléments
    }
    
    private void chargeDialogEtape()
    {
        int index = lb_etapes.getSelectedIndex();
        tb_message.setText(l_Etape.get(index).message);
        tb_nbPieces.setText(String.valueOf(l_Etape.get(index).nb_pieces));
        tb_nomEtape.setText(l_Etape.get(index).nom);
        tb_numEtape.setText(String.valueOf(l_Etape.get(index).numero));
        chargeCb_NomPieceEtape();   //Charge les éléments de cb_NomPieceEtape.
        cb_nomPieceEtape.setSelectedIndex(l_Etape.get(index).num_piece);
    }
    
    private void videDialogPiece()
    {
        tb_nomPiece.setText("");
        tb_poidPiece.setText("");
        tb_numBac.setText("");
    }
    
    private void chargeDialogPiece()
    {
        int index = lb_pieces.getSelectedIndex();
        tb_nomPiece.setText(l_Piece.get(index).nom);
        tb_poidPiece.setText(String.valueOf(l_Piece.get(index).poid));
        tb_numBac.setText(String.valueOf(l_Piece.get(index).n_bac));
    }

    private void chargeCb_NomPieceEtape()
    {
        for (int i=0; i<l_Piece.size(); i++)
        {
            cb_nomPieceEtape.addItem(l_Piece.get(i).nom);
            l_Piece.get(i).numero = i;  //Mise à jour du numéro de la pièce. 
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem b_enregistrer;
    private javax.swing.JButton b_modifEtape;
    private javax.swing.JButton b_modifPieces;
    private javax.swing.JButton b_nouvEtape;
    private javax.swing.JButton b_nouvPieces;
    private javax.swing.JButton b_okEtape;
    private javax.swing.JButton b_okPiece;
    private javax.swing.JMenuItem b_ouvrir;
    private javax.swing.JMenuItem b_quitter;
    private javax.swing.JButton b_suppEtape;
    private javax.swing.JButton b_suppPieces;
    private javax.swing.JComboBox<String> cb_nomPieceEtape;
    private javax.swing.JDialog jEtape;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JFileChooser jOuvrir_Fichier;
    private javax.swing.JDialog jPiece;
    private javax.swing.JFileChooser jSauver_Fichier;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.List lb_etapes;
    private java.awt.List lb_pieces;
    private javax.swing.JMenu m_fichier;
    private javax.swing.JTextArea tb_message;
    private javax.swing.JTextField tb_nbPieces;
    private javax.swing.JTextField tb_nomEtape;
    private javax.swing.JTextField tb_nomPiece;
    private javax.swing.JTextField tb_numBac;
    private javax.swing.JTextField tb_numEtape;
    private javax.swing.JTextField tb_poidPiece;
    // End of variables declaration//GEN-END:variables

}
