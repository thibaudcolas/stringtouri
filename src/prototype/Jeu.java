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
import org.openrdf.repository.RepositoryResult;

/**
 * Classe abstraite permettant de gérer un jeu de données quelle que soit sa provenance.
 * 
 * @author Thibaud Colas
 * @version 29032012
 * @see Repository, RepositoryConnection, JeuEphemere, JeuSesame, JeuSPARQL
 */
public abstract class Jeu {
	
	protected String nom;
	protected Repository rep;
	protected RepositoryConnection con;
	
	protected LinkedList<String> queries;
	
	/**
	 * Ajoute un namespace dans le dépôt.
	 * @param label : le préfixe du namespace.
	 * @param uri : l'URI de l'espace de nom.
	 * @throws RepositoryException
	 */
	public void addNamespace(String label, String uri) throws RepositoryException {
		con.setNamespace(label, uri);
	}
	
	public String getNamespace(String pre) throws RepositoryException {
		return con.getNamespace(pre);
	}
	
	public void razNamespaces() throws RepositoryException {
		con.clearNamespaces();
	}
	
	public List<Namespace> getNamespaceList() throws RepositoryException {
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
		for(Namespace n : ns) {
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
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, getPrefixes() + query);
	    return tupleQuery.evaluate();
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
	
	public String getLastQuery() {
		return queries.getLast(); 
	}
	
	public LinkedList<String> getQueries() {
		return queries;
	}
	
	/**
	 * Donne tous les triplets du dépôt.
	 * @return L'ensemble des triplets du dépôt.
	 * @throws RepositoryException
	 */
	public LinkedList<Statement> getAllStatements() throws RepositoryException {
		return new LinkedList<Statement>(con.getStatements(null, null, null, true).asList());
	}
	//TODO types de retour différents
	/**
	 * Donne les triplets correspondant à des critères.
	 * @return Les triplets ayant comme sujet r et comme prédicat u.
	 * @throws RepositoryException
	 */
	public RepositoryResult<Statement> getAllStatements(Resource r, URI u) throws RepositoryException {
		return con.getStatements(r, u, null, true);
	}
	
	/**
	 * Ajoute un ensemble de triplets au dépôt.
	 * @param sts : Les triplets à ajouter.
	 * @throws RepositoryException
	 */
	public void addAllStatements(Iterable<Statement> sts) throws RepositoryException {
		con.add(sts);
	}
	
	/**
	 * Retire un ensemble de triplets du dépôt.
	 * @param sts : Les triplets à retirer.
	 * @throws RepositoryException
	 */
	public void removeAllStatements(Iterable<Statement> sts) throws RepositoryException {
		con.remove(sts);
	}
	
	public void rollBack() throws RepositoryException {
		con.rollback();
	}
	
	public String getNom() {
		return nom;
	}
	
	/**
	 * Arrêt propre de la connexion puis du dépôt.
	 */
	public void shutdown() {
		try {
			con.close();
			rep.shutDown();
			
			System.out.println("Connexion " + nom + " " + (con.isOpen() ? "toujours en cours" : "terminée") + ".");
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
