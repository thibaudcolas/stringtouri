package me.assembla.stringtouri.linkage;

import me.assembla.stringtouri.dataset.Dataset;

/**
 * Links two data sets according to two predicates.
 * 
 * @author Thibaud Colas
 * @version 2012-09-30
 * @see Linkage
 */
public class StandardLinkage extends Linkage {
	
	/**
	 * Default constructor.
	 * @param src : The source data set.
	 * @param tgt : The target data set.
	 * @param srcPred : The source predicate.
	 * @param tgtPred : The target predicate.
	 */
	public StandardLinkage(Dataset src, Dataset tgt, String srcPred, String tgtPred) {
		super(src, tgt, srcPred, tgtPred);
		
		sourceQuery = writeSelectQuery(srcPred, "", src.getContext());
		targetQuery = writeSelectQuery(tgtPred, "", tgt.getContext());
	}
}
