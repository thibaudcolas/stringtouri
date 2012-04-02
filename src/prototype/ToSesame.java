package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a SESAME server with new statements.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see To
 */
public class ToSesame extends To {

	/**
	 * The data set where we're going to make the updates.
	 */
	private Jeu destination;
	
	/**
	 * Lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 */
	public ToSesame(Jeu j, String p) {
		super(j, p);
		destination = j;
	}

	/**
	 * Default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 */
	public ToSesame(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
		destination = j;
	}
	
	/**
	 * Full constructor.
	 * @param j : The old data set.
	 * @param js : A data set to be updated.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 */
	public ToSesame(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
	}

	/**
	 * Retrieves the output of the process as statements.
	 * @param executer : Tells whether or not to execute the output on the data set.
	 * @return The modified statements.
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
	
	/**
	 * Retrieves what's going to be changed and how (added, removed).
	 * @return String describing the update.
	 */
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
	
	//FIXME Toujours le même problème avec/sans <>.
	/**
	 * Updates the data set statements by overwriting the old ones with the new ones.
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
}
