package me.assembla.stringtouri.output;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.assembla.stringtouri.dataset.Dataset;
import me.assembla.stringtouri.linkage.Linkage;

import org.apache.log4j.Level;
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
 * @version 2012-09-30
 * @see Linkage
 */
public abstract class Output {
	
	/**
	 * The data set to use.
	 */
	protected Dataset olddataset;
	/**
	 * The context to use.
	 */
	protected String context;
	/**
	 * The new statements produced by the interlinking process.
	 */
	protected HashMap<String, LinkedList<Statement>> newtuples;
	/**
	 * The predicate where linking has been made.
	 */
	protected String linkingpredicate;
	/**
	 * If newPredicate isn't empty, we write insert queries.
	 */
	protected String newPredicate;
	protected String newPredicateWritable;
	
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
	protected Output(Dataset ds, String cont, String p, String np) throws RepositoryException {
		olddataset = ds;
		context = cont;
		linkingpredicate = p;
		handleNamespaces();
		newtuples = new HashMap<String, LinkedList<Statement>>();
		newPredicate = np;
		newPredicateWritable = np.startsWith("http://") ? "<" + np + ">" : np;

	}
	
	/**
	 * Imports the namespaces to be used later.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	private void handleNamespaces() throws RepositoryException {
		try {
			namespaces = new HashMap<String, String>();
			List<Namespace> tmpnamespaces = olddataset.getNamespaceList();
			for (Namespace n : tmpnamespaces) {
				namespaces.put(n.getPrefix(), n.getName());
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
		return ns.startsWith("http://") ? "<" + ns + pred.getLocalName() + ">" : ns + pred.getLocalName();
	}

	public final HashMap<String, LinkedList<Statement>> getNewTuples() {
		return newtuples;
	}
	
	public final LinkedList<LinkedList<String>> getNewTuplesAsList() {
		LinkedList<LinkedList<String>> ret = new LinkedList<LinkedList<String>>();
		LinkedList<String> tmp = null;
		
		for(String subj : newtuples.keySet()) {
			for(Statement s : newtuples.get(subj)) {
				tmp = new LinkedList<String>();
				tmp.add(s.getSubject().stringValue());
				tmp.add("".equals(newPredicate) ? s.getPredicate().stringValue() : newPredicate);
				tmp.add(s.getObject().stringValue());
			}
			ret.add(tmp);
		}
		
		return ret;
	}

	public final void setNewTuples(HashMap<String, LinkedList<Statement>> nt) throws RepositoryException {
		newtuples = nt;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Output " + olddataset.getName() + " ready to be used.");
		}
	}
	
	/**
	 * Abstract generic method to update the data set.
	 * @throws UpdateExecutionException Query failed to update data.
	 * @throws MalformedQueryException Query isn't valid.
	 * @throws RepositoryException Fatal error while updating the data set.
	 */
	public abstract void updateDataSet() throws RepositoryException, MalformedQueryException, UpdateExecutionException;
	
	/**
	 * Allows to set the logging level for this component.
	 * @param level : The logging level.
	 */
	public void setLoggingLevel(Level level) {
		LOG.setLevel(level);
	}
}
