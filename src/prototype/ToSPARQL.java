package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

/**
 * Classe qui met à jour un SPARQL endpoint avec des requêtes SPARQL.
 * 
 * @author Thibaud Colas
 * @version 29032012
 * @see To
 */
public class ToSPARQL extends To {

private Jeu destination;
private boolean deleteinsert;
	
	public ToSPARQL(Jeu j, String p) {
		super(j, p);
		destination = j;
		deleteinsert = true;
	}

	public ToSPARQL(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
		destination = j;
		deleteinsert = true;
	}
	
	public ToSPARQL(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
		deleteinsert = false;
		
		for(String ns : namespaces.keySet()) {
			try {
				destination.addNamespace(namespaces.get(ns), ns);
			} catch (RepositoryException e) {
				System.err.println("Erreur en exportant les namespaces - " + e);
			}
		}
	}

	/**
	 * Si executer = faux, alors on récupère les requêtes sans les exécuter.
	 * @param executer : Dit si on va exécuter les modifcations.
	 * @return : Les requêtes qui modifieront correctement les données.
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
	
	private String getRequetes() {
		String ret = "";
		for (String suj : maj.keySet()) {
			if(deleteinsert) {
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
	 * Envoie des requêtes SPARQL UPDATE pour supprimer/insérer des données.
	 */
	public void majStatements() {
		String query;
		try {
			for (String suj : maj.keySet()) {
				if(deleteinsert) {
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
	 * Écrit une requête optimisée pour avoir l'usage le plus faible possible du réseau.
	 * @param suj : Le sujet de la requête.
	 * @param sts : Les triplets modifiés par la requête.
	 * @return La requête sous forme de texte.
	 */
	public String writeDeleteInsertQuery(String suj, LinkedList<Statement> sts) {
		String ret = "DELETE { <" + suj + "> " + prop + " ?o } INSERT { <" + suj + ">";
		String tmpprop;
		// DELETE + INSERT combiné pour optimiser l'utilisation du réseau.
		for (Statement s : maj.get(suj)) {
			tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
			ret += " " + tmpprop + " <" + s.getObject().stringValue() + "> ;";
		}
		return ret.substring(0, ret.length() - 1) + ". } WHERE { <" + suj + "> " + prop + " ?o }";
	}
	
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
	
	public String writeDeleteQuery(String suj) {
		return "DELETE DATA { <" + suj + "> " + prop + " ?o }";
	}
	
	/**
	 * Utilisé pour convertir un objet dans sa forme requêtable.
	 * @param v : L'objet en question
	 * @return Un objet écrit proprement
	 */
	protected String filterObject(Value v) {
		String o = v.stringValue();
		return (o.startsWith("http://") ? "<" + o + ">" 
				: o.equals("true") || o.equals("false") ? o 
						: "\"" + o + "\"");
	}
}
