package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a SPARQL endpoint using SPARQL queries.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see To
 */
public class ToSPARQL extends To {

	/**
	 * The data set where we're going to make the updates.
	 */
	private Jeu destination;
	/**
	 * Tells whether to write only inserts or delete and inserts.
	 */
	private boolean deleteinsert;
	
	/**
	 * Lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSPARQL(Jeu j, String p) throws RepositoryException {
		super(j, p);
		destination = j;
		deleteinsert = true;
	}

	/**
	 * Default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSPARQL(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) throws RepositoryException {
		super(j, m, p);
		destination = j;
		deleteinsert = true;
	}
	
	/**
	 * Full constructor.
	 * @param j : The old data set.
	 * @param js : A data set to be updated.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSPARQL(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) throws RepositoryException {
		super(j, m, p, a);
		destination = js;
		deleteinsert = false;
		
		//Ajout des namespaces de l'ancien jeu dans le nouveau.
		for (String ns : namespaces.keySet()) {
			destination.addNamespace(namespaces.get(ns), ns);
		}
	}

	/**
	 * Retrieves the output of the process as SPARQL queries.
	 * @return The update queries.
	 */
	@Override
	public String getOutput() {
		String ret = "";
		LinkedList<String> queries = deleteinsert ? getDeleteInsertQueries() :getInsertQueries();
		for (String q : queries)
		{
			ret += q + "\n";
		}
		return ret;
	}
	
	/**
	 * Retrieves the SPARQL update delete+insert queries to be used in order to update the data.
	 * @return A linked list of queries as strings.
	 */
	private LinkedList<String> getDeleteInsertQueries() {
		LinkedList<String> queries = new LinkedList<String>();
		for (String suj : maj.keySet()) {
			queries.add(writeDeleteInsertQuery(suj, maj.get(suj)));
		}
		
		return queries;
	}
	
	/**
	 * Retrieves the SPARQL update insert queries to be used in order to update the data.
	 * @return A linked list of queries as strings.
	 */
	private LinkedList<String> getInsertQueries() {
		LinkedList<String> queries = new LinkedList<String>();
		for (String suj : maj.keySet()) {
			queries.add(writeInsertQuery(suj, maj.get(suj)));
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
	public void majStatements() throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		LinkedList<String> queries = deleteinsert ? getDeleteInsertQueries() :getInsertQueries();

		if (log.isInfoEnabled()) {
			log.info("Update " + destination.getNom() + " using SPARQL queries.");
		}
		
		//On veut être sûr d'effectuer soit tous les changements, soit aucun.
		destination.setAutoCommit(false);
		try {
			for (String q : queries) {
					destination.updateQuery(q);
			}
			destination.commit();
		} 
		catch (RepositoryException e) {
			destination.rollback();
			throw new RepositoryException("While SPARQL updating statements - " + destination.getNom(), e);
		} 
		catch (MalformedQueryException e) {
			destination.rollback();
			throw e;
		}
		catch (UpdateExecutionException e) {
			destination.rollback();
			throw e;
		} 
		finally {
			destination.setAutoCommit(true);
		}
	}
	
	/**
	 * Writes a shortened SPARQL DELETE+INSERT query.
	 * @param suj : The query's subject.
	 * @param sts : Statements to be used.
	 * @return Query as string.
	 */
	public String writeDeleteInsertQuery(String suj, LinkedList<Statement> sts) {
		String ret = "DELETE { <" + suj + "> " + prop + " ?o } INSERT { <" + suj + ">";
		String tmpprop;
		// DELETE + INSERT combiné pour optimiser l'utilisation du réseau.
		for (Statement s : maj.get(suj)) {
			tmpprop = filterPredicate(s.getPredicate());
			ret += " " + tmpprop + " <" + s.getObject().stringValue() + "> ;";
		}
		return ret.substring(0, ret.length() - 1) + ". } WHERE { <" + suj + "> " + prop + " ?o }";
	}
	
	/**
	 * Writes a SPARQL INSERT query.
	 * @param suj : The query's subject.
	 * @param sts : Statements to be used.
	 * @return Query as string.
	 */
	public String writeInsertQuery(String suj, LinkedList<Statement> sts) {
		String ret = "INSERT DATA { <" + suj + ">";
		String tmpprop;
		
		// C'est un INSERT qui ajoute plusieurs triplets à la fois pour un même sujet.
		for (Statement s : sts) {
			tmpprop = filterPredicate(s.getPredicate());
			ret += " " + tmpprop + " " + filterObject(s.getObject()) + " ;";
		}
		return ret.substring(0, ret.length() - 1) + ". }";
	}
	
	/**
	 * Writes a SPARQL DELETE query.
	 * @param suj : The query's subject.
	 * @return Query as string.
	 */
	public String writeDeleteQuery(String suj) {
		return "DELETE DATA { <" + suj + "> " + prop + " ?o }";
	}
	
	/**
	 * Converts an object into its correct SPARQL syntax.
	 * @param v : The object.
	 * @return A well-written object.
	 */
	protected String filterObject(Value v) {
		String o = v.stringValue();
		//FIXME Gestion des valeurs littérales
		return (o.startsWith("http://") ? "<" + o + ">" 
				: o.equals("true") || o.equals("false") ? o 
						: "\"" + o + "\"");
	}
}
