package prototype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Abstract class to process the result of the interlinking.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Linkage
 */
public abstract class To {
	
	/**
	 * The data set to use.
	 */
	protected DataSet jeumaj;
	/**
	 * The new statements produced by the interlinking process.
	 */
	protected HashMap<String, LinkedList<Statement>> maj;
	/**
	 * The predicate where linking has been made.
	 */
	protected String prop;
	/**
	 * Namespaces to be used during the process.
	 */
	protected HashMap<String, String> namespaces;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(To.class.getName());
	
	/**
	 * Super-class lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected To(DataSet j, String p) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Creation  Output " + j.getNom() + ".");
		}
		
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = new HashMap<String, LinkedList<Statement>>();
	}
	
	/**
	 * Super-class default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected To(DataSet j, HashMap<String, LinkedList<Statement>> m, String p) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Creation  Output " + j.getNom() + ".");
		}
		
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = m;
	}
	
	/**
	 * Super-class alternative constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	protected To(DataSet j, HashMap<String, LinkedList<Statement>> m, String p, boolean a) throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Output " + j.getNom() + " creation.");
		}
		
		jeumaj = j;
		handleNamespaces();
		prop = p;
		maj = a ? getFilteredStatements(m) : m;
	}
	
	/**
	 * Imports the namespaces to be used later.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	private void handleNamespaces() throws RepositoryException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Export " + jeumaj.getNom() + " namespace retrieval.");
		}
		try {
			namespaces = new HashMap<String, String>();
			List<Namespace> nstmp = jeumaj.getNamespaceList();
			for (Namespace n : nstmp) {
				namespaces.put(n.getName(), n.getPrefix());
			}
		} catch (RepositoryException e) {
			throw new RepositoryException("While fetching namespaces", e);
		}
	}
	
	/**
	 * Used to shorten a predicate into its local version.
	 * @param p : The predicate to convert.
	 * @return The predicate using a local name.
	 */
	protected String filterPredicate(URI p) {
		String ns = p.getNamespace();
		return (ns.startsWith("http://") ? namespaces.get(ns) + ":" : ns) + p.getLocalName();
	}
	
	/**
	 * Filters statements inside the data set to update the new ones.
	 * @param nouv : The new statements.
	 * @return A set of up to date statements.
	 * @throws RepositoryException Error while fetching statements.
	 */
	protected final HashMap<String, LinkedList<Statement>> getFilteredStatements(HashMap<String, LinkedList<Statement>> nouv) throws RepositoryException {
		HashMap<String, LinkedList<Statement>> resultat = getAllStatements();
		LinkedList<Statement> tmpold;
		LinkedList<Statement> tmpnew;
		String tmpprop;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Export " + jeumaj.getNom() + " statement filtering.");
		}
		
		for (String suj : nouv.keySet()) {
			tmpold = resultat.get(suj);
			if (tmpold != null) {
				tmpnew = new LinkedList<Statement>();
				// Pour tous les anciens triplets.
				for (Statement s : tmpold) {
					tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
					if (!tmpprop.equals(prop)) {
						// On ne prend que ceux qui n'ont pas la propriété qui nous intéresse.
						tmpnew.add(s);
					}
				}
				tmpnew.addAll(nouv.get(suj));
				resultat.put(suj, tmpnew);
			}
		}
		return resultat;
	}
	
	/**
	 * Retrieves every statement inside the data set.
	 * @return Hashmap of all the statements grouped by subject.
	 * @throws RepositoryException Error while fetching the statements.
	 */
	private HashMap<String, LinkedList<Statement>> getAllStatements() throws RepositoryException {
		HashMap<String, LinkedList<Statement>> resultat = new HashMap<String, LinkedList<Statement>>();
		
		LinkedList<Statement> tous = jeumaj.getAllStatements();
		LinkedList<Statement> tmpmaj;
		String tmpsuj;
		for (Statement s : tous) {
			tmpsuj = s.getSubject().stringValue();
			tmpmaj = resultat.get(tmpsuj);
			if (tmpmaj == null) {
				tmpmaj = new LinkedList<Statement>();
				resultat.put(tmpsuj, tmpmaj);
			}
			tmpmaj.add(s);
		}
		return resultat;
	}

	public final HashMap<String, LinkedList<Statement>> getMaj() {
		return maj;
	}

	public final void setMaj(HashMap<String, LinkedList<Statement>> m, boolean a) throws RepositoryException {
		this.maj = a ? getFilteredStatements(m) : m;
	}

	/**
	 * Abstract generic method to retrieve the output of the process.
	 * @return The output of the interlinking.
	 */
	public abstract String getOutput();
	
	/**
	 * Abstract generic method to update the data set.
	 * @throws UpdateExecutionException Query failed to update data.
	 * @throws MalformedQueryException Query isn't valid.
	 * @throws RepositoryException Fatal error while updating the data set.
	 */
	public abstract void majStatements() throws RepositoryException, MalformedQueryException, UpdateExecutionException;
}
