package deprecated;

/**
 * Classe de liaison entre deux jeux de données selon deux propriétés et un/deux types.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison
 */
public class LiaisonTypee extends Liaison {
	
	// On va sélectionner les triplets dont le sujet est du type désiré et qui ont la propriété qui nous intéressent.
	private String typesource;
	private String typecible;
	
	public LiaisonTypee(Jeu s, Jeu c, String ps, String pc, String ts, String tc) {
		nom = ps + "-" + pc;
		
		maxliens = 0;
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		typesource = ts;
		typecible = tc;
		
		querysource = writeQuery(ps, ts);
		querycible = writeQuery(pc, tc);
	}
	
	public LiaisonTypee(Jeu s, Jeu c, String ps, String pc, String ts, String tc, int ml) {
		nom = ps + "-" + pc;
		
		maxliens = Math.max(ml, 0);
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
		typesource = ts;
		typecible = tc;
		
		querysource = writeQuery(ps, ts);
		querycible = writeQuery(pc, tc);
	}
	
	public String getTypeSource() {
		return typesource;
	}
	
	public String getTypeCible() {
		return typecible;
	}
}
