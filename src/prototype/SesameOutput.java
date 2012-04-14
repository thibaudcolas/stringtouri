package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Updates a Sesame server with new statements.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Output
 */
public class SesameOutput extends Output {

	/**
	 * The data set where we're going to make the updates.
	 */
	private DataSet goal;
	
	/**
	 * Lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SesameOutput(DataSet ds, String p) throws RepositoryException {
		super(ds, p);
		goal = ds;
	}

	/**
	 * Default constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SesameOutput(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p) throws RepositoryException {
		super(ds, ns, p);
		goal = ds;
	}
	
	/**
	 * Full constructor.
	 * @param ds : The old data set.
	 * @param g : A data set to be updated.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public SesameOutput(DataSet ds, DataSet g, HashMap<String, LinkedList<Statement>> ns, String p, boolean a) throws RepositoryException {
		super(ds, ns, p, a);
		goal = g;
	}

	/**
	 * Retrieves the output of the process as statements.
	 * @return The modified statements.
	 */
	@Override
	public String getOutput() {
		return getNewLinks();
	}
	
	/**
	 * Retrieves what's going to be changed and how (added, removed).
	 * @return String describing the update.
	 */
	private String getNewLinks() {
		LinkedList<Statement> newlinks;
		String text = "";
		for (String suj : newtuples.keySet()) {
			newlinks = newtuples.get(suj);
			for (Statement s : newlinks) {
				text += "- " + s + "\n";
			}
			text += "+ " + newlinks + "\n";
		}
		return text;
	}
	
	//FIXME URI propriété écrite avec/sans <>.
	/**
	 * Updates the data set statements by overwriting the old ones with the new ones.
	 * @throws RepositoryException Fatal error while updating the statements.
	 */
	public void updateDataSet() throws RepositoryException {
		LinkedList<Statement> newlinks;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Update " + goal.getName() + " using Sesame statements.");
		}
		
		try {
			for (String subject : newtuples.keySet()) {
				newlinks = newtuples.get(subject);
				for (Statement s : newlinks) {
					goal.removeStatements(s.getSubject(), s.getPredicate());
				}
				goal.addAllStatements(newlinks);
			}
		} catch (RepositoryException e) {
			throw new RepositoryException("While updating statements - " + goal.getName(), e);
		}
	}
}
