package prototype;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Classe abstraite permettant de gérer un jeu de données quelle que soit sa provenance.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see RepositoryConnection
 */
public abstract class Jeu {
	
	/**
	 * Nom du jeu pour affichage.
	 */
	protected String nom;
	/** 
	 * Dépôt utilisé pour les données du jeu.
	 */
	protected Repository rep;
	/**
	 * Connexion vers le dépôt du jeu.
	 */
	protected RepositoryConnection con;
	
	/**
	 * Historique des requêtes passées vers le jeu.
	 */
	protected LinkedList<String> queries;
	
	/**
	 * Ajoute un namespace dans le dépôt.
	 * @param label : le préfixe du namespace.
	 * @param uri : l'URI de l'espace de nom.
	 * @throws RepositoryException
	 */
	public final void addNamespace(String label, String uri) throws RepositoryException {
		con.setNamespace(label, uri);
	}
	
	/**
	 * Récupère un espace de nom selon son préfixe.
	 * @param pre : le préfixe du namespace.
	 * @return L'espace de nom sous forme de chaîne de caractères.
	 * @throws RepositoryException
	 */
	public final String getNamespace(String pre) throws RepositoryException {
		return con.getNamespace(pre);
	}
	
	/**
	 * Supprime tous les namespaces du jeu.
	 * @throws RepositoryException
	 */
	public final void razNamespaces() throws RepositoryException {
		con.clearNamespaces();
	}
	
	/**
	 * Retourne tous les namespaces sous forme de liste.
	 * @return Les namespaces du jeu sous forme de liste.
	 * @throws RepositoryException
	 */
	public final List<Namespace> getNamespaceList() throws RepositoryException {
		return con.getNamespaces().asList();
	}
	
	/**
	 * Formate les espaces de noms pour utilisation dans une requête.
	 * @return Tous les namespace du dépôt sous forme de string pour être utilisé par une requête.
	 * @throws RepositoryException
	 */
	public String getPrefixes() throws RepositoryException {
		String res = "";
		List<Namespace> ns = getNamespaceList();
		for (Namespace n : ns) {
			res += "PREFIX " + n.getPrefix() + ": <" + n.getName() + "> "; 
		}
		return res;
	}
	
	/**
	 * Envoie une requête de sélection sur le jeu et retourne le résultat.
	 * @param query La requête SPARQL sans les PREFIX.
	 * @return Résultat de la requête.
	 * @throws Exception
	 */
	public TupleQueryResult SPARQLQuery(String query) throws Exception {
		System.out.println("Requête " + nom + " : " + query);
		
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, getPrefixes() + query);
	    return tq.evaluate();
	}
	
	/**
	 * Envoie une requête de mise à jour et l'exécute.
	 * @param query La requête SPARQL sans les PREFIX.
	 * @throws Exception
	 */
	public void updateQuery(String query) throws Exception {
		System.out.println("Requête " + nom + " : " + query);
		
		// Ajout de la requête brute à l'historique puis ajout des PREFIX dans la requête finale.
		queries.add(query);
		Update up = con.prepareUpdate(QueryLanguage.SPARQL, getPrefixes() + query);
	    up.execute();
	}
	
	/**
	 * Renvoie la dernière requête effectuée sur le jeu.
	 * @return Une requête sous forme textuelle.
	 */
	public final String getLastQuery() {
		return queries.getLast(); 
	}
	
	/**
	 * Renvoie toutes les requêtes qui ont été faites sur le jeu.
	 * @return Une liste de requêtes.
	 */
	public final LinkedList<String> getQueries() {
		return queries;
	}
	
	/**
	 * Donne tous les triplets du dépôt.
	 * @return L'ensemble des triplets du dépôt.
	 * @throws RepositoryException
	 */
	public final LinkedList<Statement> getAllStatements() throws RepositoryException {
		return new LinkedList<Statement>(con.getStatements(null, null, null, true).asList());
	}
	
	/**
	 * Donne les triplets correspondant à des critères.
	 * @param r : Le sujet qui nous intéresse.
	 * @param u : Le prédicat qui nous intéresse.
	 * @return Les triplets ayant comme sujet r et comme prédicat u.
	 * @throws RepositoryException
	 */
	public final LinkedList<Statement> getAllStatements(Resource r, URI u) throws RepositoryException {
		return new LinkedList<Statement>(con.getStatements(r, u, null, true).asList());
	}
	
	/**
	 * Ajoute un ensemble de triplets au dépôt.
	 * @param sts : Les triplets à ajouter.
	 * @throws RepositoryException
	 */
	public final void addAllStatements(Iterable<Statement> sts) throws RepositoryException {
		con.add(sts);
	}
	
	/**
	 * Ajoute un triplet au dépôt.
	 * @param s : Le triplet.
	 * @throws RepositoryException
	 */
	public final void addAllStatements(Statement s) throws RepositoryException {
		con.add(s);
	}
	
	/**
	 * Retire un ensemble de triplets du dépôt.
	 * @param r : Le sujet qui nous intéresse.
	 * @param u : Le prédicat qui nous intéresse.
	 * @throws RepositoryException
	 */
	public final void removeStatements(Resource r, URI u) throws RepositoryException {
		con.remove(r, u, null);
	}
	
	/**
	 * Retourne le nom du jeu.
	 * @return Le nom du jeu.
	 */
	public final String getNom() {
		return nom;
	}
	
	/**
	 * Arrêt propre de la connexion puis du dépôt.
	 */
	public final void shutdown() {
		try {
			con.close();
			rep.shutDown();
			
			System.out.println("Connexion " + nom + " " + (con.isOpen() ? "toujours en cours" : "terminée") + ".");
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
