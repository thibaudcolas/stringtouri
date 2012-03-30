package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Classe qui met à jour un serveur Sesame avec de nouveaux triplets.
 * 
 * @author Thibaud Colas
 * @version 30032012
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

	/**
	 * Si executer = faux, alors on récupère les changements sans les exécuter.
	 * @param executer : Dit si on va exécuter les modifcations.
	 * @return : Les changements dans les triplets.
	 */
	@Override
	public String getOutput(boolean executer) {
		if (output.equals("")) {
			if (executer) {
				majStatements();
			}
			else {
				output = getModifs();
			}
		}
		return output;
	}
	
	//FIXME Toujours le même problème avec/sans <>.
	/**
	 * Modifie les triplets du jeu en remplaçant les anciens par les nouveaux.
	 */
	public void majStatements() {
		LinkedList<Statement> tmpnew;
		try {
			for (String suj : maj.keySet()) {
				tmpnew = maj.get(suj);
				for (Statement s : tmpnew) {
					destination.removeStatements(s.getSubject(), s.getPredicate());
					output += "- " + s + "\n";
				}
				destination.addAllStatements(tmpnew);
				output += "+ " + tmpnew + "\n";
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private String getModifs() {
		LinkedList<Statement> tmpnew;
		String ret = "";
		for (String suj : maj.keySet()) {
			tmpnew = maj.get(suj);
			for (Statement s : tmpnew) {
				ret += "- " + s + "\n";
			}
			ret += "+ " + tmpnew + "\n";
		}
		return ret;
	}
}
