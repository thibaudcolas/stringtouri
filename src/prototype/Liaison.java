package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;


/**
 * Classe abstraite réalisant l'interconnexion entre deux jeux de données selon différents critères.
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see Jeu, LiaisonSimple, LiaisonTypee, LiaisonLibre
 */
public abstract class Liaison {
	
	protected String nom;
	
	protected Jeu source;
	protected Jeu cible;
	
	protected String propsource;
	protected String propcible;
	
	protected String querysource;
	protected String querycible;
	
	protected int maxliens;
	
	/**
	 * Les ?s ?p ?o utilisés dans les requêtes.
	 */
	public static final String svar = "s";
	public static final String pvar = "p";
	public static final String ovar = "o";
	
	/**
	 * Renvoie les données du jeu "référence" : celui d'où proviennent les données interconnectées.
	 * Une valeur = une URI, une URI = une valeur.
	 * @return HashMap clef = la valeur de notre propriété source, valeur = l'URI associée.
	 */
	public HashMap<String, String> getSourceData() {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, String> result = new HashMap<String, String>(100 + maxliens);
		TupleQueryResult tupqres;
		BindingSet bs;
		
		try {
			tupqres = source.SPARQLQuery(querysource);
			
			if (!hasCorrectBindingNames(tupqres)) throw new Exception("Bindings de la requête incorrects");
			
			// Pour toutes les lignes de résultat.
			while (tupqres.hasNext()) {
				bs = tupqres.next();
				result.put(bs.getValue(ovar).stringValue(), bs.getValue(svar).stringValue());
			}
			tupqres.close();
		}
		catch (Exception e) {
			System.err.println("Liaison " + nom + " - Erreur dans la sélection source : " + e);
		}
		
		return result;
	}
	
	/**
	 * Renvoie les données du jeu où on veut réaliser l'interconnexion.
	 * Une valeur = plusieurs URI, une URI = plusieurs valeurs.
	 * @return Association entre une valeur de la propriété cible et les URI des objets qui ont cette valeur pour cette propriété.
	 */
	public HashMap<String, LinkedList<String>> getCibleData() {
		// Si maxliens = 0, dimensionnement par défaut. Sinon, dimensionnement plus optimal.
		HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>(100 + maxliens);
		TupleQueryResult tupqres;
		BindingSet bs;
		String obj;
		LinkedList<String> subjects;
		
		try {
			tupqres = cible.SPARQLQuery(querycible);
			
			if (!hasCorrectBindingNames(tupqres)) throw new Exception("Bindings de la requête incorrects");
			
			// Pour toutes les lignes de résultat.
			while (tupqres.hasNext()) {
				bs = tupqres.next();
				obj = bs.getValue(ovar).stringValue();
				
				// Si la valeur est déjà présente dans le jeu, on prend les URI associées et on va en rajouter une.
				// Si la valeur n'est pas encore référencée, on ajoute l'URI de l'objet qui l'utilise.
				if (result.containsKey(obj)) 
					subjects = new LinkedList<String>(result.get(obj));
				else subjects = new LinkedList<String>();
				
				subjects.add(bs.getValue(svar).stringValue());
				result.put(obj, subjects);
				
			}
			tupqres.close();
		}
		catch (Exception e) {
			System.err.println("Liaison " + nom + " - Erreur dans la sélection cible : " + e);
		}
		
		return result;
	}
	
	/**
	 * Dit si les résultats passés en paramètre ont des bindings bien formés.
	 * @param tqr : Résultat d'une requête.
	 * @return Vrai si le résultat est structuré avec svar et ovar uniquement.
	 */
	public boolean hasCorrectBindingNames(TupleQueryResult tqr) {
		return tqr.getBindingNames().contains(svar)
			&& tqr.getBindingNames().contains(ovar)
			&& tqr.getBindingNames().size() == 2;
	}
	
	/**
	 * Créé les nouveaux triplets contenant les données à jour.
	 * @return Une liste chaînée de Statement correctement modifiés.
	 */
	public LinkedList<Statement> getNewStatements() {
		HashMap<String, String> sourcedata = getSourceData();
		HashMap<String, LinkedList<String>> cibledata = getCibleData();
		
		LinkedList<Statement> maj = new LinkedList<Statement>();
		
		for (String objet : cibledata.keySet()) {
			if(sourcedata.containsKey(objet)) {
				for (String sujet : cibledata.get(objet)) {
					maj.add(new StatementImpl(new URIImpl(sujet), new URIImpl(propcible), new URIImpl(sourcedata.get(objet))));
				}
			}
		}
		return maj;
	}
	
	/**
	 * Écrit une requête SPARQL qui récupère les couples sujet - objet pour la propriété qui nous intéresse.
	 * @param p : La propriété à utiliser, sous la forme namespace:propriété.
	 * @return La requête SPARQL finale.
	 */
	public String writeQuery(String p) {
		return "SELECT ?"+svar+" ?"+ovar+" "
			+ "WHERE {?"+svar+" "+p+" ?"+ovar+"}" 
			+ (maxliens > 0 ? " LIMIT " + maxliens : "");
	}
	
	/**
	 * Écrit une requête SPARQL qui récupère les couples sujet - objet pour la propriété qui nous intéresse.
	 * Le sujet sera du type passé en paramètre.
	 * @param p : La propriété à utiliser, sous la forme namespace:propriété.
	 * @param t : Le type du sujet.
	 * @return La requête SPARQL finale.
	 */
	public String writeQuery(String p, String t) {
		String type = t.equals("") ? "" : "?"+svar+" a "+t+" . ";
		return "SELECT ?"+svar+" ?"+ovar+" "
			+ "WHERE {"+ type + "?"+svar+" "+p+" ?"+ovar+"}" 
			+ (maxliens > 0 ? " LIMIT " + maxliens : "");
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getPropSource() {
		return propsource;
	}
	
	public String getPropCible() {
		return propcible;
	}
	
	public String getQuerySource() {
		return querysource;
	}
	
	public String getQueryCible() {
		return querycible;
	}
	
	public int getMaxLiens() {
		return maxliens;
	}
	
	/**
	 * Arrêt propre des jeux source et cible.
	 */
	public void shutdown() {
		cible.shutdown();
		source.shutdown();
	}
}
