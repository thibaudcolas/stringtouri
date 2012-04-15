package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
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
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 * Abstract class managing a data set from any origin.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see RepositoryConnection
 */
public abstract class DataSet {
	
	/**
	 * Data set name for display.
	 */
	protected String name;
	/** 
	 * Repository where the data is stored.
	 */
	protected Repository repository;
	/**
	 * Connection to the data set's repository.
	 */
	protected RepositoryConnection connection;
	
	/**
	 * History of all the queries submited to the data set.
	 */
	protected LinkedList<String> queries;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(DataSet.class.getName());

	/**
	 * Super-class constructor used to log initialization of the data sets.
	 * @param n : name of the data set.
	 */
	protected DataSet(String n) {
		name = n;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Created data set " + name + ".");
		}
	}
	
	/**
	 * Adds given file(s) to the repository.
	 * @param path : Path to the given folder/file.
	 * @param prefix : String to filter filenames with.
	 * @param baseuri : Base URI for the new data.
	 * @throws IOException If the submitted filepath isn't usable.
	 * @throws RDFParseException If the RDF data inside the file(s) isn't well-formed.
	 * @throws RepositoryException Internal repository error.
	 */
	public void addTuples(String path, String prefix, String baseuri) throws RepositoryException, RDFParseException, IOException {
		File source = new File(path);
		int nbimport = 0;
		
		if (source.exists()) {
			// If source is a folder, we'll get every rdf file within.
			if (source.isDirectory()) {
				
				FilenameFilter filenamefilter = new FilenameFilter() {
				    public boolean accept(File folder, String filename) {
				        return !filename.startsWith(".") && filename.endsWith(".rdf");
				    }
				};
				
				File[] rdflist = source.listFiles(filenamefilter);
				for (File f : rdflist) {
					if (f.getName().startsWith(prefix)) {
						// RDFXML tuples are added to the repository.
						connection.add(f, baseuri, RDFFormat.RDFXML);
						nbimport++;
					}
				}	
			}
			// If source is a single file.
			else {
				connection.add(source, baseuri, RDFFormat.RDFXML);
				nbimport++;
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("Import " + name + " : " + nbimport + " file(s).");
			}
		}
		else {
			throw new FileNotFoundException("Import " + name + " : " + path + " not found.");
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
	public void resetNamespaces() throws RepositoryException {
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
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Query " + name + " select - " + query);
		}
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		
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
		if (LOG.isInfoEnabled()) {
			LOG.info("Query " + name + " update - " + query);
		}
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		
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
	 * Retrieves the last query sent to the data set.
	 * @return A query as a string.
	 */
	public final String getLastQuery() {
		return queries.getLast(); 
	}
	
	/**
	 * Retrieves all the queries ever made to the data set.
	 * @return A list of queries.
	 */
	public final LinkedList<String> getQueries() {
		return queries;
	}
	
	/**
	 * Gives all of the statements inside the repository.
	 * @return A linked list containing all the statements.
	 * @throws RepositoryException Problem during retrieval.
	 */
	public final LinkedList<Statement> getAllStatements() throws RepositoryException {
		return new LinkedList<Statement>(connection.getStatements(null, null, null, true).asList());
	}
	
	/**
	 * Recovers statements according to criteria.
	 * @param subject : The subject we want to use.
	 * @param predicate : The predicat to use.
	 * @return Statements which have r as subject and u as predicat.
	 * @throws RepositoryException Problem during retrieval.
	 */
	public final LinkedList<Statement> getAllStatements(Resource subject, URI predicate) throws RepositoryException {
		return new LinkedList<Statement>(connection.getStatements(subject, predicate, null, true).asList());
	}
	
	/**
	 * Adds a set of statements to the repository.
	 * @param statements : The statements to add.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void addAllStatements(Iterable<Statement> statements) throws RepositoryException {
		connection.add(statements);
	}
	
	/**
	 * Adds a single statement to the repository.
	 * @param statement : The statement.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void addStatement(Statement statement) throws RepositoryException {
		connection.add(statement);
	}
	
	/**
	 * Removes numerous statements from the repository according to criteria.
	 * @param subject : The subject we want to use.
	 * @param predicate : The predicat to use.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void removeStatements(Resource subject, URI predicate) throws RepositoryException {
		connection.remove(subject, predicate, null);
	}
	
	/**
	 * Stops properly the connection to the repository and shuts down the repository itself.
	 */
	public final void shutdown() {
		try {
			connection.close();
			repository.shutDown();
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Connection " + name + " " + (connection.isOpen() ? "still on" : "off") + ".");
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
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Commit " + name + ".");
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
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Commit " + name + " " + (status ? "on" : "off") + " auto.");
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
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Commit " + name + " rollback.");
			}
			
		} catch (RepositoryException e) {
			LOG.error("Commit " + name + " rollback error - " + e);
		}
	}
	
	public final String getName() {
		return name;
	}
}
