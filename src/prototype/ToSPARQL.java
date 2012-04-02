package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a SPARQL endpoint using SPARQL queries.
 * 
 * @author Thibaud Colas
 * @version 01042012
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
	 */
	public ToSPARQL(Jeu j, String p) {
		super(j, p);
		destination = j;
		deleteinsert = true;
	}

	/**
	 * Default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 */
	public ToSPARQL(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
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
	 */
	public ToSPARQL(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
		deleteinsert = false;
		
		//Ajout des namespaces de l'ancien jeu dans le nouveau.
		for (String ns : namespaces.keySet()) {
			try {
				destination.addNamespace(namespaces.get(ns), ns);
			} catch (RepositoryException e) {
				System.err.println("Erreur en exportant les namespaces - " + e);
			}
		}
	}

	/**
	 * Retrieves the output of the process as statements.
	 * @param executer : Tells whether or not to submit the queries to the dataset.
	 * @return The update queries.
	 */
	@Override
	public String getOutput(boolean executer) {
		if (output.equals("")) {
			if (executer) {
				majStatements();
			}
			else {
				output = getRequetes();
			}
		}
		return output;
	}
	
	/**
	 * Retrieves the SPARQL update queries to be used in order to update the data.
	 * @return Queries as string.
	 */
	private String getRequetes() {
		String ret = "";
		for (String suj : maj.keySet()) {
			if (deleteinsert) {
				ret += writeDeleteInsertQuery(suj, maj.get(suj));
			}
			else { 
				ret += writeInsertQuery(suj, maj.get(suj));
			}
			ret += "\n";
		}
		
		return ret;
	}
	
	/**
	 * Updates the data set by sending SPARQL DELETE/INSERT queries.
	 */
	public void majStatements() {
		String query;
		try {
			for (String suj : maj.keySet()) {
				if (deleteinsert) {
					query = writeDeleteInsertQuery(suj, maj.get(suj));
				}
				else { 
					query = writeInsertQuery(suj, maj.get(suj));
				}
				output += query + "\n";
				destination.updateQuery(query);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		return (o.startsWith("http://") ? "<" + o + ">" 
				: o.equals("true") || o.equals("false") ? o 
						: "\"" + o + "\"");
	}
}
