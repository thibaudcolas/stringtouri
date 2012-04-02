package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;


/**
 * Abstract class interlinking two data sets according to criteria.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Jeu
 */
public abstract class Liaison {
	
	/**
	 * Linkage name for display purposes.
	 */
	protected String nom;
	/**
	 * Source data for new links.
	 */
	protected Jeu source;
	/**
	 * Target data for new links.
	 */
	protected Jeu cible;
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
	
	public static final String SVAR = "s";
	public static final String PVAR = "p";
	public static final String OVAR = "o";
	
	private static final int DEFSIZE = 100;
	
	/**
	 * Retrieves data for the source predicate from the source data set.
	 * One value = one URI, one URI = one value.
	 * @return HashMap where the key is the value for the source predicate and value is the associated URI (subject).
	 */
	public HashMap<String, String> getSourceData() {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, String> result = new HashMap<String, String>(DEFSIZE + maxliens);
		TupleQueryResult tupqres;
		BindingSet bs;
		
		try {
			tupqres = source.SPARQLQuery(querysource);
			
			if (!hasCorrectBindingNames(tupqres)) {
				throw new Exception("Bindings de la requête incorrects");
			}
			
			int cpt = 0;
			// Pour toutes les lignes de résultat.
			while (tupqres.hasNext()) {
				cpt++;
				bs = tupqres.next();
				result.put(bs.getValue(OVAR).stringValue(), bs.getValue(SVAR).stringValue());
			}
			tupqres.close();
			System.out.println(cpt + " résultat(s).");
		}
		catch (Exception e) {
			System.err.println("Liaison " + nom + " - Erreur dans la sélection source : " + e);
		}
		
		return result;
	}
	
	/**
	 * Retrieves data from the target data set which we want to update.
	 * One value is associated with multiple URIs, one URI can be associated with multiple values.
	 * @return Hashmap where a value is associated with the URIs having this value for the target predicate.
	 */
	public HashMap<String, LinkedList<String>> getCibleData() {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>(DEFSIZE + maxliens);
		TupleQueryResult tupqres;
		BindingSet bs;
		String obj;
		LinkedList<String> subjects;
		
		try {
			tupqres = cible.SPARQLQuery(querycible);
			
			if (!hasCorrectBindingNames(tupqres)) {
				throw new Exception("Bindings de la requête incorrects");
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
			tupqres.close();
			System.out.println(cpt + " résultat(s).");
		}
		catch (Exception e) {
			System.err.println("Liaison " + nom + " - Erreur dans la sélection cible : " + e);
		}
		
		return result;
	}
	
	/**
	 * Tels if the bindings of the results are wel-formed.
	 * @param tqr : The result of a SPARQL query.
	 * @return True if the results contain solely both SVAR and OVAR columns.
	 */
	public final boolean hasCorrectBindingNames(TupleQueryResult tqr) {
		return tqr.getBindingNames().contains(SVAR)
			&& tqr.getBindingNames().contains(OVAR)
			&& tqr.getBindingNames().size() == 2;
	}
	
	/**
	 * Creates new statements with the updated data.
	 * @return A Hashmap containing statements grouped by their subjects.
	 */
	public HashMap<String, LinkedList<Statement>> getInterconnexion() {
		HashMap<String, String> sourcedata = getSourceData();
		HashMap<String, LinkedList<String>> cibledata = getCibleData();
		
		HashMap<String, LinkedList<Statement>> maj = new HashMap<String, LinkedList<Statement>>();
		LinkedList<Statement> tmpmaj = new LinkedList<Statement>();
		
		for (String objet : cibledata.keySet()) {
			if (sourcedata.containsKey(objet)) {
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
