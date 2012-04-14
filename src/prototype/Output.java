package prototype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Abstract class to process the result of the interlinking.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Linkage
 */
public abstract class Output {
	
	/**
	 * The data set to use.
	 */
	protected DataSet olddataset;
	/**
	 * The new statements produced by the interlinking process.
	 */
	protected HashMap<String, LinkedList<Statement>> newtuples;
	/**
	 * The predicate where linking has been made.
	 */
	protected String predicate;
	/**
	 * Namespaces to be used during the process.
	 */
	protected HashMap<String, String> namespaces;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(Output.class.getName());
	
	/**
	 * Super-class lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected Output(DataSet ds, String p) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Created output " + ds.getName() + ".");
		}
		
		olddataset = ds;
		predicate = p;
		handleNamespaces();
		newtuples = new HashMap<String, LinkedList<Statement>>();
	}
	
	/**
	 * Super-class default constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected Output(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Created output " + ds.getName() + ".");
		}
		
		olddataset = ds;
		predicate = p;
		handleNamespaces();
		newtuples = ns;
	}
	
	/**
	 * Super-class alternative constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected Output(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p, boolean a) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Created output " + ds.getName() + ".");
		}
		
		olddataset = ds;
		handleNamespaces();
		predicate = p;
		newtuples = a ? getFilteredStatements(ns) : ns;
	}
	
	/**
	 * Imports the namespaces to be used later.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	private void handleNamespaces() throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Export " + olddataset.getName() + " namespace retrieval.");
		}
		try {
			namespaces = new HashMap<String, String>();
			List<Namespace> tmpnamespaces = olddataset.getNamespaceList();
			for (Namespace n : tmpnamespaces) {
				namespaces.put(n.getName(), n.getPrefix());
			}
		} catch (RepositoryException e) {
			throw new RepositoryException("While fetching namespaces", e);
		}
	}
	
	/**
	 * Used to shorten a predicate into its local version.
	 * @param pred : The predicate to convert.
	 * @return The predicate using a local name.
	 */
	protected String filterPredicate(URI pred) {
		String ns = pred.getNamespace();
		return (ns.startsWith("http://") ? namespaces.get(ns) + ":" : ns) + pred.getLocalName();
	}
	
	/**
	 * Filters statements inside the data set to update the new ones.
	 * @param tuples : The new statements.
	 * @return A set of up to date statements.
	 * @throws RepositoryException Error while fetching statements.
	 */
	protected final HashMap<String, LinkedList<Statement>> getFilteredStatements(HashMap<String, LinkedList<Statement>> tuples) throws RepositoryException {
		HashMap<String, LinkedList<Statement>> filteredstatements = getAllStatements();
		LinkedList<Statement> tmpold;
		LinkedList<Statement> tmpnew;
		String tmpprop;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Export " + olddataset.getName() + " statement filtering.");
		}
		
		for (String suj : tuples.keySet()) {
			tmpold = filteredstatements.get(suj);
			if (tmpold != null) {
				tmpnew = new LinkedList<Statement>();
				// For all the old tuples.
				for (Statement s : tmpold) {
					tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
					if (!tmpprop.equals(predicate)) {
						// We only select the ones with a predicate different from ours.
						tmpnew.add(s);
					}
				}
				tmpnew.addAll(tuples.get(suj));
				filteredstatements.put(suj, tmpnew);
			}
		}
		return filteredstatements;
	}
	
	/**
	 * Retrieves every statement inside the data set.
	 * @return Hashmap of all the statements grouped by subject.
	 * @throws RepositoryException Error while fetching the statements.
	 */
	private HashMap<String, LinkedList<Statement>> getAllStatements() throws RepositoryException {
		HashMap<String, LinkedList<Statement>> allstatements = new HashMap<String, LinkedList<Statement>>();
		
		LinkedList<Statement> all = olddataset.getAllStatements();
		LinkedList<Statement> tmptuples;
		String tmpsubject;
		for (Statement s : all) {
			tmpsubject = s.getSubject().stringValue();
			tmptuples = allstatements.get(tmpsubject);
			if (tmptuples == null) {
				tmptuples = new LinkedList<Statement>();
				allstatements.put(tmpsubject, tmptuples);
			}
			tmptuples.add(s);
		}
		return allstatements;
	}

	public final HashMap<String, LinkedList<Statement>> getNewTuples() {
		return newtuples;
	}

	public final void setNewTuples(HashMap<String, LinkedList<Statement>> nt, boolean a) throws RepositoryException {
		this.newtuples = a ? getFilteredStatements(nt) : nt;
	}

	/**
	 * Abstract generic method to retrieve the output of the process.
	 * @return The output of the interlinking.
	 */
	public abstract String getOutput();
	
	/**
	 * Abstract generic method to update the data set.
	 * @throws UpdateExecutionException Query failed to update data.
	 * @throws MalformedQueryException Query isn't valid.
	 * @throws RepositoryException Fatal error while updating the data set.
	 */
	public abstract void updateDataSet() throws RepositoryException, MalformedQueryException, UpdateExecutionException;
}
