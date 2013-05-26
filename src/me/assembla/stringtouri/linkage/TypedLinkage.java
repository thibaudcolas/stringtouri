package me.assembla.stringtouri.linkage;

import me.assembla.stringtouri.dataset.Dataset;

/**
 * Links two data sets according to two predicates and two data types.
 * 
 * @author Thibaud Colas
 * @version 21092012
 * @see Linkage
 */
public class TypedLinkage extends Linkage {
	
	/**
	 * Data type of interest within the source data set.
	 */
	private String sourceType;
	/**
	 * Data type of interest within the target data set.
	 */
	private String targetType;
	
	/**
	 * Default constructor.
	 * @param src : The source data set.
	 * @param tgt : The target data set.
	 * @param srcPred : The source predicate.
	 * @param tgtPred : The target predicate.
	 * @param srcType : The source data type.
	 * @param tgtType : The target data type.
	 */
	public TypedLinkage(Dataset src, Dataset tgt, String srcPred, String tgtPred, String srcType, String tgtType) {
		super(src, tgt, srcPred, tgtPred);
		
		sourceType = srcType;
		targetType = tgtType;
		sourceQuery = writeSelectQuery(srcPred, srcType, src.getContext());
		targetQuery = writeSelectQuery(tgtPred, tgtType, tgt.getContext());
	}
	
	public final String getSourceType() {
		return sourceType;
	}
	
	public final String getTargetType() {
		return targetType;
	}
}
