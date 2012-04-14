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
	private DataSet destination;
	
	/**
	 * Lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSesame(DataSet j, String p) throws RepositoryException {
		super(j, p);
		destination = j;
	}

	/**
	 * Default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSesame(DataSet j, HashMap<String, LinkedList<Statement>> m, String p) throws RepositoryException {
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
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public ToSesame(DataSet j, DataSet js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) throws RepositoryException {
		super(j, m, p, a);
		destination = js;
	}

	/**
	 * Retrieves the output of the process as statements.
	 * @return The modified statements.
	 */
	@Override
	public String getOutput() {
		return getModifs();
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
	
	//FIXME URI propriété écrite avec/sans <>.
	/**
	 * Updates the data set statements by overwriting the old ones with the new ones.
	 * @throws RepositoryException Fatal error while updating the statements.
	 */
	public void majStatements() throws RepositoryException {
		LinkedList<Statement> tmpnew;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Update " + destination.getNom() + " using Sesame statements.");
		}
		
		try {
			for (String suj : maj.keySet()) {
				tmpnew = maj.get(suj);
				for (Statement s : tmpnew) {
					destination.removeStatements(s.getSubject(), s.getPredicate());
				}
				destination.addAllStatements(tmpnew);
			}
		} catch (RepositoryException e) {
			throw new RepositoryException("While updating statements - " + destination.getNom(), e);
		}
	}
}
