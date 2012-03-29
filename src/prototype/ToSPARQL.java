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
	public String getOutput() {
		if (output.equals("")) {
			majStatements();
		}
		return output;
	}
	
	public void majStatements() {
		String query;
		try {
			for (String suj : maj.keySet()) {
				query = writeDeleteQuery(suj);
				output += query + "\n";
				destination.updateQuery(query);
				query = writeInsertQuery(suj, maj.get(suj));
				output += query + "\n";
				destination.updateQuery(query);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String writeInsertQuery(String suj, LinkedList<Statement> sts) {
		String ret = "INSERT DATA { <" + suj + ">";
		
		for(Statement s : sts) {
			ret += " " + s.getPredicate().stringValue() + " <" + s.getObject().stringValue() + "> ;";
		}
		return ret.substring(0, ret.length() - 1) + ". }";
	}
	
	public String writeDeleteQuery(String suj) {
		return "DELETE DATA { <" + suj + "> " + prop + " ?o }";
	}
}
