package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * Classe de gestion d'un jeu provenant d'un dépôt préexistant sur un serveur SESAME.
 * 
 * @author Thibaud Colas
 * @version 16032012
 * @see HTTPRepository, RepositoryConnection
 */
public class JeuSPARQL extends Jeu {
	
	private String endpoint;
	
	/**
	 * Constructeur classique.
	 * @param ep : URL du SPARQL Endpoint où l'on veut se connecter.
	 */
	JeuSPARQL(String ep) {
		try {	
			nom = ep;
			endpoint = ep;
			rep = new SPARQLRepository(ep);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	public String getEndPoint() {
		return endpoint;
	}
}
