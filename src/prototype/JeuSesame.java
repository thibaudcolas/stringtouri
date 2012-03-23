package prototype;

import java.util.*;

import org.openrdf.repository.*;
import org.openrdf.repository.http.HTTPRepository;

/**
 * Classe de gestion d'un jeu provenant d'un dépôt préexistant sur un serveur SESAME.
 * 
 * @author Thibaud Colas
 * @version 16032012
 * @see HTTPRepository, RepositoryConnection
 */
public class JeuSesame extends Jeu {
	
	private String sesame;
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
	
	public String getURLSesame() {
		return sesame;
	}
	
	public String getIdDepot() {
		return depot;
	}
}
