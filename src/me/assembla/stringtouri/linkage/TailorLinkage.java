package me.assembla.stringtouri.linkage;

import me.assembla.stringtouri.dataset.Dataset;

/**
 * Links two datasets according to fully customizable queries.
 * In order to work, the queries must return two columns ?s and ?o.
 * 
 * @author Thibaud Colas
 * @version 2012-09-30
 * @see Linkage
 */
public class TailorLinkage extends Linkage {
	
	/**
	 * Default constructor.
	 * @param src : The source data set.
	 * @param tgt : The target data set.
	 * @param srcPred : The source predicate.
	 * @param tgtPred : The target predicate.
	 */
	public TailorLinkage(Dataset src, Dataset tgt, String srcPred, String tgtPred, String srcQuery, String tgtQuery) {
		super(src, tgt, srcPred, tgtPred);
		
		sourceQuery = srcQuery;
		targetQuery = tgtQuery;
	}
}
