package générateur.d.instructions;
import org.json.JSONObject;
/**
 * @author Guillaume Beaudoin
 * @brief Classe pour l'objet "Piece" représantant une pièce et ses informations.
 * @version 1.0
 */
public class Piece
{
    //Variables membres
    int numero;
    int n_bac;
    double poid;
    String nom = "";
    //Constructeur
    public Piece(int _numero, int _n_bac, double _poid, String _nom)  //Constructeur
    {
        numero = _numero;
        n_bac = _n_bac;
        poid = _poid;
        nom = _nom;
    }
    
    /*
    @brief: Modifie les paramètres de la pièce.
    @param: _n_bac: numéro de bac associé à la pièce
            _poid: poid de la pièce
            _nom: nom de la pièce
    */
    public void modifPiece(int _n_bac, double _poid, String _nom)
    {
        n_bac = _n_bac;
        poid = _poid;
        nom = _nom;
    }
    
    /*
    @brief: Retourne l'objet JSON représentant l'objet.
    @param: aucun
    @return:(JSONObject)
    */
    public JSONObject toJSON()
    {
        JSONObject jo = new JSONObject();
        jo.put("nom", nom);
        jo.put("numero", numero);
        jo.put("n_bac", n_bac);
        jo.put("poid", poid);
        return jo;
    }
}
