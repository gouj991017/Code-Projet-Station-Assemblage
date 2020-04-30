package générateur.d.instructions;
import org.json.*;
/**
 * @author Guillaume Beaudoin
 * @brief Classe pour l'objet "Etape" représantant une étape et ses informations.
 * @version 1.2
 */
public class Etape
{
    //Variables membres
    int numero;
    String nom;
    String message = "";
    String image = "";
    int num_piece;
    int nb_pieces;
    //Constructeur
    public Etape(int _numero, String _nom, String _message, int _num_piece, int _nb_pieces, String _image)  //Constructeur
    {
        numero = _numero;
        nom = _nom;
        message = _message;
        num_piece = _num_piece;
        nb_pieces = _nb_pieces;
        image = _image;
    }
    
    /*
    @brief: Modifie les paramètres de l'étape.
    @param: _nom: nom de l'étape
            _message: message de l'étape
            _num_piece: numéro de pièce associé à l'étape
            _nb_pieces: nombre de pièces à prendre
    */
    public void modifEtape(String _nom, String _message, int _num_piece, int _nb_pieces, String _image)
    {
        nom = _nom;
        message = _message;
        num_piece = _num_piece;
        nb_pieces = _nb_pieces;
        image = _image;
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
        jo.put("message", message);
        jo.put("num_piece", num_piece);
        jo.put("nb_pieces", nb_pieces);
        jo.put("image", image);
        return jo;
    }
}
