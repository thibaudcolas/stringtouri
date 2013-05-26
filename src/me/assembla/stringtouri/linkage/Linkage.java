package me.assembla.stringtouri.linkage;

import java.util.HashMap;
import java.util.LinkedList;

import me.assembla.stringtouri.dataset.Dataset;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;


/**
 * Abstract class interlinking two data sets according to criteria.
 * 
 * @author Thibaud Colas
 * @version 21092012
 * @see Dataset
 */
public abstract class Linkage {
	
	public static final String SVAR = "s";
	public static final String PVAR = "p";
	public static final String OVAR = "o";
	
	/**
	 * Linkage name for display purposes.
	 */
	protected String name;
	/**
	 * Source data for new links.
	 */
	protected Dataset source;
	/**
	 * Target data for new links.
	 */
	protected Dataset target;
	/**
	 * Source predicate to lookup.
	 */
	protected String sourcePredicate;
	/**
	 * Target predicate which will be modified.
	 */
	protected String targetPredicate;
	
	/**
	 * Query submited to the source data.
	 */
	protected String sourceQuery;
	/**
	 * Query submited to the target data.
	 */
	protected String targetQuery;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(Linkage.class.getName());
	
	private static final int DEFSIZE = 100;
	
	/**
	 * Super-class constructor.
	 * @param s : The source data set.
	 * @param t : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 */
	protected Linkage(Dataset s, Dataset t, String sp, String tp) {
		name = sp + " - " + tp;
		
		source = s;
		target = t;
		sourcePredicate = sp;
		targetPredicate = tp;
	}
	
	/**
	 * Retrieves data for the source predicate from the source data set.
	 * One value = one URI, one URI = one value.
	 * @return HashMap where the key is the value for the source predicate and value is the associated URI (subject).
	 * @throws QueryEvaluationException Issue with the source query.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, String> getSourceData() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		// If maxlinks = 0, default sized. Otherwise, optimized size.
		HashMap<String, String> sourcedata = new HashMap<String, String>(DEFSIZE);
		TupleQueryResult tqr = null;
		BindingSet bs;
		
		try {
			tqr = source.selectQuery(sourceQuery);
			
			if (!hasCorrectBindingNames(tqr)) {
				throw new MalformedQueryException("Wrong query result bindings - " + sourceQuery);
			}
			
			int cpt = 0;
			// For every result binding (line).
			while (tqr.hasNext()) {
				cpt++;
				bs = tqr.next();
				sourcedata.put(bs.getValue(OVAR).stringValue(), bs.getValue(SVAR).stringValue());
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Predicate " + sourcePredicate + " has " + cpt + " statement(s) in " + source.getName() + ".");
			}
		}
		catch (QueryEvaluationException e) {
			throw new QueryEvaluationException("Query : " + sourceQuery, e);
		}
		catch (RepositoryException e) {
			throw new RepositoryException("Repository issue while fetching data - " + sourcePredicate, e);
		}
		catch (MalformedQueryException e) {
			throw new MalformedQueryException("Query : " + sourceQuery, e);
		}
		finally {
			if (tqr != null) {
				tqr.close();
			}
		}
		
		return sourcedata;
	}
	
	/**
	 * Retrieves data from the target data set which we want to update.
	 * One value is associated with multiple URIs, one URI can be associated with multiple values.
	 * @return Hashmap where a value is associated with the URIs having this value for the target predicate.
	 * @throws QueryEvaluationException Issue with the target query.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, LinkedList<String>> getTargetData() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		// If maxlinks = 0, default sized. Otherwise, optimized size.
		HashMap<String, LinkedList<String>> targetdata = new HashMap<String, LinkedList<String>>(DEFSIZE);
		TupleQueryResult tqr = null;
		BindingSet bs;
		String object;
		LinkedList<String> subjects;
		LinkedList<String> tmpsubjects;
		try {
			tqr = target.selectQuery(targetQuery);

			if (!hasCorrectBindingNames(tqr)) {
				throw new QueryEvaluationException("Wrong query result bindings - " + targetQuery);
			}
			
			int cpt = 0;
			// For every result binding (line).
			while (tqr.hasNext()) {
				cpt++;
				bs = tqr.next();
				object = bs.getValue(OVAR).stringValue();
				// If the value is already inside the data set, we retrieve associated URIs in order to add one.
				// If the value hasn't been encountered yet, we add the URI to the corresponding object.
				tmpsubjects = targetdata.get(object);
				subjects = tmpsubjects == null ? new LinkedList<String>() : new LinkedList<String>(tmpsubjects);
				
				subjects.add(bs.getValue(SVAR).stringValue());
				targetdata.put(object, subjects);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Predicate " + targetPredicate + " has " + cpt + " statement(s) in " + target.getName() + ".");
			}
		}
		catch (RepositoryException e) {
			throw new RepositoryException("While fetching data - " + sourcePredicate, e);
		}
		finally {
			if (tqr != null) {
				tqr.close();
			}
		}
		
		return targetdata;
	}
	
	/**
	 * Tels if the bindings of the results are wel-formed.
	 * @param tqr : The result of a SPARQL query.
	 * @return True if the results contain solely both SVAR and OVAR columns.
	 * @throws QueryEvaluationException Error while closing the result.
	 */
	public final boolean hasCorrectBindingNames(TupleQueryResult tqr) throws QueryEvaluationException {
		return tqr.getBindingNames().contains(SVAR)
				&& tqr.getBindingNames().contains(OVAR)
				&& tqr.getBindingNames().size() == 2;
	}
	
	/**
	 * Creates new statements with the updated data.
	 * @return A Hashmap containing statements grouped by their subjects.
	 * @throws QueryEvaluationException Issue with one of the queries.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, LinkedList<Statement>> generateLinks() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		HashMap<String, String> sourcedata = getSourceData();
		HashMap<String, LinkedList<String>> targetdata = getTargetData();
		
		HashMap<String, LinkedList<Statement>> newlinks = new HashMap<String, LinkedList<Statement>>(targetdata.size());
		LinkedList<Statement> tmplinks;
		
		int cpt = 0;
		for (String object : targetdata.keySet()) {
			if (sourcedata.containsKey(object)) {
				cpt++;
				for (String subject : targetdata.get(object)) {
					
					tmplinks = newlinks.get(subject);
					if (tmplinks == null) {
						tmplinks = new LinkedList<Statement>();
						newlinks.put(subject, tmplinks);
					}
					// targetpredicate is the only predicate for which we have tuples here.
					tmplinks.add(new StatementImpl(new URIImpl(subject), new URIImpl(targetPredicate), new URIImpl(sourcedata.get(object))));
				}
			}
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Predicate " + targetPredicate + " and " + sourcePredicate + " have " + cpt + " common statement(s) in " + name + ".");
		}
		
		return newlinks;
	}
	
	/**
	 * Reformats a predicate / class to make it usable.
	 * @param str the predicate / class to clean.
	 * @return A clean predicate / class.
	 */
	private String refineURI(String str) {
		String ret = str.trim();
		if (ret.startsWith("http")) {
			ret = "<" + ret + ">";
		}
		return ret;
	}
	
	/**
	 * Reformats a context to make it usable.
	 * @param str the context to clean.
	 * @return A clean context.
	 */
	private String refineContext(String str) {
		String ret = str.trim();
		if (!ret.startsWith("<")) {
			ret = "<" + ret + ">";
		}
		return ret;
	}
	
	/**
	 * Writes a SPARQL query to retrieve subject - object pairs for a given 
	 * predicate, a given subject class and a given context.
	 * @param predicate : The predicate to use, including its local namespace.
	 * @param type : The subject's data type.
	 * @param context : The context for our request.
	 * @return The full SPARQL query.
	 */
	public String writeSelectQuery(String predicate, String type, String context) {
		String pred = refineURI(predicate);
		String typecheck = type.equals("") ? "" : "?" + SVAR + " a " + refineURI(type) + " . ";
		String contextcheck = context.equals("") ? "" : "FROM " + refineContext(context) + " ";
		return "SELECT ?" + SVAR + " ?" + OVAR + " " + contextcheck
			+ "WHERE {" +  typecheck  +  "?" + SVAR + " " + pred + " ?" + OVAR + "}"; 
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getSourcePredicate() {
		return sourcePredicate;
	}
	
	public final String getTargetPredicate() {
		return targetPredicate;
	}
	
	public final String getSourceQuery() {
		return sourceQuery;
	}
	
	public final String getTargetQuery() {
		return targetQuery;
	}
	
	/**
	 * Proper stop of both data sets.
	 */
	public void shutdown() {
		if (source != null) {
			source.shutdown();
		}
		if (target != null) {
			target.shutdown();
		}
	}
	
	/**
	 * Allows to set the logging level for this component.
	 * @param level : The logging level.
	 */
	public void setLoggingLevel(Level level) {
		LOG.setLevel(level);
	}
}
