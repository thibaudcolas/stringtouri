package prototype;

import java.util.*;

import org.openrdf.model.Namespace;
import org.openrdf.query.*;
import org.openrdf.repository.*;

/**
 * Classe abstraite permettant de gérer un jeu de données quelle que soit sa provenance.
 * 
 * @author Thibaud Colas
 * @version 16032012
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
	 * Envoie une requête sur le jeu et retourne le résultat.
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
	
	public String getLastQuery() {
		return queries.getLast(); 
	}
	
	public LinkedList<String> getQueries() {
		return queries;
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
