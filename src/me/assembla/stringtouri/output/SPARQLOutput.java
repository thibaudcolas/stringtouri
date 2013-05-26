package me.assembla.stringtouri.output;

import java.util.LinkedList;

import me.assembla.stringtouri.dataset.Dataset;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a SPARQL endpoint using SPARQL queries.
 * 
 * @author Thibaud Colas
 * @version 21092012
 * @see Output
 */
public class SPARQLOutput extends Output {

	/**
	 * The data set where we're going to make the updates.
	 */
	private Dataset goal;
	
	/**
	 * Lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SPARQLOutput(Dataset ds, String cont, String p, String np) throws RepositoryException {
		super(ds, cont, p, np);
		goal = ds;
	}
	
	/**
	 * Retrieves the SPARQL update delete+insert queries to be used in order to update the data.
	 * @return A linked list of queries as strings.
	 */
	private LinkedList<String> getDeleteInsertQueries() {
		LinkedList<String> queries = new LinkedList<String>();
		String cont = "".equals(context) ? "" : "WITH <" + context + "> ";
		for (String subject : newtuples.keySet()) {
			queries.add(writeDeleteInsertQuery(subject, cont));
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
		LinkedList<String> queries = "".equals(newPredicate) ? getDeleteInsertQueries() : getInsertQueries();

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
			throw new RepositoryException("While SPARQL updating statements - " + goal.getName() + " " +  e, e);
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
	private String writeDeleteInsertQuery(String subject, String cont) {
		String query =  cont + "DELETE { <" + subject + "> <" + linkingpredicate + "> ?o } INSERT { <" + subject + ">";
		
		// DELETE + INSERT are combined to optimize bandwith use.
		LinkedList<Statement> updatedTuples = newtuples.get(subject);
		for (Statement s : updatedTuples) {
			query += " " + filterPredicate(s.getPredicate()) + " " + filterObject(s.getObject().stringValue()) + " ;";
		}
		return query.substring(0, query.length() - 1) + ". } WHERE { <" + subject + "> <" + linkingpredicate + "> ?o }";
	}
	
	/**
	 * Writes a SPARQL INSERT query.
	 * @param subject : The query's subject.
	 * @return Query as string.
	 */
	private String writeInsertQuery(String subject) {
		String query = "INSERT DATA" 
					+ ("".equals(context) ? "" : " { GRAPH <" + context + ">") 
					+ " { <" + subject + ">";
		
		// Adds multiple tuples with the same subject at the same time.
		LinkedList<Statement> updatedTuples = newtuples.get(subject);
		for (Statement s : updatedTuples) {
			query += " " + newPredicateWritable + " " + filterObject(s.getObject().stringValue()) + " ;";
		}
		return query.substring(0, query.length() - 1) + ". }" + ("".equals(context) ? "" : "}");
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
