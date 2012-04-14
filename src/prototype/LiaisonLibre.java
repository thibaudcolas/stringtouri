package prototype;

/**
 * Links two datasets according to fully customizable queries.
 * In order to work, the queries must return two columns ?s and ?o.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Liaison
 */
public class LiaisonLibre extends Liaison {
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param qs : The source query.
	 * @param qc : The target query.
	 */
	public LiaisonLibre(DataSet s, DataSet c, String ps, String pc, String qs, String qc) {
		super(s, c, ps, pc);
		
		maxliens = 0;
		querysource = qs;
		querycible = qc;
	}
	
	/**
	 * Useless constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param qs : The source query.
	 * @param qc : The target query.
	 * @param ml : Max number of links to create.
	 */
	public LiaisonLibre(DataSet s, DataSet c, String ps, String pc, String qs, String qc, int ml) {
		super(s, c, ps, pc);
		
		maxliens = Math.max(ml, 0);
		querysource = qs;
		querycible = qc;
	}
}
