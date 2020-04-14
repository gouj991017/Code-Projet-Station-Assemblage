/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package terminal_base;

/**
 *
 * @author Guillaume Beaudoin
 */
class ObjCommande
{   //Variables membres
    int typeBase_;
    int typeCrayon_;
    int qteCrayon_;
    int typeSupport_;
    String couleurBase_;
    int qte_;
    
    //Constructeur.
    ObjCommande(int qte, int typeBase, int typeCrayon, int typeSupport, String couleurBase, int qteCrayon)
    {   //Association des valeurs de variables.
        qte_ = qte;
        typeBase_ = typeBase;
        typeCrayon_ = typeCrayon;
        qteCrayon_ = qteCrayon;
        typeSupport_ = typeSupport;
        couleurBase_ = couleurBase;
    }
}
