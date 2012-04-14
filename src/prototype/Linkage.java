package prototype;

import java.util.HashMap;
import java.util.LinkedList;

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
 * @version 04042012
 * @see DataSet
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
	protected DataSet source;
	/**
	 * Target data for new links.
	 */
	protected DataSet target;
	/**
	 * Source predicate to lookup.
	 */
	protected String sourcepredicate;
	/**
	 * Target predicate which will be modified.
	 */
	protected String targetpredicate;
	
	/**
	 * Query submited to the source data.
	 */
	protected String sourcequery;
	/**
	 * Query submited to the target data.
	 */
	protected String targetquery;
	
	/**
	 * Max number of new links to be made.
	 */
	protected int maxlinks;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(Linkage.class.getName());
	
	private static final int DEFSIZE = 100;
	
	/**
	 * Super-class constructor used to log initialization of the linkages.
	 * @param s : The source data set.
	 * @param t : The target data set.
	 * @param sp : The source predicate.
	 * @param tp : The target predicate.
	 */
	protected Linkage(DataSet s, DataSet t, String sp, String tp) {
		name = sp + " - " + tp;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Created linkage " + name + ".");
		}
		
		source = s;
		target = t;
		sourcepredicate = sp;
		targetpredicate = tp;
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
		HashMap<String, String> sourcedata = new HashMap<String, String>(DEFSIZE + maxlinks);
		TupleQueryResult tqr = null;
		BindingSet bs;
		
		try {
			tqr = source.selectQuery(sourcequery);

			if (!hasCorrectBindingNames(tqr)) {
				throw new MalformedQueryException("Wrong query result bindings - " + sourcequery);
			}
			
			int cpt = 0;
			// For every result binding (line).
			while (tqr.hasNext()) {
				cpt++;
				bs = tqr.next();
				sourcedata.put(bs.getValue(OVAR).stringValue(), bs.getValue(SVAR).stringValue());
			}
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Predicate " + sourcepredicate + " has " + cpt + " statement(s) in " + source.getName() + ".");
			}
		}
		catch (QueryEvaluationException e) {
			throw new QueryEvaluationException("Query : " + sourcequery, e);
		}
		catch (RepositoryException e) {
			throw new RepositoryException("Repository issue while fetching data - " + sourcepredicate, e);
		}
		catch (MalformedQueryException e) {
			throw new MalformedQueryException("Query : " + sourcequery, e);
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
		HashMap<String, LinkedList<String>> targetdata = new HashMap<String, LinkedList<String>>(DEFSIZE + maxlinks);
		TupleQueryResult tqr = null;
		BindingSet bs;
		String object;
		LinkedList<String> subjects;
		
		try {
			tqr = target.selectQuery(targetquery);
			
			if (!hasCorrectBindingNames(tqr)) {
				throw new QueryEvaluationException("Wrong query result bindings - " + targetquery);
			}

			int cpt = 0;
			// For every result binding (line).
			while (tqr.hasNext()) {
				cpt++;
				bs = tqr.next();
				object = bs.getValue(OVAR).stringValue();
				
				// If the value is already inside the data set, we retrieve associated URIs in order to add one.
				// If the value hasn't been encountered yet, we add the URI to the corresponding object.
				if (targetdata.containsKey(object)) {
					subjects = new LinkedList<String>(targetdata.get(object));
				}
				else {
					subjects = new LinkedList<String>();
				}
				
				subjects.add(bs.getValue(SVAR).stringValue());
				targetdata.put(object, subjects);
			}
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Predicate " + targetpredicate + " has " + cpt + " statement(s) in " + target.getName() + ".");
			}
		}
		catch (RepositoryException e) {
			throw new RepositoryException("While fetching data - " + sourcepredicate, e);
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
		
		HashMap<String, LinkedList<Statement>> newlinks = new HashMap<String, LinkedList<Statement>>();
		LinkedList<Statement> tmplinks = new LinkedList<Statement>();
		
		int cpt = 0;
		for (String objet : targetdata.keySet()) {
			if (sourcedata.containsKey(objet)) {
				cpt++;
				for (String sujet : targetdata.get(objet)) {
					
					tmplinks = newlinks.get(sujet);
					if (tmplinks == null) {
						tmplinks = new LinkedList<Statement>();
						newlinks.put(sujet, tmplinks);
					}
					// targetpredicate is the only predicate for which we have tuples here.
					tmplinks.add(new StatementImpl(new URIImpl(sujet), new URIImpl(targetpredicate), new URIImpl(sourcedata.get(objet))));
				}
			}
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Predicate " + targetpredicate + " and " + sourcepredicate + " have " + cpt + " common statement(s) in " + name + ".");
		}
		
		return newlinks;
	}
	
	/**
	 * Writes a SPARQL query to retrieve subject - object pairs for a given predicate.
	 * @param predicate : The predicate to use, including its local namespace.
	 * @return The full SPARQL query.
	 */
	public String writeSelectQuery(String predicate) {
		return "SELECT ?" + SVAR + " ?" + OVAR + " "
			+ "WHERE {?" + SVAR + " " + predicate + " ?" + OVAR + "}" 
			+ (maxlinks > 0 ? " LIMIT " + maxlinks : "");
	}
	
	/**
	 * Writes a SPARQL query to retrieve subject - object pairs for a given predicate.
	 * The subject will also be of a given type.
	 * @param predicate : The predicate to use, including its local namespace.
	 * @param type : The subject's data type.
	 * @return The full SPARQL query.
	 */
	public String writeSelectQuery(String predicate, String type) {
		String typecheck = type.equals("") ? "" : "?" + SVAR + " a " + type + " . ";
		return "SELECT ?" + SVAR + " ?" + OVAR + " "
			+ "WHERE {" +  typecheck  +  "?" + SVAR + " " + predicate + " ?" + OVAR + "}" 
			+ (maxlinks > 0 ? " LIMIT " + maxlinks : "");
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getSourcePredicate() {
		return sourcepredicate;
	}
	
	public final String getTargetPredicate() {
		return targetpredicate;
	}
	
	public final String getSourceQuery() {
		return sourcequery;
	}
	
	public final String getTargetQuery() {
		return targetquery;
	}
	
	public final int getMaxLinks() {
		return maxlinks;
	}
	
	/**
	 * Proper stop of both data sets.
	 */
	public void shutdown() {
		target.shutdown();
		source.shutdown();
	}
}
