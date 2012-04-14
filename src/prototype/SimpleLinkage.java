package prototype;

/**
 * Links two data sets according to two predicates.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Linkage
 */
public class SimpleLinkage extends Linkage {
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 */
	public SimpleLinkage(DataSet s, DataSet c, String sp, String tp) {
		super(s, c, sp, tp);
		
		maxlinks = 0;
		sourcequery = writeSelectQuery(sp);
		targetquery = writeSelectQuery(tp);
	}
	
	/**
	 * Useless constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 * @param ml : The max number of links updated.
	 */
	public SimpleLinkage(DataSet s, DataSet c, String sp, String tp, int ml) {
		super(s, c, sp, tp);
		
		maxlinks = Math.max(ml, 0);
		sourcequery = writeSelectQuery(sp);
		targetquery = writeSelectQuery(tp);
	}
}
