package générateur.d.instructions;

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
}
