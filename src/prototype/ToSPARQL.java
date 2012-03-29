package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;

/**
 * Classe qui met à jour un SPARQL endpoint avec des requêtes SPARQL.
 * 
 * @author Thibaud Colas
 * @version 29032012
 * @see To
 */
public class ToSPARQL extends To {

private Jeu destination;
	
	public ToSPARQL(Jeu j, String p) {
		super(j, p);
		destination = j;
	}

	public ToSPARQL(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
		destination = j;
	}
	
	public ToSPARQL(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
	}

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
			ret += writeDeleteInsertQuery(suj, maj.get(suj)) + "\n";
		}
		
		return ret;
	}
	
	public void majStatements() {
		String query;
		try {
			for (String suj : maj.keySet()) {
				query = writeDeleteInsertQuery(suj, maj.get(suj));
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
		String ret = "DELETE { <"+suj+"> "+prop+" ?o } INSERT { <"+suj+">";
		String tmpprop;
		// DELETE + INSERT combiné pour optimiser l'utilisation du réseau.
		for (Statement s : maj.get(suj)) {
			tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
			ret += " " + tmpprop + " <" + s.getObject().stringValue() + "> ;";
		}
		return ret.substring(0, ret.length() - 1) + ". } WHERE { <"+suj+"> "+prop+" ?o }";
	}
	
	public String writeInsertQuery(String suj, LinkedList<Statement> sts) {
		String ret = "INSERT DATA { <" + suj + ">";
		
		// C'est un INSERT qui ajoute plusieurs triplets à la fois pour un même sujet.
		for(Statement s : sts) {
			ret += " " + s.getPredicate().stringValue() + " <" + s.getObject().stringValue() + "> ;";
		}
		return ret.substring(0, ret.length() - 1) + ". }";
	}
	
	public String writeDeleteQuery(String suj) {
		return "DELETE DATA { <" + suj + "> " + prop + " ?o }";
	}
}
