package générateur.d.instructions;
import org.json.*;
/**
 *
 * @author Guim
 */
public class Etape
{
    //Variables membres
    int numero;
    String nom;
    String message = "";
    int num_piece;
    int nb_pieces;
    public Etape(int _numero, String _nom, String _message, int _num_piece, int _nb_pieces)  //Constructeur
    {
        numero = _numero;
        nom = _nom;
        message = _message;
        num_piece = _num_piece;
        nb_pieces = _nb_pieces;
    }
    public void modifEtape(String _nom, String _message, int _num_piece, int _nb_pieces)
    {
        nom = _nom;
        message = _message;
        num_piece = _num_piece;
        nb_pieces = _nb_pieces;
    }
    public JSONObject toJSON()
    {
        JSONObject jo = new JSONObject();
        jo.put("nom", nom);
        jo.put("numero", numero);
        jo.put("message", message);
        jo.put("num_piece", num_piece);
        jo.put("nb_pieces", nb_pieces);
        return jo;
    }
}
