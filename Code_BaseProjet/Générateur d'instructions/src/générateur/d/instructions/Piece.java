package générateur.d.instructions;

/**
 *
 * @author Guim
 */
public class Piece
{
    //Variables membres
    int numero;
    int n_bac;
    double poid;
    String nom = "";
    public Piece(int _numero, int _n_bac, double _poid, String _nom)  //Constructeur
    {
        numero = _numero;
        n_bac = _n_bac;
        poid = _poid;
        nom = _nom;
    }
    
    public void modifPiece(int _n_bac, double _poid, String _nom)
    {
        n_bac = _n_bac;
        poid = _poid;
        nom = _nom;
    }
}
