package util;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a SPARQL endpoint using SPARQL queries.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see Output
 */
public class SPARQLOutput extends Output {

	/**
	 * The data set where we're going to make the updates.
	 */
	private DataSet goal;
	/**
	 * Tells whether to write only inserts or delete and inserts.
	 */
	private boolean deleteinsert;
	
	/**
	 * Lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SPARQLOutput(DataSet ds, String p) throws RepositoryException {
		super(ds, p);
		goal = ds;
		deleteinsert = true;
	}

	/**
	 * Default constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SPARQLOutput(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p) throws RepositoryException {
		super(ds, ns, p);
		goal = ds;
		deleteinsert = true;
	}
	
	/**
	 * Full constructor.
	 * @param ds : The old data set.
	 * @param g : A data set to be updated.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SPARQLOutput(DataSet ds, DataSet g, HashMap<String, LinkedList<Statement>> ns, String p, boolean a) throws RepositoryException {
		super(ds, ns, p, a);
		goal = g;
		deleteinsert = false;
		
		//Adds the old namespaces to the new data set.
		for (String namespace : namespaces.keySet()) {
			goal.addNamespace(namespaces.get(namespace), namespace);
		}
	}

	/**
	 * Retrieves the output of the process as SPARQL queries.
	 * @return The update queries.
	 */
	@Override
	public String getOutput() {
		String text = "";
		LinkedList<String> queries = deleteinsert ? getDeleteInsertQueries() : getInsertQueries();
		for (String q : queries) {
			text += q + "\n";
		}
		return text;
	}
	
	/**
	 * Retrieves the SPARQL update delete+insert queries to be used in order to update the data.
	 * @return A linked list of queries as strings.
	 */
	private LinkedList<String> getDeleteInsertQueries() {
		LinkedList<String> queries = new LinkedList<String>();
		for (String subject : newtuples.keySet()) {
			queries.add(writeDeleteInsertQuery(subject));
		}
		
		return queries;
	}
	
	/**
	 * Retrieves the SPARQL update insert queries to be used in order to update the data.
	 * @return A linked list of queries as strings.
	 */
	private LinkedList<String> getInsertQueries() {
		LinkedList<String> queries = new LinkedList<String>();
		for (String subject : newtuples.keySet()) {
			queries.add(writeInsertQuery(subject));
		}
		
		return queries;
	}
	
	/**
	 * Updates the data set by sending SPARQL DELETE/INSERT or INSERT queries.
	 * @throws RepositoryException Error while SPARQL updating statements.
	 * @throws UpdateExecutionException Query failed to update data.
	 * @throws MalformedQueryException Query isn't valid.
	 */
	@Override
	public void updateDataSet() throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		LinkedList<String> queries = deleteinsert ? getDeleteInsertQueries() : getInsertQueries();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Update " + goal.getName() + " using SPARQL queries.");
		}
		
		//On veut être sûr d'effectuer soit tous les changements, soit aucun.
		goal.setAutoCommit(false);
		try {
			for (String q : queries) {
				goal.updateQuery(q);
			}
			goal.commit();
		} 
		catch (RepositoryException e) {
			goal.rollback();
			throw new RepositoryException("While SPARQL updating statements - " + goal.getName(), e);
		} 
		catch (MalformedQueryException e) {
			goal.rollback();
			throw e;
		}
		catch (UpdateExecutionException e) {
			goal.rollback();
			throw e;
		} 
		finally {
			goal.setAutoCommit(true);
		}
	}
	
	/**
	 * Writes a shortened SPARQL DELETE+INSERT query.
	 * @param subject : The query's subject.
	 * @return Query as string.
	 */
	private String writeDeleteInsertQuery(String subject) {
		String query = "DELETE { <" + subject + "> " + linkingpredicate + " ?o } INSERT { <" + subject + ">";
		
		// DELETE + INSERT are combined to optimize bandwith use.
		for (Statement s : newtuples.get(subject)) {
			query += " " + filterPredicate(s.getPredicate()) + " " + filterObject(s.getObject().stringValue()) + " ;";
		}
		return query.substring(0, query.length() - 1) + ". } WHERE { <" + subject + "> " + linkingpredicate + " ?o }";
	}
	
	/**
	 * Writes a SPARQL INSERT query.
	 * @param subject : The query's subject.
	 * @return Query as string.
	 */
	private String writeInsertQuery(String subject) {
		String query = "INSERT DATA { <" + subject + ">";
		
		// Adds multiple tuples with the same subject at the same time.
		for (Statement s : newtuples.get(subject)) {
			query += " " + filterPredicate(s.getPredicate()) + " " + filterObject(s.getObject().stringValue()) + " ;";
		}
		return query.substring(0, query.length() - 1) + ". }";
	}
	
	/**
	 * Writes a SPARQL DELETE query.
	 * @param subject : The query's subject.
	 * @return Query as string.
	 * @deprecated
	 */
	public String writeDeleteQuery(String subject) {
		return "DELETE DATA { <" + subject + "> " + linkingpredicate + " ?o }";
	}
	
	/**
	 * Converts an object into its correct SPARQL syntax.
	 * @param obj : The object as a string.
	 * @return A well-written object.
	 */
	private String filterObject(String obj) {
		String ret;
		if (obj.startsWith("http://")) {
			ret = "<" + obj + ">";
		}
		else if (obj.equals("true") || obj.equals("false")) {
			ret = obj;
		}
		else {
			try {
				ret = "" + Integer.parseInt(obj);
			}
			catch (NumberFormatException e) {
				ret = "\"" + obj + "\"";
			}
		}
		return ret;
	}
}
