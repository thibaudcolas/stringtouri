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
		try {
			for (String suj : maj.keySet()) {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String writeDeleteQuery() {
		return "";
	}
	
	public String writeInsertQuery() {
		return "";
	}
	
	public String writeDeleteInsertQuery(Statement s) {
		String ret = "DELETE {<"+s.getSubject().stringValue()+"> "+s.getPredicate().stringValue()+" ?o} "
				+ "INSERT {<"+s.getSubject().stringValue()+"> "+s.getPredicate().stringValue()+" <"+s.getObject().stringValue()+">} "
				+ "WHERE {<"+s.getSubject().stringValue()+"> "+s.getPredicate().stringValue()+" ?o}"; 
		return ret;
	}
}
