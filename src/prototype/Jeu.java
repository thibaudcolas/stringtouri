package prototype;

import java.util.LinkedList;
import java.util.List;

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

/**
 * Abstract class managing a data set from any origin.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see RepositoryConnection
 */
public abstract class Jeu {
	
	/**
	 * Data set name for display.
	 */
	protected String nom;
	/** 
	 * Repository where the data is stored.
	 */
	protected Repository rep;
	/**
	 * Connection to the data set's repository.
	 */
	protected RepositoryConnection con;
	
	/**
	 * History of all the queries submited to the data set.
	 */
	protected LinkedList<String> queries;
	
	/**
	 * Adds a namespace to the repository.
	 * @param label : Namespaces' prefix.
	 * @param uri : Namespaces' full name.
	 * @throws RepositoryException If the new binding fails (repository unwritable).
	 */
	public final void addNamespace(String label, String uri) throws RepositoryException {
		con.setNamespace(label, uri);
	}
	
	/**
	 * Recovers the namespace matching a prefix.
	 * @param pre : The prefix of the namespace.
	 * @return The namespace as a string.
	 * @throws RepositoryException Error while reading the namespace.
	 */
	public final String getNamespace(String pre) throws RepositoryException {
		return con.getNamespace(pre);
	}
	
	/**
	 * Erases all namespaces from the repository.
	 * @return True if there is no more namespace inside the repository.
	 */
	public boolean razNamespaces() {
		boolean succes = true;
		try {
			con.clearNamespaces();
		} catch (RepositoryException e) {
			//XXX logging
			succes = false;
		}
		return succes;
	}
	
	/**
	 * Returns all of the namespaces as a list.
	 * @return A list of namespaces.
	 * @throws RepositoryException Error while reading the namespaces.
	 */
	public final List<Namespace> getNamespaceList() throws RepositoryException {
		return con.getNamespaces().asList();
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
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 * @throws QueryEvaluationException 
	 * XXX logging / remontée ?
	 */
	public TupleQueryResult SPARQLQuery(String query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		System.out.println("Requête " + nom + " : " + query);
		
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, getPrefixes() + query);
	    return tq.evaluate();
	}
	
	/**
	 * Sends an update (delete/insert) SPARQL query to the data set.
	 * @param query : The SPARQL query without its prefixes.
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 * @throws UpdateExecutionException 
	 * XXX logging / remontée ?
	 */
	public void updateQuery(String query) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		System.out.println("Requête " + nom + " : " + query);
		
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		Update up = con.prepareUpdate(QueryLanguage.SPARQL, getPrefixes() + query);
	    up.execute();
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
		return new LinkedList<Statement>(con.getStatements(null, null, null, true).asList());
	}
	
	/**
	 * Recovers statements according to criteria.
	 * @param r : The subject we want to use.
	 * @param u : The predicat to use.
	 * @return Statements which have r as subject and u as predicat.
	 * @throws RepositoryException Problem during retrieval.
	 */
	public final LinkedList<Statement> getAllStatements(Resource r, URI u) throws RepositoryException {
		return new LinkedList<Statement>(con.getStatements(r, u, null, true).asList());
	}
	
	/**
	 * Adds a set of statements to the repository.
	 * @param sts : The statements to add.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void addAllStatements(Iterable<Statement> sts) throws RepositoryException {
		con.add(sts);
	}
	
	/**
	 * Adds a single statement to the repository.
	 * @param s : The statement.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void addStatement(Statement s) throws RepositoryException {
		con.add(s);
	}
	
	/**
	 * Removes numerous statements from the repository according to criteria.
	 * @param r : The subject we want to use.
	 * @param u : The predicat to use.
	 * @throws RepositoryException Repository not writable.
	 */
	public final void removeStatements(Resource r, URI u) throws RepositoryException {
		con.remove(r, u, null);
	}
	
	/**
	 * Stops properly the connection to the repository and shuts down the repository itself.
	 */
	public final void shutdown() {
		try {
			con.close();
			rep.shutDown();
			
			System.out.println("Connexion " + nom + " " + (con.isOpen() ? "toujours en cours" : "terminée") + ".");
			
		} catch (RepositoryException e) {
			//XXX logging (+ remontée ?)
			System.err.println("Error while closing both repository and connection - " + e);
		}
	}
	
	/**
	 * Commits all of the changes which have not been commited yet.
	 */
	public final void commit() {
		try {
			con.commit();
		} catch (RepositoryException e) {
			//XXX logging
			System.err.println("Error while committing - " + e);
		}
	}
	
	/**
	 * Checks wheter autocommit is on or off.
	 * @return Status of autocommit.
	 */
	public final boolean isAutoCommit() {
		boolean ret;
		try {
			ret = con.isAutoCommit();
		} catch (RepositoryException e) {
			//XXX logging
			System.err.println("Error while checking autocommit status - " + e);
			ret = false;
		}
		return ret;
	}
	
	/**
	 * Sets auto commit (i.e one commit for each update) on or off.
	 * @param on : Autocommit status.
	 */
	public final void setAutoCommit(boolean on) {
		try {
			con.setAutoCommit(on);
		} catch (RepositoryException e) {
			//XXX logging
			System.err.println("Error while setting autocommit mode - " + e);
			System.err.println("Autocommit is " + isAutoCommit() + ", was set to " + on);
		}
	}
	
	/**
	 * Rolls back every uncommitted changes to the repository.
	 */
	public final void rollback() {
		try {
			con.rollback();
		} catch (RepositoryException e) {
			//XXX logging
			System.err.println("Error while rollbacking - " + e);
		}
	}
	
	public final String getNom() {
		return nom;
	}
}
