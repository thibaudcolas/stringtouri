package prototype;

/**
 * Classe de liaison entre deux jeux de données permettant d'écrire directement la requête de sélection des données à comparer.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison
 */
public class LiaisonLibre extends Liaison {
	
	/**
	 *  /!\ Pour fonctionner, il faut s'assurer que les requêtes renvoient bien les valeurs s et o.
	 */
	public LiaisonLibre(Jeu s, Jeu c, String ps, String pc, String qs, String qc) {
		nom = ps + "-" + pc;
		
		maxliens = 0;
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		
		querysource = qs;
		querycible = qc;
	}
	
	/**
	 *  /!\ Pour fonctionner, il faut s'assurer que les requêtes renvoient bien les valeurs s et o.
	 */
	public LiaisonLibre(Jeu s, Jeu c, String ps, String pc, String qs, String qc, int ml) {
		nom = ps + "-" + pc;
		
		maxliens = Math.max(ml, 0);
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		
		querysource = qs;
		querycible = qc;
	}
}
