package prototype;

/**
 * Links two data sets according to two predicates and two data types.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Liaison
 */
public class LiaisonTypee extends Liaison {
	
	/**
	 * Data type of interest within the source data set.
	 */
	private String typesource;
	/**
	 * Data type of interest within the target data set.
	 */
	private String typecible;
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param ts : The source data type.
	 * @param tc : The target data type.
	 */
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
	
	/**
	 * Useless constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param ts : The source data type.
	 * @param tc : The target data type.
	 * @param ml : The max number of links updated.
	 */
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
	
	public final String getTypeSource() {
		return typesource;
	}
	
	public final String getTypeCible() {
		return typecible;
	}
}
