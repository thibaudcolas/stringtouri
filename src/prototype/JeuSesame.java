package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * Classe de gestion d'un jeu provenant d'un dépôt préexistant sur un serveur SESAME.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see HTTPRepository
 */
public class JeuSesame extends Jeu {
	
	/**
	 * L'URL du serveur SESAME utilisé.
	 */
	private String sesame;
	/**
	 * Le nom du dépôt auquel on accède sur le serveur.
	 */
	private String depot;
	
	/**
	 * Constructeur fainéant.
	 * @param sd : Adresse directe vers le dépôt sur le serveur SESAME.
	 */
	JeuSesame(String sd) {
		try {	
			nom = sd;
			sesame = sd;
			depot = sd;
			rep = new HTTPRepository(sd);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur classique.
	 * @param s : URL du serveur SESAME.
	 * @param d : Identifiant du dépôt.
	 */
	JeuSesame(String s, String d) {
		try {	
			nom = s + " - " + d;
			sesame = s;
			depot = d;
			rep = new HTTPRepository(sesame, depot);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Renvoie l'URL du serveur SESAME.
	 * @return L'URL du serveur.
	 */
	public final String getURLSesame() {
		return sesame;
	}
	
	/**
	 * Renvoie l'identifiant du dépôt sur le serveur.
	 * @return L'identifiant du dépôt.
	 */
	public final String getIdDepot() {
		return depot;
	}
}
