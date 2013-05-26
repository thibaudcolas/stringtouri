package me.assembla.stringtouri.dataset;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Abstract class managing a data set from any origin.
 * 
 * @author Thibaud Colas
 * @version 13072012
 * @see RepositoryConnection
 */
public abstract class Dataset {
	
	/**
	 * Data set name for display.
	 */
	protected String name;
	/**
	 * Context to use with the data.
	 */
	protected String context;
	/** 
	 * Repository where the data is stored.
	 */
	protected Repository repository;
	/**
	 * Connection to the data set's repository.
	 */
	protected RepositoryConnection connection;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(Dataset.class.getName());

	/**
	 * Super-class constructor used to log initialization of the data sets.
	 * @param n : name of the data set.
	 * @param c : context to use.
	 */
	protected Dataset(String n, String c) {
		name = n;
		context = c;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created data set " + name + ".");
		}
	}
		
	
	/**
	 * Adds a namespace to the repository.
	 * @param prefix : Namespaces' prefix.
	 * @param uri : Namespaces' full name.
	 * @throws RepositoryException If the new binding fails (repository unwritable).
	 */
	public final void addNamespace(String prefix, String uri) throws RepositoryException {
		connection.setNamespace(prefix, uri);
	}
	
	/**
	 * Recovers the namespace matching a prefix.
	 * @param prefix : The prefix of the namespace.
	 * @return The namespace as a string.
	 * @throws RepositoryException Error while reading the namespace.
	 */
	public final String getNamespace(String prefix) throws RepositoryException {
		return connection.getNamespace(prefix);
	}
	
	/**
	 * Erases all namespaces from the repository.
	 * @throws RepositoryException Error while erasing all the namespaces.
	 */
	public final void resetNamespaces() throws RepositoryException {
		connection.clearNamespaces();
	}
	
	/**
	 * Returns all of the namespaces as a list.
	 * @return A list of namespaces.
	 * @throws RepositoryException Error while reading the namespaces.
	 */
	public final List<Namespace> getNamespaceList() throws RepositoryException {
		return connection.getNamespaces().asList();
	}
	
	/**
	 * Formats the namespaces in order to be used inside a query.
	 * @return A string made of the namespaces.
	 * @throws RepositoryException Error while reading the namespaces.
	 */
	public String getPrefixes() throws RepositoryException {
		String res = "";
		List<Namespace> ns = getNamespaceList();
		for (Namespace n : ns) {
			res += "PREFIX " + n.getPrefix() + ": <" + n.getName() + "> "; 
		}
		return res;
	}
	
	/**
	 * Sends and evaluates a SPARQL select query on the data set.
	 * @param query : The SPARQL query without its prefixes.
	 * @return The result of the query.
	 * @throws MalformedQueryException Query isn't valid.
	 * @throws RepositoryException Error while accessing the repository.
	 * @throws QueryEvaluationException Query result isn't valid.
	 */
	public TupleQueryResult selectQuery(String query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		TupleQuery tq;
		TupleQueryResult tqr;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Query " + name + " select - " + query);
		}
		
		try {
			tq = connection.prepareTupleQuery(QueryLanguage.SPARQL, getPrefixes() + query);
			tqr = tq.evaluate();
		}
		catch (MalformedQueryException e) {
			throw new MalformedQueryException("Query : " + query, e);
		} catch (QueryEvaluationException e) {
			throw new QueryEvaluationException("Query : " + query, e);
		}
	    return tqr;
	}
	
	/**
	 * Sends an update (delete/insert) SPARQL query to the data set.
	 * @param query : The SPARQL query without its prefixes.
	 * @throws MalformedQueryException Query isn't valid.
	 * @throws RepositoryException Error while accessing the repository.
	 * @throws UpdateExecutionException Query update isn't valid.
	 */
	public void updateQuery(String query) throws RepositoryException, UpdateExecutionException, MalformedQueryException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Query " + name + " update - " + query);
		}
		
		try {
			Update up = connection.prepareUpdate(QueryLanguage.SPARQL, getPrefixes() + query);
		    up.execute();
		}
		catch (MalformedQueryException e) {
			throw new MalformedQueryException("Query : " + query, e);
		} catch (UpdateExecutionException e) {
			throw new UpdateExecutionException("Query : " + query, e);
		}
	}
	
	/**
	 * Stops properly the connection to the repository and shuts down the repository itself.
	 */
	public final void shutdown() {
		try {
			connection.close();
			repository.shutDown();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Connection " + name + " " + (connection.isOpen() ? "still on" : "off") + ".");
			}
		} catch (RepositoryException e) {
			LOG.warn("Connection " + name + " failed to be closed - " + e);
		}
	}
	
	/**
	 * Commits all of the changes which have not been commited yet.
	 */
	public final void commit() {
		try {
			connection.commit();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Commit " + name + ".");
			}
			
		} catch (RepositoryException e) {
			LOG.error("Commit " + name + " error - " + e);
		}
	}
	
	/**
	 * Checks wheter autocommit is on or off.
	 * @return Status of autocommit.
	 */
	public final boolean isAutoCommit() {
		boolean status = false;
		try {
			status = connection.isAutoCommit();
		} catch (RepositoryException e) {
			LOG.error("Commit " + name + " checking error - " + e);
		}
		return status;
	}
	
	/**
	 * Sets auto commit (i.e one commit for each update) on or off.
	 * @param status : Autocommit status.
	 */
	public final void setAutoCommit(boolean status) {
		try {
			connection.setAutoCommit(status);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Commit " + name + " " + (status ? "on" : "off") + " auto.");
			}
			
		} catch (RepositoryException e) {
			LOG.error("Commit " + name + " " + (status ? "on" : "off") + " auto error - " + e);
		}
	}
	
	/**
	 * Rolls back every uncommitted changes to the repository.
	 */
	public final void rollback() {
		try {
			connection.rollback();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Commit " + name + " rollback.");
			}
			
		} catch (RepositoryException e) {
			LOG.error("Commit " + name + " rollback error - " + e);
		}
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getContext() {
		return context;
	}
	
	public final boolean hasContext() {
		return "".equals(context);
	}
	
	/**
	 * Allows to set the logging level for this component.
	 * @param level : The logging level.
	 */
	public void setLoggingLevel(Level level) {
		LOG.setLevel(level);
	}
}
