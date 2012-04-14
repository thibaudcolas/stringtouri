package prototype;

/**
 * Links two datasets according to fully customizable queries.
 * In order to work, the queries must return two columns ?s and ?o.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Linkage
 */
public class FreeLinkage extends Linkage {
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 * @param sq : The source query.
	 * @param tq : The target query.
	 */
	public FreeLinkage(DataSet s, DataSet c, String sp, String tp, String sq, String tq) {
		super(s, c, sp, tp);
		
		maxlinks = 0;
		sourcequery = sq;
		targetquery = tq;
	}
	
	/**
	 * Useless constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 * @param sq : The source query.
	 * @param tq : The target query.
	 * @param ml : Max number of new links to create.
	 */
	public FreeLinkage(DataSet s, DataSet c, String ps, String pc, String sq, String tq, int ml) {
		super(s, c, ps, pc);
		
		maxlinks = Math.max(ml, 0);
		sourcequery = sq;
		targetquery = tq;
	}
}
