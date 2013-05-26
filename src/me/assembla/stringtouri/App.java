package me.assembla.stringtouri;

import java.util.LinkedList;

import me.assembla.stringtouri.dataset.Dataset;
import me.assembla.stringtouri.linkage.Linkage;
import me.assembla.stringtouri.linkage.StandardLinkage;
import me.assembla.stringtouri.linkage.TailorLinkage;
import me.assembla.stringtouri.linkage.TypedLinkage;
import me.assembla.stringtouri.output.Output;
import me.assembla.stringtouri.output.SPARQLOutput;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Handles the interlinking process from A to Z.
 * 
 * @author Thibaud Colas
 * @version 21092012
 * @see Dataset
 */
public class App {
	
	/**
	 * Name for display purposes.
	 */
	protected String name;
	/**
	 * Data set to which we'll link data.
	 */
	protected Dataset reference;
	/**
	 * Data set where the links will be made.
	 */
	protected Dataset goal;
	
	protected String goalContext;
	/**
	 * Predicate where the values are going to be replaced with external links.
	 */
	protected String updatedpredicate;
	/**
	 * Linking component managing linking rules to apply.
	 */
	protected Linkage linkage;
	/**
	 * Output handler, telling how the update will be processed.
	 */
	protected Output output;
	/**
	 * Logging level to use at runtime.
	 */
	protected Level logginglevel;
	/**
	 * Main Logger to record actions on pretty much everything.
	 */
	protected static final Logger LOG = Logger.getLogger(App.class.getName());
	
	/**
	 * Shortest constructor to use only with parent classes.
	 */
	protected App() {
	}
	
	/**
	 * Sets the linking handler to a simple one.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 */
	public void useSimpleLinkage(String referencepredicate, String goalpredicate) {
		linkage = new StandardLinkage(reference, goal, referencepredicate, goalpredicate);
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created simple linkage " + name + ".");
		}
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Sets the linking handler to be much more complex.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 * @param referencetype : Type of the values on source side.
	 * @param goaltype : Type of the values on target side.
	 */
	public void useTypedLinkage(String referencepredicate, String goalpredicate, String referencetype, String goaltype) {
		linkage = new TypedLinkage(reference, goal, referencepredicate, goalpredicate, referencetype, goaltype);
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created typed linkage " + name + ".");
		}
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Customizable criteria linking handler setter.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 * @param referencequery : SPARQL query to be made.
	 * @param goalquery : SPARQL query to be made.
	 */
	public void useTailorLinkage(String referencepredicate, String goalpredicate, String referencequery, String goalquery) {
		linkage = new TailorLinkage(reference, goal, referencepredicate, goalpredicate, referencequery, goalquery);
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created tailor linkage " + name + ".");
		}
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Sets output to be SPARQL Update queries.
	 * @param newPredicate If not empty, indicates that we need to create a new predicate with the updated data.
	 * @throws RepositoryException 
	 * @throws QueryEvaluationException 
	 * @throws MalformedQueryException 
	 */
	public void useSPARQLOutput(String newPredicate) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		try {
			output = new SPARQLOutput(goal, goalContext, updatedpredicate, newPredicate);
			output.setLoggingLevel(logginglevel);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created SPARQL output " + goal.getName() + ".");
				LOG.debug("Export " + goal.getName() + " namespace retrieval.");
			}
			
			generateNewLinks();
		} catch (RepositoryException e) {
			LOG.fatal("Export " + name + " SPARQL - " + e);
			shutdown();
			throw e;
		} catch (QueryEvaluationException e) {
			LOG.fatal("Export " + name + " SPARQL - " + e);
			shutdown();
			throw e;
		} catch (MalformedQueryException e) {
			LOG.fatal("Export " + name + " SPARQL - " + e);
			shutdown();
			throw e;
		}
		
	}
	
	/**
	 * Starts the interlinking process.
	 * @param newPredicate If not empty, indicates that we need to create a new predicate with the updated data.
	 * @throws QueryEvaluationException 
	 * @throws RepositoryException 
	 * @throws MalformedQueryException 
	 */
	protected void generateNewLinks() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		try {
			output.setNewTuples(linkage.generateLinks());
			if (LOG.isDebugEnabled()) {
				LOG.debug("New links from " + reference.getName() + " to " + goal.getName() + " retrieved.");
			}
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			throw e;
		} catch (QueryEvaluationException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			throw e;
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			throw e;
		}
	}
	
	/**
	 * Gets the new links as a list.
	 * @return Output as a list of statements as lists of string.
	 */
	public final LinkedList<LinkedList<String>> getOutputAsList() {
		return output.getNewTuplesAsList();
	}
	
	/**
	 * Updates statements inside the repository.
	 * @throws RepositoryException 
	 * @throws UpdateExecutionException 
	 * @throws MalformedQueryException 
	 */
	public final void updateData() throws RepositoryException, UpdateExecutionException, MalformedQueryException {
		try {
			output.updateDataSet();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Data set " + goal.getName() + " updated.");
			}
			
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			throw(e);
		} catch (UpdateExecutionException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			throw(e);
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			throw(e);
		}
	}

	/**
	 * Shuts down both data sets.
	 */
	public final void shutdown() {
		if (reference != null) {
			reference.shutdown();
		}
		if (goal != null) {
			goal.shutdown();
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Shutdown app " + name);
		}
	}
	
	/**
	 * Allows to set the logging level.
	 * @param level : The logging level.
	 */
	public final void setLoggingLevel(Level level) {
		LOG.setLevel(level);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Log " + LOG.getName() + " set to " + level);
		}
	}
}
