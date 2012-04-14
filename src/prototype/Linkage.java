package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;


/**
 * Abstract class interlinking two data sets according to criteria.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see DataSet
 */
public abstract class Linkage {
	
	public static final String SVAR = "s";
	public static final String PVAR = "p";
	public static final String OVAR = "o";
	
	/**
	 * Linkage name for display purposes.
	 */
	protected String nom;
	/**
	 * Source data for new links.
	 */
	protected DataSet source;
	/**
	 * Target data for new links.
	 */
	protected DataSet cible;
	/**
	 * Source predicate to lookup.
	 */
	protected String propsource;
	/**
	 * Target predicate which will be modified.
	 */
	protected String propcible;
	
	/**
	 * Query submited to the source data.
	 */
	protected String querysource;
	/**
	 * Query submited to the target data.
	 */
	protected String querycible;
	
	/**
	 * Max number of new links to be made.
	 */
	protected int maxliens;
	
	/**
	 * Logger to record actions on the data set.
	 */
	protected static final Logger LOG = Logger.getLogger(Linkage.class.getName());
	
	private static final int DEFSIZE = 100;
	
	/**
	 * Super-class constructor used to log initialization of the linkages.
	 * @param s : The source data set.
	 * @param c : The target data set.
	 * @param ps : The source predicate.
	 * @param pc : The target predicate.
	 */
	protected Linkage(DataSet s, DataSet c, String ps, String pc) {
		nom = ps + " - " + pc;
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Creation  Liaison " + nom + ".");
		}
		
		source = s;
		cible = c;
		propsource = ps;
		propcible = pc;
	}
	
	/**
	 * Retrieves data for the source predicate from the source data set.
	 * One value = one URI, one URI = one value.
	 * @return HashMap where the key is the value for the source predicate and value is the associated URI (subject).
	 * @throws QueryEvaluationException Issue with the source query.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, String> getSourceData() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, String> result = new HashMap<String, String>(DEFSIZE + maxliens);
		TupleQueryResult tupqres = null;
		BindingSet bs;
		
		try {
			tupqres = source.SPARQLQuery(querysource);

			if (!hasCorrectBindingNames(tupqres)) {
				throw new MalformedQueryException("Wrong query result bindings - " + querysource);
			}
			
			int cpt = 0;
			// Pour toutes les lignes de résultat.
			while (tupqres.hasNext()) {
				cpt++;
				bs = tupqres.next();
				result.put(bs.getValue(OVAR).stringValue(), bs.getValue(SVAR).stringValue());
			}
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Predicate " + propsource + " has " + cpt + " statement(s) in " + source.getNom() + ".");
			}
		}
		catch (QueryEvaluationException e) {
			throw new QueryEvaluationException("Query : " + querysource, e);
		}
		catch (RepositoryException e) {
			throw new RepositoryException("Repository issue while fetching data - " + propsource, e);
		}
		catch (MalformedQueryException e) {
			throw new MalformedQueryException("Query : " + querysource, e);
		}
		finally {
			if (tupqres != null) {
				tupqres.close();
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves data from the target data set which we want to update.
	 * One value is associated with multiple URIs, one URI can be associated with multiple values.
	 * @return Hashmap where a value is associated with the URIs having this value for the target predicate.
	 * @throws QueryEvaluationException Issue with the target query.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, LinkedList<String>> getCibleData() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>(DEFSIZE + maxliens);
		TupleQueryResult tupqres = null;
		BindingSet bs;
		String obj;
		LinkedList<String> subjects;
		
		try {
			tupqres = cible.SPARQLQuery(querycible);
			
			if (!hasCorrectBindingNames(tupqres)) {
				throw new QueryEvaluationException("Wrong query result bindings - " + querycible);
			}

			int cpt = 0;
			// Pour toutes les lignes de résultat.
			while (tupqres.hasNext()) {
				cpt++;
				bs = tupqres.next();
				obj = bs.getValue(OVAR).stringValue();
				
				// Si la valeur est déjà présente dans le jeu, on prend les URI associées et on va en rajouter une.
				// Si la valeur n'est pas encore référencée, on ajoute l'URI de l'objet qui l'utilise.
				if (result.containsKey(obj)) {
					subjects = new LinkedList<String>(result.get(obj));
				}
				else {
					subjects = new LinkedList<String>();
				}
				
				subjects.add(bs.getValue(SVAR).stringValue());
				result.put(obj, subjects);
			}
			
			if (LOG.isInfoEnabled()) {
				LOG.info("Predicate " + propcible + " has " + cpt + " statement(s) in " + cible.getNom() + ".");
			}
		}
		catch (RepositoryException e) {
			throw new RepositoryException("While fetching data - " + propsource, e);
		}
		finally {
			if (tupqres != null) {
				tupqres.close();
			}
		}
		
		return result;
	}
	
	/**
	 * Tels if the bindings of the results are wel-formed.
	 * @param tqr : The result of a SPARQL query.
	 * @return True if the results contain solely both SVAR and OVAR columns.
	 * @throws QueryEvaluationException Error while closing the result.
	 */
	public final boolean hasCorrectBindingNames(TupleQueryResult tqr) throws QueryEvaluationException {
		return tqr.getBindingNames().contains(SVAR)
				&& tqr.getBindingNames().contains(OVAR)
				&& tqr.getBindingNames().size() == 2;
	}
	
	/**
	 * Creates new statements with the updated data.
	 * @return A Hashmap containing statements grouped by their subjects.
	 * @throws QueryEvaluationException Issue with one of the queries.
	 * @throws RepositoryException Repository error while fetching data.
	 * @throws MalformedQueryException Wrong query result bindings.
	 */
	public HashMap<String, LinkedList<Statement>> getInterconnexion() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		HashMap<String, String> sourcedata = getSourceData();
		HashMap<String, LinkedList<String>> cibledata = getCibleData();
		
		HashMap<String, LinkedList<Statement>> maj = new HashMap<String, LinkedList<Statement>>();
		LinkedList<Statement> tmpmaj = new LinkedList<Statement>();
		
		int cpt = 0;
		for (String objet : cibledata.keySet()) {
			if (sourcedata.containsKey(objet)) {
				cpt++;
				for (String sujet : cibledata.get(objet)) {
					
					tmpmaj = maj.get(sujet);
					if (tmpmaj == null) {
						tmpmaj = new LinkedList<Statement>();
						maj.put(sujet, tmpmaj);
					}
					// propcible est la seule propriété pour laquelle on a des triplets.
					tmpmaj.add(new StatementImpl(new URIImpl(sujet), new URIImpl(propcible), new URIImpl(sourcedata.get(objet))));
				}
			}
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Predicate " + propcible + " and " + propsource + " have " + cpt + " common statement(s) in " + nom + ".");
		}
		
		return maj;
	}
	
	/**
	 * Writes a SPARQL query to retrieve subject - object pairs for a given predicate.
	 * @param p : The predicate to use, including its local namespace.
	 * @return La requête SPARQL finale.
	 */
	public String writeQuery(String p) {
		return "SELECT ?" + SVAR + " ?" + OVAR + " "
			+ "WHERE {?" + SVAR + " " + p + " ?" + OVAR + "}" 
			+ (maxliens > 0 ? " LIMIT " + maxliens : "");
	}
	
	/**
	 * Writes a SPARQL query to retrieve subject - object pairs for a given predicate.
	 * The subject will also be of a given type.
	 * @param p : The predicate to use, including its local namespace.
	 * @param t : The subject's data type.
	 * @return La requête SPARQL finale.
	 */
	public String writeQuery(String p, String t) {
		String type = t.equals("") ? "" : "?" + SVAR + " a " + t + " . ";
		return "SELECT ?" + SVAR + " ?" + OVAR + " "
			+ "WHERE {" +  type  +  "?" + SVAR + " " + p + " ?" + OVAR + "}" 
			+ (maxliens > 0 ? " LIMIT " + maxliens : "");
	}
	
	public final String getNom() {
		return nom;
	}
	
	public final String getPropSource() {
		return propsource;
	}
	
	public final String getPropCible() {
		return propcible;
	}
	
	public final String getQuerySource() {
		return querysource;
	}
	
	public final String getQueryCible() {
		return querycible;
	}
	
	public final int getMaxLiens() {
		return maxliens;
	}
	
	/**
	 * Proper stop of both data sets.
	 */
	public void shutdown() {
		cible.shutdown();
		source.shutdown();
	}
}
