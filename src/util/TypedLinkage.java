package util;

/**
 * Links two data sets according to two predicates and two data types.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Linkage
 */
public class TypedLinkage extends Linkage {
	
	/**
	 * Data type of interest within the source data set.
	 */
	private String sourcetype;
	/**
	 * Data type of interest within the target data set.
	 */
	private String targettype;
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 * @param st : The source data type.
	 * @param tt : The target data type.
	 */
	public TypedLinkage(DataSet s, DataSet c, String sp, String tp, String st, String tt) {
		super(s, c, sp, tp);
		
		maxlinks = 0;
		sourcetype = st;
		targettype = tt;
		sourcequery = writeSelectQuery(sp, st);
		targetquery = writeSelectQuery(tp, tt);
	}
	
	/**
	 * Default constructor.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 * @param st : The source data type.
	 * @param tt : The target data type.
	 * @param ml : The max number of new links.
	 */
	public TypedLinkage(DataSet s, DataSet c, String sp, String tp, String st, String tt, int ml) {
		super(s, c, sp, tp);
		
		maxlinks = Math.max(ml, 0);
		sourcetype = st;
		targettype = tt;
		sourcequery = writeSelectQuery(sp, st);
		targetquery = writeSelectQuery(tp, tt);
	}
	
	public final String getSourceType() {
		return sourcetype;
	}
	
	public final String getTargetType() {
		return targettype;
	}
}
