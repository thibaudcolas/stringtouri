package prototype;

/**
 * Links two data sets according to two predicates.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Liaison
 */
public class LiaisonSimple extends Liaison {
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 */
	public LiaisonSimple(DataSet s, DataSet c, String ps, String pc) {
		super(s, c, ps, pc);
		
		maxliens = 0;
		querysource = writeQuery(ps);
		querycible = writeQuery(pc);
	}
	
	/**
	 * Useless constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param ml : The max number of links updated.
	 */
	public LiaisonSimple(DataSet s, DataSet c, String ps, String pc, int ml) {
		super(s, c, ps, pc);
		
		maxliens = Math.max(ml, 0);
		querysource = writeQuery(ps);
		querycible = writeQuery(pc);
	}
}
