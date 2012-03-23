package prototype;

/**
 * Classe de liaison entre deux jeux de données selon deux propriétés.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison
 */
public class LiaisonSimple extends Liaison {
	
	public LiaisonSimple(Jeu s, Jeu c, String ps, String pc) {
		nom = ps + "-" + pc;
		
		maxliens = 0;
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		
		querysource = writeQuery(ps);
		querycible = writeQuery(pc);
	}
	
	public LiaisonSimple(Jeu s, Jeu c, String ps, String pc, int ml) {
		nom = ps + "-" + pc;
		
		maxliens = Math.max(ml, 0);
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		
		querysource = writeQuery(ps);
		querycible = writeQuery(pc);
	}
}
