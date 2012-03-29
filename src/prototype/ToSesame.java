package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;

//TODO make it work

/**
 * Classe qui met à jour un serveur Sesame avec de nouveaux triplets
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see To
 */
public class ToSesame extends To {

	private Jeu destination;
	
	public ToSesame(Jeu j, String p) {
		super(j, p);
		destination = j;
	}

	public ToSesame(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
		destination = j;
	}
	
	public ToSesame(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
	}

	@Override
	public String getOutput() {
		if (output.equals("")) majStatements();
		return output;
	}
	
	public void majStatements() {
		LinkedList<Statement> tmp = maj.get(null); //TODO
		LinkedList<Statement> aenlever = new LinkedList<Statement>();
		try {
			for (Statement m : maj.get(null)) { //TODO
				aenlever.addAll(destination.getAllStatements(m.getSubject(), m.getPredicate()).asList());
			}
			destination.removeAllStatements(aenlever);
			output += "- " + aenlever.size() + " - " + aenlever + "\n";
			output += "+ " + tmp.size() + " - " + tmp + "\n";
			//destination.addAllStatements(tmp);
			//TODO utiliser un Set, attention aux doublons, mieux gérer les propriétés
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isNewRepository() {
		return true;
	}

}
